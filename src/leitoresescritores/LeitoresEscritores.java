package leitoresescritores;

import java.util.ArrayList;
import java.util.List;

import util.Log;
import util.Verificador;

public class LeitoresEscritores {

    public static Verificador executar(int numLeitores, int numEscritores, int operacoes, int tempoMaxMs)
            throws InterruptedException {
        Verificador v = new Verificador("Leitores e Escritores");
        MemoriaCompartilhada mem = new MemoriaCompartilhada(v);

        List<Thread> threads = new ArrayList<>();
        for (int i = 1; i <= numEscritores; i++) {
            threads.add(new Escritor(i, mem, operacoes, tempoMaxMs));
        }
        for (int i = 1; i <= numLeitores; i++) {
            threads.add(new Leitor(i, mem, operacoes, tempoMaxMs, v));
        }
        threads.forEach(Thread::start);
        v.aguardarTermino(15_000, threads);

        int soma = 0;
        for (int i = 0; i < mem.tamanho(); i++) {
            soma += mem.ler(i);
        }
        v.checar(soma == MemoriaCompartilhada.SOMA_TOTAL, "Soma final de M = " + soma);

        v.resumo("Leitores=" + numLeitores + ", escritores=" + numEscritores + ", operações por thread=" + operacoes);
        v.resumo("M final=" + mem.conteudo() + ", máximo de leitores simultâneos="
                + mem.getMaxLeitoresSimultaneos() + " (prova que leituras ocorrem em paralelo)");
        return v;
    }

    public static void main(String[] args) throws InterruptedException {
        Log.titulo("LEITORES E ESCRITORES");
        Log.reiniciarRelogio();
        executar(3, 2, 4, 120).imprimir();
    }
}
