package barbeiro;

import util.Log;

public class Barbeiro extends Thread {
    private final Barbearia barbearia;

    public Barbeiro(Barbearia barbearia) {
        super("Barbeiro");
        this.barbearia = barbearia;
    }

    @Override
    public void run() {
        try {
            barbearia.barbeiro(); // laço infinito: atende um cliente após o outro
        } catch (InterruptedException e) {
            Log.info("Barbearia fechou: barbeiro vai para casa");
        }
    }
}
