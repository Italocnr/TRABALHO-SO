package robos;

public class Lisa extends Thread {
    private final Controle c;
    private final int ciclos;

    public Lisa(Controle c, int ciclos) {
        super("Lisa");
        this.c = c;
        this.ciclos = ciclos;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < ciclos; i++) {
                c.semLisa.acquire();   // vez da Lisa (depois do Bart)
                c.move("Lisa");
                c.semMaggie.release(); // Lisa -> Maggie

                c.semLisa.acquire();   // vez da Lisa (depois da Maggie)
                c.move("Lisa");
                c.semBart.release();   // Lisa -> Bart
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
