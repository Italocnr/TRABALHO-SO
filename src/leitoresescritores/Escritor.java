package leitoresescritores;

import java.util.concurrent.ThreadLocalRandom;

import util.Log;
import util.Tempo;

public class Escritor extends Thread {
    private final MemoriaCompartilhada mem;
    private final int escritas;
    private final int tempoMaxMs;

    public Escritor(int id, MemoriaCompartilhada mem, int escritas, int tempoMaxMs) {
        super("Escritor-" + id);
        this.mem = mem;
        this.escritas = escritas;
        this.tempoMaxMs = tempoMaxMs;
    }

    @Override
    public void run() {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        try {
            for (int k = 0; k < escritas; k++) {
                Log.info("quer escrever (aguardando acesso exclusivo)");
                mem.iniciarEscrita();
                // ---------- REGIÃO CRÍTICA (acesso exclusivo) ----------
                if (k % 2 == 0) {
                    // M = [a, b, c, d]: escreve um vetor novo, posição por posição
                    int a = rnd.nextInt(MemoriaCompartilhada.SOMA_TOTAL + 1);
                    int b = rnd.nextInt(MemoriaCompartilhada.SOMA_TOTAL - a + 1);
                    int c = rnd.nextInt(MemoriaCompartilhada.SOMA_TOTAL - a - b + 1);
                    int[] novo = {a, b, c, MemoriaCompartilhada.SOMA_TOTAL - a - b - c};
                    for (int i = 0; i < novo.length; i++) {
                        mem.escrever(i, novo[i]);
                        Tempo.dormirAleatorio(0, tempoMaxMs / 4); // estado intermediário inconsistente
                    }
                    Log.info("write M = " + mem.conteudo());
                } else {
                    // M[j] = ...: transfere uma quantidade de uma posição para outra
                    int i = rnd.nextInt(mem.tamanho());
                    int j = (i + 1 + rnd.nextInt(mem.tamanho() - 1)) % mem.tamanho();
                    int qtd = Math.min(rnd.nextInt(1, 4), mem.ler(i)); // não deixa M[i] negativo
                    mem.escrever(i, mem.ler(i) - qtd);
                    Tempo.dormirAleatorio(0, tempoMaxMs / 4);     // estado intermediário inconsistente
                    mem.escrever(j, mem.ler(j) + qtd);
                    Log.info("write M[" + i + "]-=" + qtd + ", M[" + j + "]+=" + qtd + " -> M = " + mem.conteudo());
                }
                mem.terminarEscrita();
                Tempo.dormirAleatorio(0, tempoMaxMs);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
