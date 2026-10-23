package filosofos;

import java.util.ArrayList;
import java.util.List;

import util.Log;
import util.Verificador;

public class JantarDosFilosofos {

    public static Verificador executar(int refeicoes, int tempoMaxMs, boolean ordenado, long timeoutMs)
            throws InterruptedException {
        Verificador v = new Verificador("Jantar dos Filósofos" + (ordenado ? "" : " (versão INGÊNUA)"));
        Mesa mesa = new Mesa(ordenado, v);

        List<Filosofo> filosofos = new ArrayList<>();
        for (int i = 0; i < Mesa.N; i++) {
            filosofos.add(new Filosofo(i, mesa, refeicoes, tempoMaxMs));
        }
        filosofos.forEach(Thread::start);
        v.aguardarTermino(timeoutMs, filosofos);
        for (Filosofo f : filosofos) {
            f.join(1_000); // espera os interrompidos (se houve deadlock) encerrarem
        }

        StringBuilder sb = new StringBuilder();
        for (Filosofo f : filosofos) {
            v.checar(f.getRefeicoesFeitas() == refeicoes,
                    f.getName() + " comeu " + f.getRefeicoesFeitas() + " de " + refeicoes + " vezes");
            sb.append(f.getName()).append("=").append(f.getRefeicoesFeitas()).append(" ");
        }
        v.resumo("Estratégia: " + (ordenado ? "pegar primeiro o palito de menor índice"
                : "todos pegam primeiro o palito da direita (ingênua)"));
        v.resumo("Refeições: " + sb.toString().trim());
        return v;
    }

    public static void main(String[] args) throws InterruptedException {
        boolean ingenuo = args.length > 0 && args[0].equalsIgnoreCase("ingenuo");
        Log.titulo("JANTAR DOS FILÓSOFOS" + (ingenuo ? " - VERSÃO INGÊNUA (DEADLOCK)" : ""));
        Log.reiniciarRelogio();
        // Na versão ingênua a meditação é curta para que todos peguem o palito da direita juntos
        executar(3, ingenuo ? 10 : 100, !ingenuo, ingenuo ? 3_000 : 15_000).imprimir();
    }
}
