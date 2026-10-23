package util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Coleta as violações de invariantes detectadas durante a execução (validação/testes).
 * Se ao final não houver violações, a sincronização funcionou corretamente.
 */
public class Verificador {
    private final String nome;
    private final Queue<String> violacoes = new ConcurrentLinkedQueue<>();
    private final List<String> resumo = Collections.synchronizedList(new ArrayList<>());

    public Verificador(String nome) {
        this.nome = nome;
    }

    /** Registra uma violação caso a condição seja falsa. */
    public void checar(boolean condicao, String mensagem) {
        if (!condicao) {
            violacoes.add(mensagem);
        }
    }

    public void resumo(String linha) {
        resumo.add(linha);
    }

    /**
     * Espera as threads terminarem. Se alguma não terminar dentro do prazo,
     * considera-se que houve DEADLOCK e a thread é interrompida.
     */
    public void aguardarTermino(long timeoutMs, Collection<? extends Thread> threads) throws InterruptedException {
        long limite = System.currentTimeMillis() + timeoutMs;
        for (Thread t : threads) {
            long restante = limite - System.currentTimeMillis();
            if (restante > 0) {
                t.join(restante);
            }
        }
        for (Thread t : threads) {
            if (t.isAlive()) {
                violacoes.add("Thread '" + t.getName() + "' não terminou em " + timeoutMs
                        + " ms (possível DEADLOCK)");
                t.interrupt();
            }
        }
    }

    public boolean ok() {
        return violacoes.isEmpty();
    }

    public boolean houveDeadlock() {
        return violacoes.stream().anyMatch(v -> v.contains("DEADLOCK"));
    }

    public List<String> getViolacoes() {
        return new ArrayList<>(violacoes);
    }

    public List<String> getResumo() {
        synchronized (resumo) {
            return new ArrayList<>(resumo);
        }
    }

    public void imprimir() {
        System.out.println();
        System.out.println("---------- VALIDAÇÃO: " + nome + " ----------");
        for (String linha : getResumo()) {
            System.out.println("  " + linha);
        }
        if (ok()) {
            System.out.println("  RESULTADO: OK - nenhuma violação de sincronização detectada");
        } else {
            System.out.println("  RESULTADO: FALHOU - " + violacoes.size() + " violação(ões):");
            for (String v : violacoes) {
                System.out.println("    - " + v);
            }
        }
    }
}
