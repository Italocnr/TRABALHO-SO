package selvagens;

import java.util.concurrent.atomic.AtomicInteger;

import util.Log;
import util.Tempo;

public class Selvagem extends Thread {
    private final Caldeirao caldeirao;
    private final int refeicoes;
    private final int tempoMaxMs;
    private final AtomicInteger comendoAgora;
    private final AtomicInteger maxComendoJuntos;

    public Selvagem(int id, Caldeirao caldeirao, int refeicoes, int tempoMaxMs,
            AtomicInteger comendoAgora, AtomicInteger maxComendoJuntos) {
        super("Selvagem-" + id);
        this.caldeirao = caldeirao;
        this.refeicoes = refeicoes;
        this.tempoMaxMs = tempoMaxMs;
        this.comendoAgora = comendoAgora;
        this.maxComendoJuntos = maxComendoJuntos;
    }

    @Override
    public void run() {
        try {
            for (int r = 0; r < refeicoes; r++) {
                caldeirao.servir();                       // um por vez (região crítica)
                int n = comendoAgora.incrementAndGet();   // comer acontece FORA da região crítica
                maxComendoJuntos.accumulateAndGet(n, Math::max);
                Log.info("COMENDO (" + n + " selvagens comendo agora)");
                Tempo.dormirAleatorio(0, tempoMaxMs);
                comendoAgora.decrementAndGet();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
