package fumantes;

import java.util.ArrayList;
import java.util.List;

import util.Log;
import util.Verificador;

public class ProblemaDosFumantes {

    public static Verificador executar(int rodadas, int tempoMaxMs) throws InterruptedException {
        Verificador v = new Verificador("Problema dos Fumantes");
        Mesa mesa = new Mesa(v);

        List<Fumante> fumantes = new ArrayList<>();
        for (Ingrediente i : Ingrediente.values()) {
            fumantes.add(new Fumante(i, mesa, tempoMaxMs));
        }
        Representante representante = new Representante(mesa, rodadas);

        List<Thread> threads = new ArrayList<>(fumantes);
        threads.add(representante);
        fumantes.forEach(Thread::start);
        representante.start();
        v.aguardarTermino(15_000, threads);

        int total = 0;
        StringBuilder porFumante = new StringBuilder();
        for (Fumante f : fumantes) {
            total += f.getCigarrosFumados();
            porFumante.append(f.getName()).append("=").append(f.getCigarrosFumados()).append(" ");
        }
        v.checar(total == rodadas, "Cigarros fumados (" + total + ") != rodadas (" + rodadas + ")");

        v.resumo("Rodadas=" + rodadas + ", cigarros fumados=" + total);
        v.resumo("Por fumante: " + porFumante.toString().trim());
        return v;
    }

    public static void main(String[] args) throws InterruptedException {
        Log.titulo("PROBLEMA DOS FUMANTES");
        Log.reiniciarRelogio();
        executar(8, 150).imprimir();
    }
}
