package barbeiro;

public class Cliente extends Thread {
    private final Barbearia barbearia;
    private final int id;

    public Cliente(int id, Barbearia barbearia) {
        super("Cliente-" + id);
        this.id = id;
        this.barbearia = barbearia;
    }

    @Override
    public void run() {
        try {
            barbearia.cliente(id); // sem laço: cada cliente corta o cabelo uma única vez
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
