package prodconssemaforo;

import java.util.Set;

import util.Log;
import util.Tempo;
import util.Verificador;

public class Consumidor extends Thread {
    private final BufferLimitado buffer;
    private final int quantidade;
    private final Set<Integer> consumidos;
    private final Verificador verificador;
    private final int tempoMaxMs;

    public Consumidor(int id, BufferLimitado buffer, int quantidade, Set<Integer> consumidos,
            Verificador verificador, int tempoMaxMs) {
        super("Consumidor-" + id);
        this.buffer = buffer;
        this.quantidade = quantidade;
        this.consumidos = consumidos;
        this.verificador = verificador;
        this.tempoMaxMs = tempoMaxMs;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < quantidade; i++) {
                int item = buffer.remover();
                verificador.checar(consumidos.add(item), "Item " + item + " consumido duas vezes");
                Tempo.dormirAleatorio(0, tempoMaxMs);
            }
            Log.info("terminou de consumir " + quantidade + " itens");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
