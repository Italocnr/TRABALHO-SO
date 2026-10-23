package robos;

import java.util.List;

import util.Log;
import util.Verificador;

public class TresRobos {
    private static final String[] PADRAO = {"Bart", "Lisa", "Maggie", "Lisa"};

    /** Um ciclo = Bart, Lisa, Maggie, Lisa (4 movimentos). */
    public static Verificador executar(int ciclos, int tempoMaxMs) throws InterruptedException {
        Verificador v = new Verificador("Três Robôs");
        Controle c = new Controle(tempoMaxMs, v);

        // Iniciados em ordem "errada" de propósito: a ordem de start não importa, quem manda são os semáforos
        List<Thread> robos = List.of(new Maggie(c, ciclos), new Lisa(c, ciclos), new Bart(c, ciclos));
        robos.forEach(Thread::start);
        v.aguardarTermino(15_000, robos);

        List<String> seq = c.getSequencia();
        v.checar(seq.size() == 4 * ciclos, "Movimentos=" + seq.size() + ", esperado=" + (4 * ciclos));
        for (int i = 0; i < seq.size(); i++) {
            if (!seq.get(i).equals(PADRAO[i % 4])) {
                v.checar(false, "Movimento #" + (i + 1) + " foi " + seq.get(i) + ", esperado " + PADRAO[i % 4]);
                break;
            }
        }

        v.resumo("Ciclos=" + ciclos + ", movimentos=" + seq.size());
        v.resumo("Início da sequência: " + String.join(" -> ", seq.subList(0, Math.min(12, seq.size())))
                + (seq.size() > 12 ? " -> ..." : ""));
        return v;
    }

    public static void main(String[] args) throws InterruptedException {
        Log.titulo("TRÊS ROBÔS (Bart -> Lisa -> Maggie -> Lisa -> ...)");
        Log.reiniciarRelogio();
        executar(4, 80).imprimir();
    }
}
