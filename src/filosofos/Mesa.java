package filosofos;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicIntegerArray;

import util.Log;
import util.Tempo;
import util.Verificador;

/**
 * RECURSOS COMPARTILHADOS: os 5 palitos (hashis) p0..p4.
 * Cada palito é um semáforo binário iniciado em 1 (1 = palito livre na mesa).
 * O filósofo fi usa o palito à direita p(i) e o palito à esquerda p(i+1).
 *
 * ESTRATÉGIA ANTI-DEADLOCK (ordenação de recursos / solução assimétrica):
 * todo filósofo pega primeiro o palito de MENOR índice. Para f0..f3 isso é o
 * palito da direita, mas f4 (palitos p4 e p0) pega p0 primeiro. Assim nunca se
 * forma a espera circular em que cada um segura um palito e espera o do vizinho.
 *
 * Com ordenado=false todos pegam primeiro o palito da direita (versão ingênua),
 * o que pode gerar deadlock — usado apenas para demonstração.
 */
public class Mesa {
    public static final int N = 5;

    private final Semaphore[] palitos = new Semaphore[N];
    private final AtomicIntegerArray comendo = new AtomicIntegerArray(N); // 1 = filósofo i está comendo
    private final boolean ordenado;
    private final Verificador verificador;

    public Mesa(boolean ordenado, Verificador verificador) {
        for (int i = 0; i < N; i++) {
            palitos[i] = new Semaphore(1);
        }
        this.ordenado = ordenado;
        this.verificador = verificador;
    }

    public static int direito(int filosofo) {
        return filosofo;
    }

    public static int esquerdo(int filosofo) {
        return (filosofo + 1) % N;
    }

    public void pegarPalitos(int f) throws InterruptedException {
        int primeiro;
        int segundo;
        if (ordenado) {
            primeiro = Math.min(direito(f), esquerdo(f));
            segundo = Math.max(direito(f), esquerdo(f));
        } else {
            primeiro = direito(f);
            segundo = esquerdo(f);
        }
        Log.info("Com FOME: tentando pegar p" + primeiro);
        palitos[primeiro].acquire();                     // down(palito primeiro)
        Log.info("Pegou p" + primeiro + ", tentando pegar p" + segundo);
        if (!ordenado) {
            Tempo.dormirAleatorio(50, 60); // alarga a janela para o deadlock aparecer na demonstração
        }
        palitos[segundo].acquire();                      // down(palito segundo)

        // ---------- REGIÃO CRÍTICA: o filósofo segura os dois palitos ----------
        comendo.set(f, 1);
        verificador.checar(comendo.get((f + 1) % N) == 0 && comendo.get((f + N - 1) % N) == 0,
                "Filósofo " + f + " comendo junto com um vizinho");
    }

    public void devolverPalitos(int f) {
        comendo.set(f, 0);
        palitos[direito(f)].release();                   // up(palito direito)
        palitos[esquerdo(f)].release();                  // up(palito esquerdo)
        Log.info("Devolveu p" + direito(f) + " e p" + esquerdo(f) + " à mesa");
    }
}
