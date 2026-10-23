package testes;

import barbeiro.BarbeiroDorminhoco;
import filosofos.JantarDosFilosofos;
import fumantes.ProblemaDosFumantes;
import leitoresescritores.LeitoresEscritores;
import prodconssemaforo.ProdConsSemaforo;
import robos.TresRobos;
import selvagens.JantarDosSelvagens;
import util.Log;
import util.Verificador;

/**
 * Validação automatizada: executa cada problema várias vezes, com tempos pequenos
 * (mais trocas de contexto = mais chances de achar condições de corrida), e confere
 * as invariantes registradas pelo Verificador. Qualquer thread que não termina no
 * prazo é reportada como DEADLOCK.
 */
public class TodosOsTestes {

    private interface Cenario {
        Verificador executar() throws InterruptedException;
    }

    private static int falhas = 0;

    public static void main(String[] args) throws InterruptedException {
        Log.setAtivo(false); // sem log detalhado: só o resultado dos testes
        System.out.println("=============== TESTES DE SINCRONIZAÇÃO ===============");

        rodar("Produtor/Consumidor - 3 prod, 2 cons, buffer 4", 30,
                () -> ProdConsSemaforo.executar(3, 2, 300, 4, 0));
        rodar("Produtor/Consumidor - buffer de 1 posição", 30,
                () -> ProdConsSemaforo.executar(4, 4, 100, 1, 0));
        rodar("Barbeiro - 3 cadeiras, 40 clientes", 15,
                () -> BarbeiroDorminhoco.executar(3, 40, 3, 6));
        rodar("Barbeiro - 1 cadeira de espera", 15,
                () -> BarbeiroDorminhoco.executar(1, 30, 3, 4));
        rodar("Leitores/Escritores - 5 leitores, 3 escritores", 10,
                () -> LeitoresEscritores.executar(5, 3, 20, 4));
        rodar("Fumantes - 200 rodadas", 15,
                () -> ProblemaDosFumantes.executar(200, 0));
        rodar("Filósofos - ordenação de recursos, sem pausas", 30,
                () -> JantarDosFilosofos.executar(200, 0, true, 10_000));
        rodar("Selvagens - 6 selvagens, caldeirão 4", 20,
                () -> JantarDosSelvagens.executar(6, 4, 30, 0));
        rodar("Selvagens - caldeirão de 1 porção", 20,
                () -> JantarDosSelvagens.executar(5, 1, 20, 0));
        rodar("Três robôs - 500 ciclos", 20,
                () -> TresRobos.executar(500, 0));

        // Contraprova: sem a estratégia anti-deadlock o detector precisa acusar o deadlock
        Verificador ingenuo = JantarDosFilosofos.executar(3, 10, false, 2_000);
        boolean detectou = ingenuo.houveDeadlock();
        System.out.printf("[%s] CONTRAPROVA Filósofos ingênuos -> deadlock %s%n",
                detectou ? "PASSOU" : "AVISO ", detectou ? "detectado, como esperado"
                        : "não ocorreu nesta execução (é probabilístico)");

        System.out.println("=======================================================");
        System.out.println(falhas == 0 ? "TODOS OS TESTES PASSARAM" : falhas + " CENÁRIO(S) FALHARAM");
        System.exit(falhas == 0 ? 0 : 1);
    }

    private static void rodar(String nome, int repeticoes, Cenario cenario) throws InterruptedException {
        long inicio = System.currentTimeMillis();
        Verificador ultimo = null;
        for (int i = 1; i <= repeticoes; i++) {
            ultimo = cenario.executar();
            if (!ultimo.ok()) {
                falhas++;
                System.out.printf("[FALHOU] %s (execução %d/%d)%n", nome, i, repeticoes);
                ultimo.getViolacoes().stream().limit(5).forEach(v -> System.out.println("          - " + v));
                return;
            }
        }
        System.out.printf("[PASSOU] %s  (%dx, %d ms)%n", nome, repeticoes, System.currentTimeMillis() - inicio);
        for (String linha : ultimo.getResumo()) {
            System.out.println("          " + linha);
        }
    }
}
