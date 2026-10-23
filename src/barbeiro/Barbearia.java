package barbeiro;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import util.Log;
import util.Tempo;
import util.Verificador;

/**
 * Solução de Tanenbaum para o Barbeiro Dorminhoco.
 *
 * Semáforos clássicos:
 *  - clientes (customers, inicia 0): clientes esperando atendimento. O barbeiro dorme em down(clientes).
 *  - barbeiros (barbers, inicia 0) : barbeiros livres (0 ou 1). O cliente espera em down(barbeiros).
 *  - mutex (inicia 1)              : exclusão mútua sobre a variável 'esperando'.
 * Variável:
 *  - esperando (waiting): cópia de 'clientes' que pode ser lida (não dá para ler o valor de um semáforo).
 *
 * Semáforos de encontro (rendezvous) para o corte em si:
 *  - clienteSentado (inicia 0) : o cliente avisa que sentou na cadeira do barbeiro.
 *  - corteConcluido (inicia 0) : o barbeiro avisa que terminou; só então o cliente sai.
 */
public class Barbearia {
    private final int cadeiras;
    private int esperando = 0;                 // protegido por mutex
    private int maxEsperando = 0;              // protegido por mutex

    private final Semaphore clientes = new Semaphore(0);
    private final Semaphore barbeiros = new Semaphore(0);
    private final Semaphore mutex = new Semaphore(1);
    private final Semaphore clienteSentado = new Semaphore(0);
    private final Semaphore corteConcluido = new Semaphore(0);

    private int clienteNaCadeira = -1;         // escrito pelo cliente antes de up(clienteSentado)

    private final int tempoCorteMaxMs;
    private final Verificador verificador;
    private final Set<Integer> cortados = ConcurrentHashMap.newKeySet();
    private final AtomicInteger atendidos = new AtomicInteger();
    private final AtomicInteger desistentes = new AtomicInteger();

    public Barbearia(int cadeiras, int tempoCorteMaxMs, Verificador verificador) {
        this.cadeiras = cadeiras;
        this.tempoCorteMaxMs = tempoCorteMaxMs;
        this.verificador = verificador;
    }

    /** Linha de execução do BARBEIRO (procedimento barber de Tanenbaum). */
    public void barbeiro() throws InterruptedException {
        while (true) {
            mutex.acquire();
            boolean ninguem = esperando == 0;
            mutex.release();
            if (ninguem) {
                Log.info("Nenhum cliente esperando -> barbeiro vai DORMIR");
            }

            clientes.acquire();             // down(customers): dorme se não houver clientes
            mutex.acquire();                // down(mutex): entra na região crítica
            // ---------- REGIÃO CRÍTICA ----------
            esperando--;
            verificador.checar(esperando >= 0, "esperando ficou negativo: " + esperando);
            Log.info("Barbeiro acordado, chama o próximo cliente (esperando=" + esperando + ")");
            barbeiros.release();            // up(barbers): um barbeiro pronto para cortar
            mutex.release();                // up(mutex): sai da região crítica

            clienteSentado.acquire();       // espera o cliente sentar na cadeira
            int id = clienteNaCadeira;
            Log.info("Barbeiro CORTANDO o cabelo do Cliente-" + id);
            Tempo.dormirAleatorio(tempoCorteMaxMs / 2, tempoCorteMaxMs);   // cut_hair()
            verificador.checar(cortados.add(id), "Cliente-" + id + " teve o cabelo cortado duas vezes");
            Log.info("Barbeiro terminou o corte do Cliente-" + id);
            corteConcluido.release();
        }
    }

    /** Linha de execução de um CLIENTE (procedimento customer de Tanenbaum). */
    public void cliente(int id) throws InterruptedException {
        Log.info("Cliente-" + id + " chegou na barbearia");
        mutex.acquire();                    // down(mutex): entra na região crítica
        // ---------- REGIÃO CRÍTICA ----------
        if (esperando < cadeiras) {
            esperando++;
            maxEsperando = Math.max(maxEsperando, esperando);
            verificador.checar(esperando <= cadeiras, "Mais clientes esperando que cadeiras: " + esperando);
            Log.info("Cliente-" + id + " sentou na sala de espera (" + esperando + "/" + cadeiras + " cadeiras)");
            clientes.release();             // up(customers): acorda o barbeiro se necessário
            mutex.release();                // up(mutex): sai da região crítica

            barbeiros.acquire();            // down(barbers): espera o barbeiro ficar livre
            clienteNaCadeira = id;
            Log.info("Cliente-" + id + " sentou na cadeira do barbeiro");
            clienteSentado.release();
            corteConcluido.acquire();       // get_haircut(): espera o corte terminar
            atendidos.incrementAndGet();
            Log.info("Cliente-" + id + " saiu com o cabelo cortado");
        } else {
            desistentes.incrementAndGet();
            Log.info("Cliente-" + id + " encontrou a barbearia LOTADA e foi embora");
            mutex.release();                // up(mutex): sai sem cortar o cabelo
        }
    }

    // Getters lidos somente após o término das threads
    public int getAtendidos() {
        return atendidos.get();
    }

    public int getDesistentes() {
        return desistentes.get();
    }

    public int getCortesRealizados() {
        return cortados.size();
    }

    public int getEsperando() {
        return esperando;
    }

    public int getMaxEsperando() {
        return maxEsperando;
    }
}
