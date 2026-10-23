package util;

import java.util.concurrent.ThreadLocalRandom;

/** Simula o tempo gasto em uma atividade (cortar cabelo, comer, fumar...). */
public final class Tempo {
    private Tempo() {
    }

    /** Dorme um tempo aleatório entre minMs e maxMs. Com maxMs <= 0 apenas cede a CPU. */
    public static void dormirAleatorio(int minMs, int maxMs) throws InterruptedException {
        if (maxMs <= 0) {
            Thread.yield();
            return;
        }
        Thread.sleep(ThreadLocalRandom.current().nextInt(Math.max(0, minMs), maxMs + 1));
    }
}
