# Trabalho Prático 1 — Comunicação e Sincronização de Processos e Threads

PUC Minas · Engenharia de Software · Sistemas Operacionais · Prof. Diego Silva Caldeira Rocha

Implementação, em **Java 21** (sem bibliotecas externas), de problemas clássicos de sincronização entre threads usando **`java.util.concurrent.Semaphore`**. Cada solução evita condições de corrida e deadlock, e o projeto inclui uma suíte que valida automaticamente esses comportamentos.

## Integrantes

- Guilherme Augusto Martins de Carvalho
- Ítalo Eduardo Carneiro da Silva
- João Victor Vial Leite Soares
- Vinicius Matos Oliveira Rocha

## Como executar

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

| Opção               | Problema                                               |
|---------------------|--------------------------------------------------------|
| `prodcons`          | Produtor/Consumidor — Parte I (código do enunciado)    |
| `prodcons-sem`      | a) Produtor/Consumidor com semáforos                   |
| `barbeiro`          | b) Barbeiro Dorminhoco                                 |
| `leitores`          | c) Leitores e Escritores                               |
| `fumantes`          | d) Problema dos Fumantes                               |
| `filosofos`         | e) Jantar dos Filósofos                                |
| `filosofos-ingenuo` | e) Filósofos **sem** estratégia (demonstra o deadlock) |
| `selvagens`         | f) Jantar dos Selvagens                                |
| `robos`             | g) Três Robôs                                          |
| `testes`            | Validação automatizada de todos os problemas           |

## Organização do código

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

Todos os problemas seguem o mesmo padrão:

- **Uma classe de recurso compartilhado** (`BufferLimitado`, `Barbearia`, `MemoriaCompartilhada`, `Mesa`, `Caldeirao`, `Controle`), que contém os semáforos e as regiões críticas (marcadas com `// ---------- REGIÃO CRÍTICA ----------`).
- **Uma classe por tipo de thread**, sempre `extends Thread`: `Barbeiro`, `Cliente`, `Leitor`, `Escritor`, `Fumante`, `Representante`, `Filosofo`, `Selvagem`, `Cozinheiro`, `Bart`, `Lisa`, `Maggie`.
- **Uma classe principal**, com `executar(...)` (cria e inicia as threads, aguarda o término e confere os resultados) e `main(...)` (demonstração).

Cada linha impressa segue o formato `[tempo em ms] [nome da thread] mensagem`, indicando qual thread fez o quê e quando.

## Problemas e soluções

### Parte I — Produtor/Consumidor com monitor

Código fornecido no enunciado. Um buffer de uma posição com métodos `synchronized`: o produtor faz `wait()` enquanto o buffer está cheio, o consumidor faz `wait()` enquanto está vazio, e `notifyAll()` acorda a outra parte após cada operação.

### a) Produtor/Consumidor com semáforos

Produtores inserem e consumidores retiram itens de um buffer circular de capacidade limitada.

- `vazios(N)` conta as posições livres, `cheios(0)` conta os itens disponíveis e `mutex(1)` garante acesso exclusivo ao buffer.
- O produtor faz `acquire` em `vazios`, depois em `mutex`, insere o item e faz `release` em `cheios`. O consumidor faz o inverso.
- **Anti-deadlock:** o semáforo de contagem é sempre adquirido **antes** do mutex, para nenhuma thread bloquear segurando o acesso exclusivo.

### b) Barbeiro Dorminhoco

Um barbeiro, vários clientes e uma sala de espera com número limitado de cadeiras.

- O barbeiro dorme no semáforo `clientes` enquanto não há ninguém esperando.
- O cliente adquire o `mutex` e confere as cadeiras: se estiverem todas ocupadas, vai embora; se não, senta, faz `release` em `clientes` (acordando o barbeiro) e aguarda em `barbeiros`.
- `clienteSentado` e `corteConcluido` sincronizam o início e o fim do corte.
- **Anti-deadlock:** o mutex é sempre liberado antes de qualquer bloqueio.

### c) Leitores e Escritores

Várias threads acessam um vetor compartilhado. Leitores podem ler simultaneamente; escritores precisam de acesso exclusivo.

- `recurso(1)` controla o acesso ao vetor e `mutexLeitores(1)` protege o contador de leitores ativos. O primeiro leitor a entrar bloqueia os escritores, e o último a sair os libera.
- A catraca `fila(1, justa)` impede que novos leitores passem continuamente na frente de um escritor que está esperando, evitando inanição.
- **Anti-deadlock:** todas as threads adquirem os semáforos na mesma ordem.

### d) Problema dos Fumantes

Três fumantes, cada um com um ingrediente, e um representante que coloca dois ingredientes na mesa.

