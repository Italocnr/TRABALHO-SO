package fumantes;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import util.Log;
import util.Verificador;

/**
 * RECURSO COMPARTILHADO: a mesa onde o representante coloca dois ingredientes.
 *
 * Semáforos:
 *  - fumante[TABACO], fumante[PAPEL], fumante[FOSFORO] (iniciam 0):
 *      cada fumante fica bloqueado no semáforo do ingrediente que ELE POSSUI.
 *      O representante faz up apenas no semáforo do fumante que tem o ingrediente que falta.
 *  - representante (inicia 0): o representante bloqueia nele depois de servir a mesa;
 *      o fumante faz up quando termina de fumar.
 * Como só uma thread por vez tem a "vez" (alternância garantida pelos semáforos),
 * a mesa nunca é acessada por duas threads ao mesmo tempo.
 */
public class Mesa {
    private final List<Ingrediente> ingredientesNaMesa = new ArrayList<>();
    private final Semaphore[] fumante = {new Semaphore(0), new Semaphore(0), new Semaphore(0)};
    private final Semaphore representante = new Semaphore(0);
    private volatile boolean encerrado = false;

    private final AtomicInteger acessandoMesa = new AtomicInteger();
    private final AtomicInteger fumandoAgora = new AtomicInteger();
    private final Verificador verificador;

    public Mesa(Verificador verificador) {
        this.verificador = verificador;
    }

    /** Chamado pelo REPRESENTANTE: coloca dois ingredientes, acorda o fumante certo e bloqueia. */
    public void colocarIngredientes(Ingrediente a, Ingrediente b, Ingrediente faltante) throws InterruptedException {
        verificador.checar(acessandoMesa.incrementAndGet() == 1, "Mesa acessada por duas threads");
        verificador.checar(ingredientesNaMesa.isEmpty(), "Representante colocou ingredientes sobre outros");
        ingredientesNaMesa.add(a);
        ingredientesNaMesa.add(b);
        Log.info("Colocou " + a + " + " + b + " na mesa -> acorda o fumante que tem " + faltante);
        acessandoMesa.decrementAndGet();

        fumante[faltante.ordinal()].release();   // up(fumante[faltante])
        representante.acquire();                 // down(representante): bloqueia até o cigarro acabar
    }

    /** Chamado pelo FUMANTE: bloqueia até receber ingredientes. Retorna false no fim da simulação. */
    public boolean pegarIngredientes(Ingrediente meu) throws InterruptedException {
        fumante[meu.ordinal()].acquire();        // down(fumante[meu])
        if (encerrado) {
            return false;
        }
        verificador.checar(acessandoMesa.incrementAndGet() == 1, "Mesa acessada por duas threads");
        verificador.checar(ingredientesNaMesa.size() == 2 && !ingredientesNaMesa.contains(meu)
                && !ingredientesNaMesa.get(0).equals(ingredientesNaMesa.get(1)),
                "Fumante com " + meu + " recebeu ingredientes errados: " + ingredientesNaMesa);
        Log.info("Pegou " + ingredientesNaMesa + " da mesa (já tinha " + meu + ")");
        ingredientesNaMesa.clear();
        acessandoMesa.decrementAndGet();
        verificador.checar(fumandoAgora.incrementAndGet() == 1, "Dois fumantes fumando ao mesmo tempo");
        return true;
    }

    /** Chamado pelo FUMANTE quando termina o cigarro: desbloqueia o representante. */
    public void terminarDeFumar() {
        fumandoAgora.decrementAndGet();
        representante.release();                 // up(representante)
    }

    /** Fim da simulação: acorda todos os fumantes para que terminem. */
    public void encerrar() {
        encerrado = true;
        for (Semaphore s : fumante) {
            s.release();
        }
    }
}
