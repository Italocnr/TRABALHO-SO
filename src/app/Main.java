package app;

import java.util.Scanner;

import ProdutorConsumidor.ProdCons;
import barbeiro.BarbeiroDorminhoco;
import filosofos.JantarDosFilosofos;
import fumantes.ProblemaDosFumantes;
import leitoresescritores.LeitoresEscritores;
import prodconssemaforo.ProdConsSemaforo;
import robos.TresRobos;
import selvagens.JantarDosSelvagens;
import testes.TodosOsTestes;

/** Menu para executar cada problema. Também aceita o nome do problema como argumento. */
public class Main {

    public static void main(String[] args) throws Exception {
        String opcao;
        if (args.length > 0) {
            opcao = args[0];
        } else {
            System.out.println("Trabalho Prático 1 - Comunicação e Sincronização de Processos e Threads");
            System.out.println(" 1) prodcons          - Parte I: Produtor/Consumidor (monitor, código do enunciado)");
            System.out.println(" 2) prodcons-sem      - Produtor/Consumidor com semáforos");
            System.out.println(" 3) barbeiro          - Barbeiro Dorminhoco");
            System.out.println(" 4) leitores          - Leitores e Escritores");
            System.out.println(" 5) fumantes          - Problema dos Fumantes");
            System.out.println(" 6) filosofos         - Jantar dos Filósofos");
            System.out.println(" 7) filosofos-ingenuo - Filósofos SEM estratégia (mostra o deadlock)");
            System.out.println(" 8) selvagens         - Jantar dos Selvagens");
            System.out.println(" 9) robos             - Três Robôs");
            System.out.println("10) testes            - Executa todos os testes de validação");
            System.out.print("Escolha: ");
            opcao = new Scanner(System.in).nextLine().trim();
        }

        switch (opcao) {
            case "1", "prodcons" -> ProdCons.main(new String[0]);
            case "2", "prodcons-sem" -> ProdConsSemaforo.main(new String[0]);
            case "3", "barbeiro" -> BarbeiroDorminhoco.main(new String[0]);
            case "4", "leitores" -> LeitoresEscritores.main(new String[0]);
            case "5", "fumantes" -> ProblemaDosFumantes.main(new String[0]);
            case "6", "filosofos" -> JantarDosFilosofos.main(new String[0]);
            case "7", "filosofos-ingenuo" -> JantarDosFilosofos.main(new String[] {"ingenuo"});
            case "8", "selvagens" -> JantarDosSelvagens.main(new String[0]);
            case "9", "robos" -> TresRobos.main(new String[0]);
            case "10", "testes" -> TodosOsTestes.main(new String[0]);
            default -> System.out.println("Opção inválida: " + opcao);
        }
    }
}
