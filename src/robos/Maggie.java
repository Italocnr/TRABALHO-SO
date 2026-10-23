package robos;

public class Maggie extends Thread {
    private final Controle c;
    private final int ciclos;

    public Maggie(Controle c, int ciclos) {
        super("Maggie");
        this.c = c;
        this.ciclos = ciclos;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < ciclos; i++) {
                c.semMaggie.acquire(); // espera a sua vez
                c.move("Maggie");
                c.semLisa.release();   // Maggie -> Lisa
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
