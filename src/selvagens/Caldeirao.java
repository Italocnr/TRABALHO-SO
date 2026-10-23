package selvagens;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import util.Log;
import util.Tempo;
import util.Verificador;

/**
 * RECURSO COMPARTILHADO: o caldeirão com 'porcoes' porções (capacidade N).
 *
 * Semáforos:
 *  - mutex (inicia 1)          : só um selvagem se serve por vez (região crítica do caldeirão).
 *  - caldeiraoVazio (inicia 0) : o cozinheiro dorme nele; o selvagem que encontra o caldeirão
 *                                vazio faz up para acordá-lo.
 *  - caldeiraoCheio (inicia 0) : o selvagem espera nele enquanto o cozinheiro enche;
 *                                o cozinheiro faz up ao terminar.
 * O selvagem que encontrou o caldeirão vazio espera o cozinheiro SEGURANDO o mutex:
 * assim nenhum outro selvagem tenta se servir de um caldeirão vazio, e o cozinheiro
 * só enche quando porcoes == 0 (e ninguém mais mexe no caldeirão enquanto ele enche).
 */
public class Caldeirao {
    private final int capacidade;
    private int porcoes;

    private final Semaphore mutex = new Semaphore(1);
    private final Semaphore caldeiraoVazio = new Semaphore(0);
    private final Semaphore caldeiraoCheio = new Semaphore(0);

    private final int tempoMaxMs;
    private final Verificador verificador;
    private final AtomicInteger servindoAgora = new AtomicInteger();
    private final AtomicInteger porcoesServidas = new AtomicInteger();
    private final AtomicInteger recargas = new AtomicInteger();

    public Caldeirao(int capacidade, int tempoMaxMs, Verificador verificador) {
        this.capacidade = capacidade;
        this.porcoes = capacidade; // o jantar começa com o caldeirão cheio
        this.tempoMaxMs = tempoMaxMs;
        this.verificador = verificador;
    }

    /** Chamado pelo SELVAGEM. */
    public void servir() throws InterruptedException {
        mutex.acquire();                         // down(mutex)
        try {
            // ---------- REGIÃO CRÍTICA ----------
            verificador.checar(servindoAgora.incrementAndGet() == 1, "Dois selvagens se servindo ao mesmo tempo");
            if (porcoes == 0) {
                Log.info("Caldeirão VAZIO -> acorda o cozinheiro e espera");
                caldeiraoVazio.release();        // up(caldeiraoVazio): acorda o cozinheiro
                caldeiraoCheio.acquire();        // down(caldeiraoCheio): espera encher
            }
            verificador.checar(porcoes > 0, "Selvagem se serviu de caldeirão vazio");
            porcoes--;
            porcoesServidas.incrementAndGet();
            Log.info("Serviu-se de uma porção (restam " + porcoes + "/" + capacidade + ")");
        } finally {
            servindoAgora.decrementAndGet();
            mutex.release();                     // up(mutex)
        }
    }

    /** Chamado pelo COZINHEIRO em laço. */
    public void encher() throws InterruptedException {
        Log.info("Cozinheiro DORMINDO");
        caldeiraoVazio.acquire();                // down(caldeiraoVazio): dorme até ser acordado
        verificador.checar(porcoes == 0, "Cozinheiro encheu caldeirão que não estava vazio: " + porcoes);
        Log.info("Cozinheiro acordou e está cozinhando " + capacidade + " porções");
        Tempo.dormirAleatorio(tempoMaxMs / 2, tempoMaxMs);
        porcoes = capacidade;
        recargas.incrementAndGet();
        Log.info("Caldeirão CHEIO");
        caldeiraoCheio.release();                // up(caldeiraoCheio): libera o selvagem que espera
    }

    public int getPorcoesServidas() {
        return porcoesServidas.get();
    }

    public int getRecargas() {
        return recargas.get();
    }
}
