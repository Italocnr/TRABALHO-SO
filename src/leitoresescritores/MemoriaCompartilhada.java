package leitoresescritores;

import java.util.Arrays;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import util.Verificador;

/**
 * RECURSO COMPARTILHADO: o vetor de inteiros M (como na figura do enunciado).
 * Invariante usada nos testes: a soma dos elementos de M é sempre SOMA_TOTAL.
 * Os escritores alteram M em vários passos, então uma leitura concorrente com
 * uma escrita enxergaria uma soma errada.
 *
 * Semáforos (solução justa, sem inanição de escritores):
 *  - fila (inicia 1, FIFO)  : "catraca" por ordem de chegada. Um escritor que chega
 *                             impede que novos leitores passem na frente dele.
 *  - mutexLeitores (inicia 1): exclusão mútua sobre o contador 'leitores'.
 *  - recurso (inicia 1)     : acesso exclusivo a M. É obtido pelo escritor ou
 *                             pelo PRIMEIRO leitor do grupo e liberado pelo ÚLTIMO.
 */
public class MemoriaCompartilhada {
    public static final int SOMA_TOTAL = 20;
    private final int[] m = {3, 7, 1, 9};

    private int leitores = 0; // leitores lendo agora; protegido por mutexLeitores

    private final Semaphore fila = new Semaphore(1, true);
    private final Semaphore mutexLeitores = new Semaphore(1);
    private final Semaphore recurso = new Semaphore(1);

    // Contadores usados apenas para validação
    private final AtomicInteger leitoresAtivos = new AtomicInteger();
    private final AtomicInteger escritoresAtivos = new AtomicInteger();
    private final AtomicInteger maxLeitoresSimultaneos = new AtomicInteger();
    private final Verificador verificador;

    public MemoriaCompartilhada(Verificador verificador) {
        this.verificador = verificador;
    }

    public void iniciarLeitura() throws InterruptedException {
        fila.acquire();                 // respeita a ordem de chegada
        mutexLeitores.acquire();
        leitores++;
        if (leitores == 1) {
            recurso.acquire();          // primeiro leitor tranca M contra escritores
        }
        mutexLeitores.release();
        fila.release();                 // libera a catraca: outros leitores podem entrar juntos

        int ativos = leitoresAtivos.incrementAndGet();
        maxLeitoresSimultaneos.accumulateAndGet(ativos, Math::max);
        verificador.checar(escritoresAtivos.get() == 0, "Leitor entrou com escritor ativo");
    }

    public void terminarLeitura() throws InterruptedException {
        leitoresAtivos.decrementAndGet();
        mutexLeitores.acquire();
        leitores--;
        if (leitores == 0) {
            recurso.release();          // último leitor destranca M
        }
        mutexLeitores.release();
    }

    public void iniciarEscrita() throws InterruptedException {
        fila.acquire();                 // bloqueia a entrada de novos leitores
        recurso.acquire();              // espera os leitores atuais saírem
        fila.release();

        int e = escritoresAtivos.incrementAndGet();
        verificador.checar(e == 1, "Dois escritores ao mesmo tempo");
        verificador.checar(leitoresAtivos.get() == 0, "Escritor entrou com leitores ativos");
    }

    public void terminarEscrita() {
        escritoresAtivos.decrementAndGet();
        recurso.release();
    }

    // Operações sobre M: só podem ser chamadas entre iniciar/terminar (REGIÃO CRÍTICA)
    public int ler(int i) {
        return m[i];
    }

    public void escrever(int i, int valor) {
        m[i] = valor;
    }

    public int tamanho() {
        return m.length;
    }

    public String conteudo() {
        return Arrays.toString(m);
    }

    public int getMaxLeitoresSimultaneos() {
        return maxLeitoresSimultaneos.get();
    }
}
