package robos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import util.Log;
import util.Tempo;
import util.Verificador;

/**
 * Sequência desejada: Bart -> Lisa -> Maggie -> Lisa -> Bart -> Lisa -> Maggie -> Lisa -> ...
 *
 * Um semáforo por robô, funcionando como um "bastão" passado de mão em mão:
 *  - semBart   (inicia 1): Bart é o primeiro a se mover.
 *  - semLisa   (inicia 0)
 *  - semMaggie (inicia 0)
 * Só existe 1 permissão circulando entre os três semáforos, então apenas um robô
 * se move por vez. Lisa alterna entre passar a vez para Maggie e para Bart, o que
 * é feito com duas etapas no laço dela (sem variável compartilhada e sem espera ocupada:
 * quem não tem a vez fica BLOQUEADO no acquire).
 */
public class Controle {
    final Semaphore semBart = new Semaphore(1);
    final Semaphore semLisa = new Semaphore(0);
    final Semaphore semMaggie = new Semaphore(0);

    private final List<String> sequencia = Collections.synchronizedList(new ArrayList<>());
    private final AtomicInteger movendo = new AtomicInteger();
    private final int tempoMaxMs;
    private final Verificador verificador;

    public Controle(int tempoMaxMs, Verificador verificador) {
        this.tempoMaxMs = tempoMaxMs;
        this.verificador = verificador;
    }

    /** move(): o movimento do robô. Só pode ser executado por um robô de cada vez. */
    public void move(String robo) throws InterruptedException {
        verificador.checar(movendo.incrementAndGet() == 1, "Dois robôs se movendo ao mesmo tempo");
        sequencia.add(robo);
        Log.info(robo + " se MOVEU  (movimento #" + sequencia.size() + ")");
        Tempo.dormirAleatorio(0, tempoMaxMs);
        movendo.decrementAndGet();
    }

    public List<String> getSequencia() {
        synchronized (sequencia) {
            return new ArrayList<>(sequencia);
        }
    }
}
