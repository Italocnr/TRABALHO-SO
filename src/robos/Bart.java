package robos;

public class Bart extends Thread {
    private final Controle c;
    private final int ciclos;

    public Bart(Controle c, int ciclos) {
        super("Bart");
        this.c = c;
        this.ciclos = ciclos;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < ciclos; i++) {
                c.semBart.acquire();   // espera a sua vez
                c.move("Bart");
                c.semLisa.release();   // Bart -> Lisa
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
