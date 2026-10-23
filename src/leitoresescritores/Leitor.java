package leitoresescritores;

import java.util.concurrent.ThreadLocalRandom;

import util.Log;
import util.Tempo;
import util.Verificador;

public class Leitor extends Thread {
    private final MemoriaCompartilhada mem;
    private final int leituras;
    private final int tempoMaxMs;
    private final Verificador verificador;

    public Leitor(int id, MemoriaCompartilhada mem, int leituras, int tempoMaxMs, Verificador verificador) {
        super("Leitor-" + id);
        this.mem = mem;
        this.leituras = leituras;
        this.tempoMaxMs = tempoMaxMs;
        this.verificador = verificador;
    }

    @Override
    public void run() {
        try {
            for (int k = 0; k < leituras; k++) {
                mem.iniciarLeitura();
                // ---------- REGIÃO CRÍTICA (compartilhada com outros leitores) ----------
                if (k % 2 == 0) {
                    // read M: lê o vetor inteiro, devagar, e confere a soma
                    StringBuilder sb = new StringBuilder("[");
                    int soma = 0;
                    for (int i = 0; i < mem.tamanho(); i++) {
                        int x = mem.ler(i);
                        soma += x;
                        sb.append(i == 0 ? "" : ", ").append(x);
                        Tempo.dormirAleatorio(0, tempoMaxMs / 4);
                    }
                    sb.append("]");
                    Log.info("read M = " + sb + "  (soma=" + soma + ")");
                    verificador.checar(soma == MemoriaCompartilhada.SOMA_TOTAL,
                            getName() + " leu M inconsistente " + sb + " soma=" + soma);
                } else {
                    // read M[i]: lê uma única posição
                    int i = ThreadLocalRandom.current().nextInt(mem.tamanho());
                    Log.info("read M[" + i + "] = " + mem.ler(i));
                    Tempo.dormirAleatorio(0, tempoMaxMs / 2);
                }
                mem.terminarLeitura();
                Tempo.dormirAleatorio(0, tempoMaxMs);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
