package prodconssemaforo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import util.Log;
import util.Verificador;

/** Produtor/Consumidor (Bounded Buffer) resolvido com semáforos. */
public class ProdConsSemaforo {

    public static Verificador executar(int produtores, int consumidores, int itensPorProdutor,
            int capacidade, int tempoMaxMs) throws InterruptedException {
        Verificador v = new Verificador("Produtor/Consumidor com semáforos");
        BufferLimitado buffer = new BufferLimitado(capacidade, v);
        Set<Integer> consumidos = ConcurrentHashMap.newKeySet();
        int total = produtores * itensPorProdutor;

        List<Thread> threads = new ArrayList<>();
        for (int c = 0; c < consumidores; c++) {
            int qtd = total / consumidores + (c < total % consumidores ? 1 : 0);
            threads.add(new Consumidor(c, buffer, qtd, consumidos, v, tempoMaxMs));
        }
        for (int p = 0; p < produtores; p++) {
            threads.add(new Produtor(p, buffer, itensPorProdutor, tempoMaxMs));
        }
        threads.forEach(Thread::start);
        v.aguardarTermino(10_000, threads);

        // Todo item produzido deve ter sido consumido exatamente uma vez
        for (int p = 0; p < produtores; p++) {
            for (int i = 0; i < itensPorProdutor; i++) {
                v.checar(consumidos.contains(p * 1000 + i), "Item " + (p * 1000 + i) + " foi perdido");
            }
        }
        v.checar(consumidos.size() == total, "Consumidos " + consumidos.size() + " de " + total);
        v.checar(buffer.getOcupados() == 0, "Buffer terminou com " + buffer.getOcupados() + " itens");

        v.resumo("Produtores=" + produtores + ", consumidores=" + consumidores + ", capacidade=" + capacidade);
        v.resumo("Itens produzidos=" + total + ", consumidos=" + consumidos.size()
                + ", restantes no buffer=" + buffer.getOcupados());
        return v;
    }

    public static void main(String[] args) throws InterruptedException {
        Log.titulo("PRODUTOR / CONSUMIDOR COM SEMÁFOROS");
        Log.reiniciarRelogio();
        executar(2, 2, 6, 3, 100).imprimir();
    }
}
