package barbeiro;

import java.util.ArrayList;
import java.util.List;

import util.Log;
import util.Tempo;
import util.Verificador;

public class BarbeiroDorminhoco {

    public static Verificador executar(int cadeiras, int numClientes, int chegadaMaxMs, int corteMaxMs)
            throws InterruptedException {
        Verificador v = new Verificador("Barbeiro Dorminhoco");
        Barbearia barbearia = new Barbearia(cadeiras, corteMaxMs, v);

        Barbeiro barbeiro = new Barbeiro(barbearia);
        barbeiro.start();

        List<Cliente> clientes = new ArrayList<>();
        for (int i = 1; i <= numClientes; i++) {
            Cliente c = new Cliente(i, barbearia);
            clientes.add(c);
            c.start();
            Tempo.dormirAleatorio(0, chegadaMaxMs); // intervalo entre chegadas
        }

        v.aguardarTermino(15_000, clientes);
        barbeiro.interrupt(); // fim do expediente: barbeiro está dormindo em down(clientes)
        barbeiro.join(1_000);

        v.checar(barbearia.getAtendidos() + barbearia.getDesistentes() == numClientes,
                "Atendidos + desistentes != total de clientes");
        v.checar(barbearia.getCortesRealizados() == barbearia.getAtendidos(),
                "Cortes realizados (" + barbearia.getCortesRealizados() + ") != atendidos ("
                        + barbearia.getAtendidos() + ")");
        v.checar(barbearia.getEsperando() == 0, "Sobraram clientes esperando: " + barbearia.getEsperando());
        v.checar(barbearia.getMaxEsperando() <= cadeiras, "Mais clientes na espera que cadeiras");

        v.resumo("Cadeiras de espera=" + cadeiras + ", clientes=" + numClientes);
        v.resumo("Atendidos=" + barbearia.getAtendidos() + ", desistentes=" + barbearia.getDesistentes()
                + ", máximo simultâneo na espera=" + barbearia.getMaxEsperando());
        return v;
    }

    public static void main(String[] args) throws InterruptedException {
        Log.titulo("BARBEIRO DORMINHOCO");
        Log.reiniciarRelogio();
        executar(3, 15, 60, 150).imprimir();
    }
}
