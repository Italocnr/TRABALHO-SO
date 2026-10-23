package selvagens;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import util.Log;
import util.Verificador;

public class JantarDosSelvagens {

    public static Verificador executar(int numSelvagens, int capacidade, int refeicoes, int tempoMaxMs)
            throws InterruptedException {
        Verificador v = new Verificador("Jantar dos Selvagens");
        Caldeirao caldeirao = new Caldeirao(capacidade, tempoMaxMs, v);
        AtomicInteger comendoAgora = new AtomicInteger();
        AtomicInteger maxComendoJuntos = new AtomicInteger();

        Cozinheiro cozinheiro = new Cozinheiro(caldeirao);
        cozinheiro.start();

        List<Selvagem> selvagens = new ArrayList<>();
        for (int i = 1; i <= numSelvagens; i++) {
            selvagens.add(new Selvagem(i, caldeirao, refeicoes, tempoMaxMs, comendoAgora, maxComendoJuntos));
        }
        selvagens.forEach(Thread::start);
        v.aguardarTermino(15_000, selvagens);
        cozinheiro.interrupt();
        cozinheiro.join(1_000);

        int total = numSelvagens * refeicoes;
        // Começa cheio; cada recarga só acontece quando o caldeirão esvazia e alguém ainda quer comer
        int recargasEsperadas = Math.max(0, (total - capacidade + capacidade - 1) / capacidade);
        v.checar(caldeirao.getPorcoesServidas() == total,
                "Porções servidas (" + caldeirao.getPorcoesServidas() + ") != " + total);
        v.checar(caldeirao.getRecargas() == recargasEsperadas,
                "Recargas (" + caldeirao.getRecargas() + ") != esperado (" + recargasEsperadas + ")");

        v.resumo("Selvagens=" + numSelvagens + ", capacidade do caldeirão=" + capacidade
                + ", refeições por selvagem=" + refeicoes);
        v.resumo("Porções servidas=" + caldeirao.getPorcoesServidas() + ", recargas do cozinheiro="
                + caldeirao.getRecargas() + ", máximo comendo ao mesmo tempo=" + maxComendoJuntos.get());
        return v;
    }

    public static void main(String[] args) throws InterruptedException {
        Log.titulo("JANTAR DOS SELVAGENS");
        Log.reiniciarRelogio();
        executar(4, 5, 4, 100).imprimir();
    }
}
