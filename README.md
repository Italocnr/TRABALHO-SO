# Trabalho Prático 1 — Comunicação e Sincronização de Processos e Threads

PUC Minas · Engenharia de Software · Sistemas Operacionais · Prof. Diego Silva Caldeira Rocha
Linguagem: **Java 21** (sem bibliotecas externas). Sincronização: **`java.util.concurrent.Semaphore`**.

---

## 1. Como compilar e executar

Windows (PowerShell):

```powershell
.\run.ps1              # compila e abre o menu
.\run.ps1 barbeiro     # compila e executa direto um problema
.\run.ps1 testes       # executa a suíte de validação
```

Linux / macOS / Git Bash:

```sh
sh run.sh              # menu
sh run.sh barbeiro
sh run.sh testes
```

Manualmente:

```sh
javac -encoding UTF-8 -d out $(find src -name "*.java")
java -cp out app.Main <opção>
```

| Opção               | Problema                                               | Classe com `main`                                   |
|---------------------|--------------------------------------------------------|-----------------------------------------------------|
| `prodcons`          | a) Produtor/Consumidor — Parte I (código do enunciado) | `ProdutorConsumidor.ProdCons`                        |
| `prodcons-sem`      | a) Produtor/Consumidor com semáforos                   | `prodconssemaforo.ProdConsSemaforo`                  |
| `barbeiro`          | b) Barbeiro Dorminhoco                                 | `barbeiro.BarbeiroDorminhoco`                        |
| `leitores`          | c) Leitores e Escritores                               | `leitoresescritores.LeitoresEscritores`              |
| `fumantes`          | d) Problema dos Fumantes                               | `fumantes.ProblemaDosFumantes`                       |
| `filosofos`         | e) Jantar dos Filósofos                                | `filosofos.JantarDosFilosofos`                       |
| `filosofos-ingenuo` | e) Filósofos **sem** estratégia (mostra o deadlock)    | `filosofos.JantarDosFilosofos ingenuo`               |
| `selvagens`         | f) Jantar dos Selvagens                                | `selvagens.JantarDosSelvagens`                       |
| `robos`             | g) Três Robôs                                          | `robos.TresRobos`                                    |
| `testes`            | Validação automatizada de todos os problemas           | `testes.TodosOsTestes`                               |

## 2. Organização do código

```
src/
├── ProdutorConsumidor/   Parte I – código do enunciado (monitor: synchronized/wait/notifyAll)
├── prodconssemaforo/     a) Produtor/Consumidor com semáforos
├── barbeiro/             b) Barbeiro Dorminhoco
├── leitoresescritores/   c) Leitores e Escritores
├── fumantes/             d) Fumantes
├── filosofos/            e) Filósofos
├── selvagens/            f) Selvagens
├── robos/                g) Três Robôs
├── util/                 Log (impressão), Tempo (sleep aleatório), Verificador (validação)
├── testes/               TodosOsTestes – suíte de validação
└── app/                  Main – menu
```

Todo problema segue o mesmo padrão, o que facilita a explicação no vídeo:

* **Uma classe de recurso compartilhado** (`BufferLimitado`, `Barbearia`, `MemoriaCompartilhada`, `Mesa`, `Caldeirao`, `Controle`) que contém **os semáforos** e as **regiões críticas** (marcadas com o comentário `// ---------- REGIÃO CRÍTICA ----------`).
* **Uma classe por tipo de thread**, sempre `extends Thread` (igual à Parte I): `Barbeiro`, `Cliente`, `Leitor`, `Escritor`, `Fumante`, `Representante`, `Filosofo`, `Selvagem`, `Cozinheiro`, `Bart`, `Lisa`, `Maggie`.
* **Uma classe principal** com `executar(...)` (cria e inicia as threads, espera terminarem, confere os resultados) e `main(...)` (demonstração).

Cada linha impressa tem o formato `[tempo em ms] [nome da thread] mensagem`, então no vídeo dá para apontar **qual thread** fez **o quê** e **quando**.

### Como a validação funciona (`util/Verificador`)

* Durante a execução, contadores atômicos registram quem está dentro de cada região crítica, e `verificador.checar(condição, mensagem)` registra uma violação se uma regra for quebrada (ex.: "dois robôs se movendo ao mesmo tempo").
* Ao final, `aguardarTermino(timeout, threads)` espera as threads. **Se alguma não terminar no prazo, é registrado DEADLOCK.**
* Depois são conferidos totais (itens produzidos = consumidos, cigarros = rodadas, etc.).
* `TodosOsTestes` roda cada problema dezenas de vezes com tempos ~0 ms (máximo de trocas de contexto, maior chance de expor condição de corrida) e mostra `[PASSOU]`/`[FALHOU]`. Também roda uma **contraprova**: os filósofos sem a estratégia anti-deadlock, para mostrar que o detector realmente acusa deadlock.

