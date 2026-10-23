package fumantes;

import util.Log;
import util.Tempo;

public class Fumante extends Thread {
    private final Mesa mesa;
    private final Ingrediente meuIngrediente;
    private final int tempoMaxMs;
    private int cigarrosFumados = 0;

    public Fumante(Ingrediente meuIngrediente, Mesa mesa, int tempoMaxMs) {
        super("Fumante-" + meuIngrediente);
        this.meuIngrediente = meuIngrediente;
        this.mesa = mesa;
        this.tempoMaxMs = tempoMaxMs;
    }

    @Override
    public void run() {
        try {
            Log.info("Tenho apenas " + meuIngrediente + ", fico bloqueado esperando a mesa");
            while (mesa.pegarIngredientes(meuIngrediente)) {
                Log.info("Fez o cigarro e está FUMANDO no fumódromo");
                Tempo.dormirAleatorio(tempoMaxMs / 2, tempoMaxMs);
                cigarrosFumados++;
                Log.info("Terminou de fumar -> desbloqueia o representante");
                mesa.terminarDeFumar();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public int getCigarrosFumados() {
        return cigarrosFumados;
    }
}
