package prodconssemaforo;

import util.Log;
import util.Tempo;

public class Produtor extends Thread {
    private final BufferLimitado buffer;
    private final int id;
    private final int quantidade;
    private final int tempoMaxMs;

    public Produtor(int id, BufferLimitado buffer, int quantidade, int tempoMaxMs) {
        super("Produtor-" + id);
        this.id = id;
        this.buffer = buffer;
        this.quantidade = quantidade;
        this.tempoMaxMs = tempoMaxMs;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < quantidade; i++) {
                int item = id * 1000 + i; // item único: identifica produtor e sequência
                buffer.inserir(item);
                Tempo.dormirAleatorio(0, tempoMaxMs);
            }
            Log.info("terminou de produzir " + quantidade + " itens");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
