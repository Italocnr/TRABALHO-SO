package selvagens;

import util.Log;

public class Cozinheiro extends Thread {
    private final Caldeirao caldeirao;

    public Cozinheiro(Caldeirao caldeirao) {
        super("Cozinheiro");
        this.caldeirao = caldeirao;
    }

    @Override
    public void run() {
        try {
            while (true) {
                caldeirao.encher();
            }
        } catch (InterruptedException e) {
            Log.info("Fim do jantar: cozinheiro encerra");
        }
    }
}
