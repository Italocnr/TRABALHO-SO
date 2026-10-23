package prodconssemaforo;

import java.util.concurrent.Semaphore;

import util.Log;
import util.Verificador;

/**
 * RECURSO COMPARTILHADO: vetor circular de tamanho fixo (bounded buffer).
 *
 * Semáforos:
 *  - vazios (inicia = capacidade): conta posições livres. Produtor faz down; consumidor faz up.
 *  - cheios (inicia = 0)         : conta posições ocupadas. Consumidor faz down; produtor faz up.
 *  - mutex  (inicia = 1)         : exclusão mútua sobre itens/entrada/saida/ocupados.
 */
public class BufferLimitado {
    private final int[] itens;
    private int entrada = 0;
    private int saida = 0;
    private int ocupados = 0;

    private final Semaphore vazios;
    private final Semaphore cheios = new Semaphore(0);
    private final Semaphore mutex = new Semaphore(1);

    private final Verificador verificador;

    public BufferLimitado(int capacidade, Verificador verificador) {
        this.itens = new int[capacidade];
        this.vazios = new Semaphore(capacidade);
        this.verificador = verificador;
    }

    public void inserir(int item) throws InterruptedException {
        vazios.acquire();   // down(vazios): bloqueia se o buffer estiver cheio
        mutex.acquire();    // down(mutex): entra na região crítica
        try {
            // ---------- REGIÃO CRÍTICA ----------
            itens[entrada] = item;
            entrada = (entrada + 1) % itens.length;
            ocupados++;
            verificador.checar(ocupados <= itens.length, "Buffer estourou: ocupados=" + ocupados);
            Log.info("put " + item + "   -> buffer " + ocupados + "/" + itens.length);
        } finally {
            mutex.release(); // up(mutex): sai da região crítica
        }
        cheios.release();   // up(cheios): avisa que existe um item novo
    }

    public int remover() throws InterruptedException {
        cheios.acquire();   // down(cheios): bloqueia se o buffer estiver vazio
        mutex.acquire();
        int item;
        try {
            // ---------- REGIÃO CRÍTICA ----------
            item = itens[saida];
            saida = (saida + 1) % itens.length;
            ocupados--;
            verificador.checar(ocupados >= 0, "Buffer negativo: ocupados=" + ocupados);
            Log.info("get " + item + "   -> buffer " + ocupados + "/" + itens.length);
        } finally {
            mutex.release();
        }
        vazios.release();   // up(vazios): libera uma posição para os produtores
        return item;
    }

    /** Lido apenas depois que todas as threads terminaram. */
    public int getOcupados() {
        return ocupados;
    }
}
