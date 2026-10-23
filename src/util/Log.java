package util;

/**
 * Impressão padronizada: [tempo desde o início] [nome da thread] mensagem.
 * É synchronized para que linhas de threads diferentes não se misturem.
 */
public final class Log {
    private static volatile boolean ativo = true;
    private static volatile long inicio = System.currentTimeMillis();

    private Log() {
    }

    public static void setAtivo(boolean valor) {
        ativo = valor;
    }

    public static void reiniciarRelogio() {
        inicio = System.currentTimeMillis();
    }

    public static synchronized void info(String mensagem) {
        if (!ativo) {
            return;
        }
        long t = System.currentTimeMillis() - inicio;
        System.out.printf("[%6d ms] [%-12s] %s%n", t, Thread.currentThread().getName(), mensagem);
    }

    public static void titulo(String texto) {
        if (ativo) {
            System.out.println();
            System.out.println("==================== " + texto + " ====================");
        }
    }
}