- Cada fumante tem um semáforo próprio iniciado em 0. O representante escolhe os ingredientes e acorda **apenas** o fumante que possui o ingrediente que falta.
- Depois de fumar, o fumante faz `release` em `representante`, liberando a próxima rodada.
- **Anti-deadlock:** os dois ingredientes são entregues juntos, então nenhum fumante fica com um ingrediente esperando o outro.

### e) Jantar dos Filósofos

Cinco filósofos alternam entre meditar e comer, compartilhando cinco palitos; cada um precisa dos dois palitos ao seu lado.

- Cada palito é um semáforo binário (`palitos[i] = 1`).
- A versão ingênua (todos pegam primeiro o palito da direita) leva a uma espera circular e, portanto, a deadlock. Ela está incluída (`filosofos-ingenuo`) para demonstrar o problema.
- **Anti-deadlock:** ordenação de recursos. Cada filósofo pega primeiro o palito de **menor índice**, o que quebra o ciclo de espera.

### f) Jantar dos Selvagens

Vários selvagens se servem de um caldeirão com porções limitadas, e um cozinheiro o reabastece.

- `mutex(1)` garante que só um selvagem se sirva por vez. Ao encontrar o caldeirão vazio, o selvagem faz `release` em `caldeiraoVazio` e aguarda em `caldeiraoCheio`.
- O cozinheiro fica bloqueado até ser chamado, enche o caldeirão e libera o selvagem que esperava.
- Depois de se servirem, os selvagens comem em paralelo, fora da região crítica.
- **Anti-deadlock:** o cozinheiro não depende do mutex.

### g) Três Robôs

Bart, Lisa e Maggie precisam se mover na sequência Bart → Lisa → Maggie → Lisa, repetidamente.

- Um semáforo por robô: `semBart(1)`, `semLisa(0)`, `semMaggie(0)`. A permissão funciona como um bastão passado de um robô para o outro; a Lisa alterna entre liberar a Maggie e liberar o Bart.
- Quem não tem a vez fica bloqueado no `acquire()`, sem espera ocupada.
- **Anti-deadlock:** existe uma única permissão em circulação, sempre repassada adiante.

### Resumo

| Problema              | Threads                   | Recurso compartilhado    | Semáforos (valor inicial)                                                 | Estratégia anti-deadlock                          |
|-----------------------|---------------------------|--------------------------|---------------------------------------------------------------------------|---------------------------------------------------|
| Produtor/Consumidor   | Produtores, Consumidores  | Buffer circular          | vazios(N), cheios(0), mutex(1)                                            | Semáforo de contagem antes do mutex               |
| Barbeiro Dorminhoco   | Barbeiro, Clientes        | Sala de espera / cadeira | clientes(0), barbeiros(0), mutex(1), clienteSentado(0), corteConcluido(0) | Mutex liberado antes de bloquear; cliente desiste |
| Leitores e Escritores | Leitores, Escritores      | Vetor M                  | fila(1, justo), mutexLeitores(1), recurso(1)                              | Ordem fixa de aquisição; catraca evita inanição   |
| Fumantes              | 3 Fumantes, Representante | Mesa                     | fumante[3](0), representante(0)                                           | Representante acorda só o fumante certo           |
| Filósofos             | 5 Filósofos               | 5 palitos                | palitos[5](1)                                                             | Pegar primeiro o palito de menor índice           |
| Selvagens             | Selvagens, Cozinheiro     | Caldeirão                | mutex(1), caldeiraoVazio(0), caldeiraoCheio(0)                            | Cozinheiro não depende do mutex                   |
| Três Robôs            | Bart, Lisa, Maggie        | Direito de se mover      | semBart(1), semLisa(0), semMaggie(0)                                      | Uma única permissão sempre repassada              |

## Validação

A classe `util/Verificador` confere as regras de cada problema durante e após a execução:

- Contadores atômicos registram quem está em cada região crítica, e `checar(condição, mensagem)` registra uma violação quando alguma regra é quebrada (por exemplo, dois robôs se movendo ao mesmo tempo).
- `aguardarTermino(timeout, threads)` espera as threads terminarem; se alguma não terminar no prazo, registra **DEADLOCK**.
- Ao final, os totais são conferidos (itens produzidos = consumidos, cigarros = rodadas etc.).

A suíte `testes.TodosOsTestes` executa cada problema dezenas de vezes com pausas próximas de 0 ms, o que maximiza as trocas de contexto e a chance de expor condições de corrida. Ela também roda uma **contraprova**: os filósofos sem a estratégia anti-deadlock, para mostrar que o detector de fato acusa o deadlock.

Saída esperada de `run testes`:

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
