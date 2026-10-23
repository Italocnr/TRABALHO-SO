package filosofos;

import util.Log;
import util.Tempo;

public class Filosofo extends Thread {
    private final int id;
    private final Mesa mesa;
    private final int refeicoes;
    private final int tempoMaxMs;
    private int refeicoesFeitas = 0;

    public Filosofo(int id, Mesa mesa, int refeicoes, int tempoMaxMs) {
        super("Filosofo-" + id);
        this.id = id;
        this.mesa = mesa;
        this.refeicoes = refeicoes;
        this.tempoMaxMs = tempoMaxMs;
    }

    @Override
    public void run() {
        try {
            for (int r = 0; r < refeicoes; r++) {
                Log.info("MEDITANDO");
                Tempo.dormirAleatorio(0, tempoMaxMs);

                mesa.pegarPalitos(id);
                Log.info("COMENDO (refeição " + (r + 1) + "/" + refeicoes + ")");
                Tempo.dormirAleatorio(0, tempoMaxMs);
                refeicoesFeitas++;
                mesa.devolverPalitos(id);
            }
            Log.info("Satisfeito, saiu da mesa");
        } catch (InterruptedException e) {
            Log.info("Interrompido (estava travado esperando um palito)");
        }
    }

    public int getRefeicoesFeitas() {
        return refeicoesFeitas;
    }
}