Resultado esperado de `run testes`:

```
[PASSOU] Produtor/Consumidor - 3 prod, 2 cons, buffer 4  (30x, ...)
[PASSOU] Produtor/Consumidor - buffer de 1 posição  (30x, ...)
[PASSOU] Barbeiro - 3 cadeiras, 40 clientes  (15x, ...)
[PASSOU] Barbeiro - 1 cadeira de espera  (15x, ...)
[PASSOU] Leitores/Escritores - 5 leitores, 3 escritores  (10x, ...)
[PASSOU] Fumantes - 200 rodadas  (15x, ...)
[PASSOU] Filósofos - ordenação de recursos, sem pausas  (30x, ...)
[PASSOU] Selvagens - 6 selvagens, caldeirão 4  (20x, ...)
[PASSOU] Selvagens - caldeirão de 1 porção  (20x, ...)
[PASSOU] Três robôs - 500 ciclos  (20x, ...)
[PASSOU] CONTRAPROVA Filósofos ingênuos -> deadlock detectado, como esperado
TODOS OS TESTES PASSARAM
```

---

## 3. Roteiro do vídeo — Trabalho Prático 1
a) Produtor e Consumidor com semáforos
Nesse problema, temos produtores que inserem itens e consumidores que os retiram de um buffer circular de capacidade limitada.

Utilizamos três semáforos: vazios, que controla as posições disponíveis; cheios, que controla os itens prontos para consumo; e mutex, que garante que apenas uma thread por vez altere o buffer.

O produtor espera por uma posição livre, entra na região crítica, insere o item e sinaliza que existe um novo item disponível. O consumidor faz o processo inverso.

Para evitar deadlock, primeiro adquirimos o semáforo de contagem e depois o mutex, impedindo que uma thread fique bloqueada esperando espaço enquanto mantém o acesso exclusivo ao buffer.

Nos testes, verificamos se todos os itens foram consumidos corretamente, sem perdas, duplicações ou violações da capacidade do buffer.
b) Barbeiro Dorminhoco
Nesse problema, temos um barbeiro, vários clientes e uma sala de espera com um número limitado de cadeiras.

Utilizamos semáforos para sincronizar a chegada dos clientes com o barbeiro. Quando não há clientes, o barbeiro fica bloqueado esperando. Quando um cliente chega, ele sinaliza sua presença e aguarda o atendimento.

Se todas as cadeiras estiverem ocupadas, o cliente vai embora. Caso consiga esperar, ele aguarda sua vez e só sai depois que o corte termina.

O mutex protege o contador de clientes esperando, evitando que duas threads alterem essa informação simultaneamente. Também usamos semáforos para sincronizar o momento em que o cliente senta e o término do corte.

Nos testes, verificamos se a quantidade de atendimentos e desistências corresponde ao total de clientes, se o limite de cadeiras é respeitado e se não ocorre deadlock.
c) Leitores e Escritores
Nesse problema, várias threads acessam uma memória compartilhada, representada por um vetor. Os leitores podem acessar os dados simultaneamente, mas os escritores precisam ter acesso exclusivo.

Utilizamos o semáforo recurso para controlar o acesso à memória e mutexLeitores para proteger o contador de leitores ativos. Quando o primeiro leitor entra, ele bloqueia o acesso dos escritores. Quando o último leitor sai, libera esse acesso.

Os escritores precisam adquirir o recurso exclusivo antes de modificar o vetor. Também utilizamos uma fila de sincronização para impedir que novos leitores ultrapassem continuamente um escritor que está esperando.

Nos testes, verificamos se as leituras podem acontecer em paralelo, se nunca ocorre leitura durante uma escrita e se os valores permanecem consistentes durante as operações.
d) Problema dos Fumantes
Nesse problema, temos três fumantes, cada um com um ingrediente, e um representante que coloca dois ingredientes sobre uma mesa.

Cada fumante possui um semáforo próprio, inicialmente bloqueado. O representante escolhe quais ingredientes disponibilizar e sinaliza apenas para o fumante que possui o ingrediente que está faltando.

O fumante acorda, retira os ingredientes, prepara o cigarro e fuma. Quando termina, sinaliza ao representante, permitindo que uma nova rodada comece.

Essa estratégia evita que vários fumantes tentem retirar ingredientes simultaneamente ou que fiquem esperando uns pelos outros indefinidamente. A sincronização garante que apenas o fumante correto seja liberado em cada rodada.

