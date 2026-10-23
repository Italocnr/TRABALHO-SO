package fumantes;

import java.util.concurrent.ThreadLocalRandom;

import util.Log;

/** Representante de cigarros: tem estoque infinito dos três ingredientes. */
public class Representante extends Thread {
    private final Mesa mesa;
    private final int rodadas;

    public Representante(Mesa mesa, int rodadas) {
        super("Representante");
        this.mesa = mesa;
        this.rodadas = rodadas;
    }

    @Override
    public void run() {
        Ingrediente[] todos = Ingrediente.values();
        try {
            for (int r = 1; r <= rodadas; r++) {
                // Escolhe aleatoriamente o ingrediente que NÃO vai à mesa; os outros dois vão
                Ingrediente faltante = todos[ThreadLocalRandom.current().nextInt(3)];
                Ingrediente a = todos[(faltante.ordinal() + 1) % 3];
                Ingrediente b = todos[(faltante.ordinal() + 2) % 3];
                Log.info("Rodada " + r);
                mesa.colocarIngredientes(a, b, faltante);
            }
            Log.info("Fim das rodadas: avisa os fumantes para irem embora");
            mesa.encerrar();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