Nos testes, verificamos se o fumante escolhido é sempre o correto, se as rodadas acontecem na ordem esperada e se o número de cigarros corresponde ao número de rodadas.
e) Jantar dos Filósofos
Nesse problema, temos cinco filósofos que alternam entre meditar e comer, compartilhando cinco palitos. Cada filósofo precisa dos dois palitos ao seu lado para comer.

Cada palito é controlado por um semáforo binário, garantindo que ele não seja utilizado por dois filósofos ao mesmo tempo.

Uma solução ingênua consiste em todos pegarem primeiro o palito da direita. Isso pode causar deadlock, porque cada filósofo segura um palito e espera indefinidamente pelo outro.

Para resolver esse problema, implementamos uma estratégia de ordenação de recursos: cada filósofo pega primeiro o palito de menor índice. Assim, quebramos a espera circular que poderia causar o deadlock.

Também executamos a versão ingênua para demonstrar que o problema realmente pode acontecer. Nos testes da solução corrigida, verificamos se todos os filósofos conseguem comer sem conflitos ou bloqueios permanentes.
f) Jantar dos Selvagens
Nesse problema, vários selvagens compartilham um caldeirão com uma quantidade limitada de porções, enquanto um cozinheiro é responsável por reabastecê-lo.

Utilizamos um mutex para garantir que apenas um selvagem se sirva por vez. Quando o caldeirão está vazio, um selvagem sinaliza ao cozinheiro e aguarda até que ele prepare uma nova quantidade de comida.

O cozinheiro permanece bloqueado enquanto não precisa reabastecer o caldeirão. Quando é sinalizado, ele enche o recipiente e libera o selvagem que estava esperando.

A sincronização impede que alguém retire comida de um caldeirão vazio e evita que vários selvagens tentem solicitar o reabastecimento simultaneamente. Depois de se servirem, os selvagens podem comer em paralelo, sem manter o mutex ocupado.

Nos testes, verificamos o controle das porções, o funcionamento do reabastecimento e a ausência de violações das regras do problema.
g) Três Robôs
Nesse problema, temos três robôs, Bart, Lisa e Maggie, que precisam executar seus movimentos em uma sequência específica: Bart, Lisa, Maggie e Lisa, repetidamente.

Para controlar essa ordem, utilizamos um semáforo para cada robô. Inicialmente, somente o semáforo do Bart permite a execução. Depois de se mover, ele libera a Lisa, que realiza seu movimento e libera a Maggie.

Após o movimento da Maggie, a Lisa recebe novamente a permissão. Na segunda etapa, ela libera o Bart, reiniciando o ciclo.

Dessa forma, os semáforos controlam tanto a ordem de execução quanto o acesso exclusivo à operação de movimento. As threads que não possuem a vez ficam bloqueadas, sem precisar verificar continuamente se podem executar.

Nos testes, verificamos se a sequência de movimentos é respeitada durante todos os ciclos e se nunca existem dois robôs se movendo simultaneamente.

## 4. Tabela-resumo (útil para slides)

| Problema               | Threads                             | Recurso compartilhado        | Semáforos (valor inicial)                                                    | Estratégia anti-deadlock                              |
|------------------------|-------------------------------------|------------------------------|-------------------------------------------------------------------------------|-------------------------------------------------------|
| Produtor/Consumidor    | Produtores, Consumidores            | Buffer circular              | vazios(N), cheios(0), mutex(1)                                                | Semáforo de contagem antes do mutex                   |
| Barbeiro Dorminhoco    | Barbeiro, Clientes                  | Sala de espera / cadeira     | clientes(0), barbeiros(0), mutex(1), clienteSentado(0), corteConcluido(0)     | Mutex liberado antes de bloquear; cliente desiste     |
| Leitores e Escritores  | Leitores, Escritores                | Vetor M                      | fila(1, justo), mutexLeitores(1), recurso(1)                                  | Ordem fixa de aquisição; catraca evita inanição       |
| Fumantes               | 3 Fumantes, Representante           | Mesa                         | fumante[3](0), representante(0)                                               | Representante acorda só o fumante certo               |
| Filósofos              | 5 Filósofos                         | 5 palitos                    | palitos[5](1)                                                                 | Pegar primeiro o palito de menor índice               |
| Selvagens              | Selvagens, Cozinheiro               | Caldeirão                    | mutex(1), caldeiraoVazio(0), caldeiraoCheio(0)                                | Cozinheiro não depende do mutex                       |
| Três Robôs             | Bart, Lisa, Maggie                  | Direito de se mover          | semBart(1), semLisa(0), semMaggie(0)                                          | Uma única permissão sempre repassada                  |
