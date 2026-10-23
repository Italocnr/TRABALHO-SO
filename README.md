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

## 3. Roteiro do vídeo

### 3.1 Regras do enunciado que o vídeo precisa cumprir

* **Todos os integrantes aparecem e falam individualmente.** Ausência de qualquer um = nota zero para o grupo.
* Cada integrante diz **o próprio nome** e **qual foi a sua contribuição**.
* O vídeo precisa cobrir, no mínimo: modelagem; threads e suas linhas de execução; recursos compartilhados e regiões críticas; uso dos semáforos; estratégia contra deadlock; execução e resultados dos testes. A tabela de modelagem de cada problema abaixo cobre esses itens na ordem.

### 3.2 Divisão sugerida (5 integrantes, ~4–6 min cada)

| Integrante | Parte                                                                                          |
|------------|------------------------------------------------------------------------------------------------|
| 1          | Abertura + Parte I (monitor) + Produtor/Consumidor com semáforos + estrutura do projeto        |
| 2          | b) Barbeiro Dorminhoco                                                                         |
| 3          | c) Leitores e Escritores + d) Fumantes                                                         |
| 4          | e) Filósofos (incluindo a demonstração do deadlock) + f) Selvagens                             |
| 5          | g) Três Robôs + suíte de testes completa + encerramento                                        |

Grupo menor? Junte blocos (ex.: com 3 pessoas: 1+2, 3+4, 5). O que importa é cada pessoa explicar o que fez.

### 3.3 Dicas de gravação

* Deixe o editor com fonte grande e o terminal ao lado. Abra os arquivos **antes** de gravar.
* Quando falar de uma linha, **selecione/realce a linha** no editor.
* Antes de executar, limpe o terminal (`cls` / `clear`) para a saída começar do topo.
* A saída varia a cada execução (threads!). Isso é **esperado**: diga isso no vídeo. As frases do roteiro abaixo descrevem o que procurar, não uma saída fixa.
* Vocabulário: `acquire()` = **down / P / wait** do semáforo; `release()` = **up / V / signal**. Diga os dois nomes na primeira vez.

Nos blocos abaixo, **[TELA]** indica o que mostrar e **🎙️** o que falar.

---

### BLOCO 0 — Abertura (Integrante 1, ~1 min)

**[TELA]** README aberto na seção 2 (organização do código).

🎙️ "Olá, nós somos o grupo formado por *[nomes]*. Este é o Trabalho Prático 1 de Sistemas Operacionais, sobre comunicação e sincronização de threads. Implementamos tudo em Java. Na Parte I executamos o produtor/consumidor do enunciado, que usa monitor. Na Parte II implementamos, com **semáforos**, o produtor/consumidor, o barbeiro dorminhoco, leitores e escritores, fumantes, filósofos, o jantar dos selvagens e os três robôs."

🎙️ "Todos os problemas seguem o mesmo padrão: uma classe com o **recurso compartilhado**, que guarda os semáforos e tem as **regiões críticas** marcadas com comentário; uma classe por **tipo de thread**, que estende `Thread` como na Parte I; e uma classe principal que cria as threads e **valida** o resultado. Cada linha impressa mostra o tempo em milissegundos e o **nome da thread** que imprimiu, para dar para acompanhar quem fez o quê."

---

### BLOCO 1 — Parte I: Produtor/Consumidor com monitor (Integrante 1, ~3 min)

**Modelagem**

| Item                 | Neste problema                                                                               |
|----------------------|----------------------------------------------------------------------------------------------|
| Threads              | 1 `Produtor` (coloca 0..4) e 1 `Consumidor` (retira 5 valores)                               |
| Recurso compartilhado| `Buffer` com **uma única posição** (`valor`) e o flag `cheio`                                |
| Região crítica       | Os métodos `get()` e `put()` inteiros (são `synchronized`)                                    |
| Sincronização        | Monitor do Java: `synchronized` + `wait()` + `notifyAll()`                                   |

**[TELA]** [Buffer.java](src/ProdutorConsumidor/Buffer.java)

🎙️ "O buffer guarda um único valor e um booleano `cheio`. Os dois métodos são `synchronized`: só uma thread por vez executa dentro do objeto Buffer. Isso é a exclusão mútua do **monitor**."

**[TELA]** realçar [Buffer.java:20-31](src/ProdutorConsumidor/Buffer.java#L20-L31) (`put`)

🎙️ "No `put`, enquanto o buffer estiver cheio, o produtor chama `wait()`: ele libera o monitor e dorme. Quando há espaço, grava o valor, marca `cheio = true`, imprime `put` e chama `notifyAll()` para acordar quem estiver esperando, no caso o consumidor."

**[TELA]** realçar [Buffer.java:7-18](src/ProdutorConsumidor/Buffer.java#L7-L18) (`get`)

🎙️ "O `get` é o espelho: enquanto estiver vazio, o consumidor espera. Depois marca `cheio = false`, avisa com `notifyAll()`, imprime `get` e retorna o valor. Usamos `while` e não `if` porque, ao acordar, a thread precisa testar a condição de novo: outra thread pode ter mudado o estado antes dela."

**[TELA]** [Produtor.java](src/ProdutorConsumidor/Produtor.java), [Consumidor.java](src/ProdutorConsumidor/Consumidor.java), [ProdCons.java](src/ProdutorConsumidor/ProdCons.java)

🎙️ "O produtor faz 5 `put`, com i de 0 a 4, dormindo um tempo aleatório entre eles. O comentário do enunciado diz 10 valores, mas o laço vai até 5. O consumidor faz 5 `get`. O `main` cria um buffer e inicia uma thread de cada."

**[TELA]** executar `.\run.ps1 prodcons`

🎙️ "Na tela, cada valor aparece primeiro como `put` e depois como `get`, sempre alternando: **nunca dois `put` seguidos** (o buffer só tem uma posição) e **nunca um `get` antes do `put` correspondente**. As linhas `Produtor #0 put` e `Consumidor #0 get` às vezes trocam de ordem entre si. Isso acontece porque esses `println` estão **fora** do `synchronized`, depois que o método já retornou: as duas threads competem pela CPU e qualquer uma pode imprimir primeiro. Já as linhas `put`/`get`, impressas **dentro** do monitor, estão sempre na ordem certa."

---

### BLOCO 2 — Produtor/Consumidor com semáforos (Integrante 1, ~3 min)

**Modelagem**

| Item                  | Neste problema                                                                                 |
|-----------------------|------------------------------------------------------------------------------------------------|
| Threads               | N `Produtor` (cada um gera itens `id*1000 + i`), M `Consumidor`                                |
| Recurso compartilhado | `BufferLimitado`: vetor circular de tamanho fixo + `entrada`, `saida`, `ocupados`              |
| Regiões críticas      | Corpo de `inserir()` e de `remover()` entre `mutex.acquire()` e `mutex.release()`              |
| Semáforos             | `vazios` = capacidade (vagas livres) · `cheios` = 0 (itens prontos) · `mutex` = 1              |
| Anti-deadlock         | Pegar **primeiro** o semáforo de contagem e **depois** o `mutex`                               |

**[TELA]** [BufferLimitado.java:19-31](src/prodconssemaforo/BufferLimitado.java#L19-L31)

🎙️ "Agora o buffer tem várias posições, e usamos três semáforos. `vazios` começa com o tamanho do buffer e conta as vagas livres. `cheios` começa em zero e conta os itens prontos para consumo. `mutex` começa em 1 e garante exclusão mútua nos índices do vetor."

**[TELA]** realçar [BufferLimitado.java:34-48](src/prodconssemaforo/BufferLimitado.java#L34-L48)

🎙️ "Para inserir, o produtor faz **down em `vazios`**: se não houver vaga, ele bloqueia aqui. Depois faz **down no `mutex`** e entra na região crítica, onde grava o item e avança o índice. Sai com **up no `mutex`** e faz **up em `cheios`**, avisando que existe um item novo. O `remover` é simétrico: down em `cheios`, mutex, retira, e up em `vazios`."

🎙️ **(deadlock)** "A ordem importa. Se o produtor pegasse o `mutex` primeiro e depois bloqueasse em `vazios` com o buffer cheio, ele dormiria **segurando o mutex**. O consumidor nunca conseguiria entrar para liberar uma vaga: **deadlock**. Por isso sempre esperamos pelo semáforo de contagem **fora** da região crítica."

**[TELA]** executar `.\run.ps1 prodcons-sem`

🎙️ "Cada linha mostra a ocupação, por exemplo `buffer 3/3`. Repare que ela **nunca passa de 3** nem fica negativa: quando chega a 3/3, a próxima linha é sempre um `get`, porque os produtores ficaram bloqueados em `vazios`. Os itens 0, 1, 2 vêm do Produtor-0 e 1000, 1001… do Produtor-1. No final, a validação confere que os 12 itens produzidos foram consumidos **exatamente uma vez**, nenhum perdido ou duplicado, e que o buffer terminou vazio."

---

### BLOCO 3 — b) Barbeiro Dorminhoco (Integrante 2, ~5 min)

**Modelagem**

| Item                  | Neste problema                                                                                                         |
|-----------------------|------------------------------------------------------------------------------------------------------------------------|
| Threads               | 1 `Barbeiro` (laço infinito) e N `Cliente` (cada um executa uma vez e termina)                                         |
| Recurso compartilhado | Sala de espera (`esperando`, `cadeiras`) e a cadeira do barbeiro (`clienteNaCadeira`)                                   |
| Região crítica        | Leitura/alteração de `esperando`, protegida por `mutex`                                                                |
| Semáforos             | `clientes`=0 (*customers*) · `barbeiros`=0 (*barbers*) · `mutex`=1 · `clienteSentado`=0 · `corteConcluido`=0           |
| Anti-deadlock         | Ninguém bloqueia segurando o `mutex`; cliente sem cadeira vai embora em vez de esperar                                  |

**[TELA]** [Barbearia.java:14-35](src/barbeiro/Barbearia.java#L14-L35)

🎙️ "Usamos a solução do Tanenbaum. `clientes` conta quantos clientes estão esperando e é onde o barbeiro **dorme**. `barbeiros` indica se o barbeiro está livre (0 ou 1) e é onde o cliente espera a vez. `mutex` protege a variável `esperando`, que é uma cópia legível do semáforo `clientes`: em semáforos clássicos não dá para ler o valor, e o cliente precisa saber quantos estão esperando para decidir se fica."

🎙️ "Acrescentamos dois semáforos de **encontro** (rendezvous). `clienteSentado` é o cliente avisando ao barbeiro que sentou na cadeira, e `corteConcluido` é o barbeiro avisando ao cliente que terminou. O enunciado diz que o cliente sai quando o corte termina, e sem eles o cliente iria embora antes do corte acabar."

**[TELA]** realçar [Barbearia.java:52-78](src/barbeiro/Barbearia.java#L52-L78) (linha de execução do barbeiro)

🎙️ "Esta é a linha de execução do **barbeiro**, um laço infinito. Na linha 61 ele faz **down em `clientes`**. Se não houver ninguém, o semáforo está em zero e o barbeiro **dorme** ali. Quando acorda, entra na região crítica com o `mutex`, decrementa `esperando`, porque um cliente sai da sala de espera, faz **up em `barbeiros`** para dizer 'estou pronto' e solta o mutex. Depois espera o cliente sentar, corta o cabelo, simulado com um `sleep`, e faz up em `corteConcluido`."

**[TELA]** realçar [Barbearia.java:81-105](src/barbeiro/Barbearia.java#L81-L105) (linha de execução do cliente)

🎙️ "Agora o **cliente**. Ele pega o `mutex` e confere se `esperando < cadeiras`. Se **não houver cadeira**, solta o mutex e vai embora: é o `else`. Se houver, incrementa `esperando`, faz **up em `clientes`**, que é o que **acorda o barbeiro**, e solta o mutex. Só depois faz **down em `barbeiros`** e espera o barbeiro ficar livre. Quando é sua vez, senta, avisa e espera o corte terminar. Não há laço: cada cliente corta o cabelo uma vez só."

🎙️ **(regiões críticas e deadlock)** "A região crítica é só o acesso a `esperando`, sempre entre `mutex.acquire` e `mutex.release`. O ponto principal contra deadlock: o cliente **solta o mutex antes** de bloquear em `barbeiros`, e o barbeiro solta o mutex **antes** de cortar. Ninguém dorme segurando o mutex, então outros clientes sempre conseguem entrar para sentar ou desistir. E como um cliente sem cadeira vai embora em vez de esperar, nunca se forma uma espera circular."

**[TELA]** executar `.\run.ps1 barbeiro` (3 cadeiras, 15 clientes)

🎙️ "Logo no início aparece `Nenhum cliente esperando -> barbeiro vai DORMIR`. Quando o Cliente-1 chega, ele senta na sala de espera e o barbeiro **acorda** logo em seguida: é o up em `clientes`. Enquanto o barbeiro corta, os próximos clientes ocupam as cadeiras, e vemos `1/3`, `2/3`, `3/3`. Quando estão todas ocupadas, o próximo cliente imprime `encontrou a barbearia LOTADA e foi embora`. Cada `CORTANDO o cabelo do Cliente-X` vem depois de `Cliente-X sentou na cadeira`, e o `Cliente-X saiu` vem depois de `terminou o corte`. No final o barbeiro volta a dormir, e quando todos os clientes terminam, o programa fecha a barbearia."

🎙️ "A validação confere que atendidos + desistentes = total de clientes, que o número de cortes é igual ao de atendidos (nenhum cliente teve o cabelo cortado duas vezes), que a espera **nunca passou do número de cadeiras** e que ninguém ficou travado."

---

### BLOCO 4 — c) Leitores e Escritores (Integrante 3, ~4 min)

**Modelagem**

| Item                  | Neste problema                                                                                               |
|-----------------------|--------------------------------------------------------------------------------------------------------------|
| Threads               | N `Leitor` (read M ou read M[i]) e K `Escritor` (M = [..] ou M[i]-=x, M[j]+=x)                               |
| Recurso compartilhado | Vetor `M = [3, 7, 1, 9]` (o da figura); a soma de M deve ser sempre **20**                                   |
| Regiões críticas      | Leitura: entre `iniciarLeitura()`/`terminarLeitura()` (compartilhada) · Escrita: entre `iniciarEscrita()`/`terminarEscrita()` (exclusiva) · contador `leitores` (protegido por `mutexLeitores`) |
| Semáforos             | `recurso`=1 · `mutexLeitores`=1 · `fila`=1 (justo/FIFO)                                                       |
| Anti-deadlock         | Ordem fixa de aquisição: `fila` → `mutexLeitores` → `recurso`; o escritor nunca precisa de `mutexLeitores`   |

**[TELA]** [MemoriaCompartilhada.java:12-30](src/leitoresescritores/MemoriaCompartilhada.java#L12-L30)

🎙️ "A memória compartilhada é o vetor M da figura. Para os testes, criamos uma regra: a soma dos elementos é sempre 20. O escritor altera M em **vários passos**, com pausas entre eles, então se um leitor lesse durante uma escrita, veria uma soma diferente de 20. Isso é uma condição de corrida que fica **visível**."

🎙️ "São três semáforos. `recurso` dá acesso exclusivo a M. `mutexLeitores` protege o contador `leitores`. `fila` é uma catraca justa, por ordem de chegada, que evita que os escritores esperem para sempre."

**[TELA]** realçar [MemoriaCompartilhada.java:42-65](src/leitoresescritores/MemoriaCompartilhada.java#L42-L65)

🎙️ "Para ler, o leitor passa pela catraca `fila`, pega `mutexLeitores` e incrementa `leitores`. Se for o **primeiro** leitor, faz down em `recurso`: ele tranca M **contra escritores**, mas para o grupo todo de leitores. Depois solta o mutex e a catraca, então **outros leitores entram juntos**. Ao terminar, decrementa o contador, e o **último** leitor a sair faz up em `recurso`, liberando os escritores."

**[TELA]** realçar [MemoriaCompartilhada.java:67-80](src/leitoresescritores/MemoriaCompartilhada.java#L67-L80)

🎙️ "O escritor pega a catraca `fila`, o que **impede que novos leitores entrem na frente dele**, e então faz down em `recurso`, esperando os leitores atuais saírem. Com isso ele escreve **sozinho**. Sem a `fila`, um fluxo contínuo de leitores poderia manter `recurso` ocupado para sempre e o escritor nunca escreveria: é a inanição (starvation)."

🎙️ **(deadlock)** "Todos pegam os semáforos sempre na mesma ordem, `fila`, depois `mutexLeitores`, depois `recurso`, e o escritor nem usa `mutexLeitores`. Sem ordem circular, não existe deadlock."

**[TELA]** executar `.\run.ps1 leitores`

🎙️ "Primeiro mostrem o paralelismo: aparecem vários `read M = [3, 7, 1, 9]` de leitores diferentes **no mesmo milissegundo**, ou seja, eles leram **juntos**. Depois, as linhas `write` dos escritores nunca se misturam com leituras. Toda leitura completa termina com `soma=20`, mesmo o escritor mudando o vetor em várias etapas. Na validação, o `máximo de leitores simultâneos` maior que 1 prova que a leitura é paralela, e o `OK` prova que nenhum leitor entrou com escritor ativo e nunca houve dois escritores juntos."

---

### BLOCO 5 — d) Fumantes (Integrante 3, ~3 min)

**Modelagem**

| Item                  | Neste problema                                                                                         |
|-----------------------|--------------------------------------------------------------------------------------------------------|
| Threads               | 3 `Fumante` (TABACO, PAPEL, FOSFORO) e 1 `Representante`                                               |
| Recurso compartilhado | A mesa (`ingredientesNaMesa`)                                                                           |
| Região crítica        | Colocar ingredientes (representante) e retirá-los (fumante); o acesso é alternado pelos semáforos      |
| Semáforos             | `fumante[TABACO]`, `fumante[PAPEL]`, `fumante[FOSFORO]` = 0 · `representante` = 0                      |
| Anti-deadlock         | O fumante nunca pega um ingrediente por vez: o representante acorda diretamente o **único** fumante que pode fumar |

**[TELA]** [Mesa.java:13-31](src/fumantes/Mesa.java#L13-L31)

🎙️ "Cada fumante fica bloqueado no semáforo do ingrediente **que ele possui**, e todos começam em zero, como diz o enunciado: os três começam bloqueados. O semáforo `representante` é onde o representante bloqueia depois de servir a mesa."

**[TELA]** [Representante.java:19-35](src/fumantes/Representante.java#L19-L35) e [Mesa.java:38-48](src/fumantes/Mesa.java#L38-L48)

🎙️ "O representante sorteia o ingrediente que vai **faltar**, coloca os outros dois na mesa e faz **up apenas no semáforo do fumante que tem o ingrediente que falta**. Em seguida faz **down em `representante`** e bloqueia."

**[TELA]** [Fumante.java:20-33](src/fumantes/Fumante.java#L20-L33) e [Mesa.java:51-71](src/fumantes/Mesa.java#L51-L71)

🎙️ "O fumante acorda, retira os dois ingredientes da mesa, faz o cigarro e fuma por um tempo aleatório. **Quando termina de fumar**, faz up em `representante`, que volta a servir a mesa, e o ciclo se repete."

🎙️ **(deadlock)** "Na versão clássica, se cada fumante pegasse um ingrediente por vez, dois fumantes poderiam segurar um ingrediente cada e esperar o outro para sempre. Aqui isso não acontece porque o representante entrega os dois ingredientes **de uma vez** e acorda **só** o fumante certo. Em cada momento existe exatamente uma thread com a vez: ou o representante, ou um fumante."

**[TELA]** executar `.\run.ps1 fumantes`

🎙️ "Cada rodada segue o padrão: `Colocou FOSFORO + TABACO na mesa -> acorda o fumante que tem PAPEL`, depois o `Fumante-PAPEL` pega exatamente esses dois, fuma, e só depois de `Terminou de fumar` aparece a próxima rodada. **Nunca há dois fumantes fumando ao mesmo tempo** e o fumante acordado é sempre o certo. A validação confere isso e que o número de cigarros é igual ao de rodadas."

---

### BLOCO 6 — e) Jantar dos Filósofos (Integrante 4, ~5 min)

**Modelagem**

| Item                  | Neste problema                                                                                          |
|-----------------------|---------------------------------------------------------------------------------------------------------|
| Threads               | 5 `Filosofo` (f0..f4): laço de meditar → pegar palitos → comer → devolver                                |
| Recurso compartilhado | 5 palitos p0..p4; fi usa p(i) (direita) e p(i+1) (esquerda)                                             |
| Região crítica        | Comer (entre pegar e devolver os dois palitos)                                                          |
| Semáforos             | `palitos[0..4]`, cada um binário iniciado em 1 (1 = palito livre)                                        |
| Anti-deadlock         | **Ordenação de recursos**: todos pegam primeiro o palito de **menor índice** (f4 pega p0 antes de p4)    |

**[TELA]** [Mesa.java:12-36](src/filosofos/Mesa.java#L12-L36)

🎙️ "Cada palito é um semáforo binário que começa em 1, o que significa palito livre. O filósofo fi usa o palito da direita, p(i), e o da esquerda, p(i+1) mod 5, exatamente como na figura."

**[TELA]** executar primeiro `.\run.ps1 filosofos-ingenuo`

🎙️ "Primeiro, o problema. Nesta versão ingênua, todo filósofo pega primeiro o palito da direita. Veja a saída: os cinco imprimem `Pegou pX, tentando pegar pY`, e depois **nada mais acontece**. Cada um segura um palito e espera o do vizinho, que também espera o do vizinho, formando uma **espera circular**: é o **deadlock**. Depois de 3 segundos, nosso verificador percebe que nenhuma thread terminou, mostra `possível DEADLOCK` e ninguém comeu."

**[TELA]** realçar [Mesa.java:47-69](src/filosofos/Mesa.java#L47-L69)

🎙️ "A solução está aqui: com `ordenado = true`, o filósofo pega primeiro o palito de **menor índice**. Para f0 a f3 isso é o palito da direita, mas para o **f4**, que usa p4 e p0, o primeiro é o **p0**. Ele vai na direção contrária e **quebra o ciclo**: é impossível os cinco segurarem um palito cada e esperarem em círculo. Isso elimina a condição de 'espera circular' do deadlock."

🎙️ "Quando consegue os dois palitos, o filósofo entra na região crítica, que é comer. Em `devolverPalitos` ele faz up nos dois semáforos, e os vizinhos que estavam bloqueados podem continuar."

**[TELA]** executar `.\run.ps1 filosofos`

🎙️ "Agora todos comem. Repare no Filosofo-4: ele imprime `tentando pegar p0` primeiro. Nunca aparecem dois vizinhos `COMENDO` ao mesmo tempo, mas filósofos **não vizinhos**, como f0 e f2, comem juntos. A validação confere que nenhum filósofo comeu ao mesmo tempo que um vizinho e que **todos** fizeram as 3 refeições, sem deadlock e sem ninguém passando fome."

---

### BLOCO 7 — f) Jantar dos Selvagens (Integrante 4, ~4 min)

**Modelagem**

| Item                  | Neste problema                                                                                                  |
|-----------------------|-----------------------------------------------------------------------------------------------------------------|
| Threads               | N `Selvagem` (servir → comer, R vezes) e 1 `Cozinheiro` (laço infinito: dormir → encher)                        |
| Recurso compartilhado | O caldeirão (`porcoes`, com capacidade N)                                                                       |
| Região crítica        | `servir()`: testar se está vazio e retirar uma porção                                                           |
| Semáforos             | `mutex`=1 · `caldeiraoVazio`=0 (cozinheiro dorme) · `caldeiraoCheio`=0 (selvagem espera a recarga)               |
| Anti-deadlock         | O selvagem espera o cozinheiro segurando o `mutex`, mas o cozinheiro **não** precisa do `mutex`                 |

**[TELA]** [Caldeirao.java:9-30](src/selvagens/Caldeirao.java#L9-L30)

🎙️ "O caldeirão começa com N porções. `mutex` garante que **só um selvagem se sirva por vez**. `caldeiraoVazio` é onde o cozinheiro **dorme**. `caldeiraoCheio` é onde o selvagem espera enquanto o cozinheiro cozinha."

**[TELA]** realçar [Caldeirao.java:45-63](src/selvagens/Caldeirao.java#L45-L63)

🎙️ "O selvagem pega o `mutex`. Se `porcoes == 0`, faz **up em `caldeiraoVazio`** para acordar o cozinheiro e **down em `caldeiraoCheio`** para esperar. Depois retira uma porção e solta o mutex. Repare que ele espera o cozinheiro **segurando o mutex**. É de propósito: assim nenhum outro selvagem tenta se servir de um caldeirão vazio e ninguém acorda o cozinheiro duas vezes."

**[TELA]** realçar [Caldeirao.java:66-76](src/selvagens/Caldeirao.java#L66-L76) e [Selvagem.java:26-40](src/selvagens/Selvagem.java#L26-L40)

🎙️ "O cozinheiro dorme em `caldeiraoVazio`, enche o caldeirão e faz up em `caldeiraoCheio`, devolvendo o controle ao selvagem que esperava. Como o cozinheiro **não usa o mutex**, segurar o mutex durante a espera não causa deadlock. E o cozinheiro só é acordado quando o caldeirão está vazio. Já o **comer** acontece fora da região crítica, então vários selvagens comem ao mesmo tempo."

**[TELA]** executar `.\run.ps1 selvagens`

🎙️ "Vemos `Serviu-se de uma porção (restam 4/5)`, `3/5`… até `0/5`. O próximo selvagem imprime `Caldeirão VAZIO -> acorda o cozinheiro e espera`, o cozinheiro acorda, enche, imprime `Caldeirão CHEIO` e volta a dormir. Só então o selvagem se serve: `restam 4/5`. As linhas `COMENDO (3 selvagens comendo agora)` mostram que **comem juntos**, mas **se servem um por vez**. A validação confere as três restrições do enunciado: nunca dois se servindo juntos, nunca alguém se servindo de caldeirão vazio e o cozinheiro só enchendo com o caldeirão vazio. Também confere que o número de recargas é exatamente o esperado."

---

### BLOCO 8 — g) Três Robôs (Integrante 5, ~3 min)

**Modelagem**

| Item                  | Neste problema                                                                                   |
|-----------------------|--------------------------------------------------------------------------------------------------|
| Threads               | `Bart`, `Lisa`, `Maggie`                                                                         |
| Recurso compartilhado | O "direito de se mover" (`move()` só pode ser executado por um robô por vez)                      |
| Região crítica        | `move()`                                                                                          |
| Semáforos             | `semBart`=1 · `semLisa`=0 · `semMaggie`=0                                                         |
| Sem espera ocupada    | Quem não tem a vez fica **bloqueado** no `acquire()`, sem laço testando variável                  |

**[TELA]** [Controle.java:13-29](src/robos/Controle.java#L13-L29)

🎙️ "A sequência é Bart, Lisa, Maggie, Lisa, e repete. Usamos um semáforo por robô. Os valores iniciais são **semBart = 1**, porque Bart é o primeiro, e **semLisa = semMaggie = 0**. Pense nisso como um **bastão** passado de mão em mão: existe só uma permissão circulando, então só um robô se move por vez."

**[TELA]** [Bart.java:14-24](src/robos/Bart.java#L14-L24), [Maggie.java:14-24](src/robos/Maggie.java#L14-L24), [Lisa.java:14-28](src/robos/Lisa.java#L14-L28)

🎙️ "Bart espera `semBart`, se move e passa a vez para a Lisa. Maggie espera `semMaggie`, se move e também passa para a Lisa. O truque está na **Lisa**: o laço dela tem **duas etapas**. Na primeira ela se move e passa para a Maggie; na segunda se move e passa para o Bart. Assim ela alterna os destinos **sem nenhuma variável compartilhada**. Não há espera ocupada: quem não tem a vez fica dormindo no `acquire()`, sem gastar CPU."

🎙️ **(deadlock)** "Sempre existe exatamente uma permissão entre os três semáforos, e quem a recebe sempre a repassa depois de se mover. Por isso o sistema nunca trava."

**[TELA]** executar `.\run.ps1 robos`

🎙️ "No `main`, iniciamos as threads de propósito na ordem Maggie, Lisa, Bart, e mesmo assim o primeiro movimento é do Bart, porque quem define a ordem são os semáforos, não o `start()`. A saída mostra `Bart`, `Lisa`, `Maggie`, `Lisa`, `Bart`… com o número do movimento, e a validação confere a sequência inteira e que nunca dois robôs se moveram ao mesmo tempo."

---

### BLOCO 9 — Suíte de testes e encerramento (Integrante 5, ~3 min)

**[TELA]** [TodosOsTestes.java](src/testes/TodosOsTestes.java) e [Verificador.java:33-53](src/util/Verificador.java#L33-L53)

🎙️ "Para validar de forma automática, cada problema registra as suas regras no `Verificador`. Por exemplo: 'nunca mais de 3 clientes esperando', 'nunca leitor junto com escritor', 'nunca dois vizinhos comendo'. Se alguma regra for quebrada, uma violação é registrada. O método `aguardarTermino` espera as threads e, se alguma **não terminar no prazo**, acusa **DEADLOCK**."

🎙️ "A suíte roda cada problema dezenas de vezes com tempos quase zero. Isso força o máximo de trocas de contexto entre as threads, que é a situação em que condições de corrida aparecem. Também testamos casos extremos, como buffer de uma posição e caldeirão de uma porção."

**[TELA]** executar `.\run.ps1 testes`

🎙️ "Todos os cenários aparecem como `PASSOU`: centenas de execuções sem nenhuma violação e sem nenhum deadlock. A última linha é uma **contraprova**: rodamos os filósofos **sem** a estratégia anti-deadlock, e o verificador **detectou o deadlock**. Isso mostra que os testes realmente pegam falhas, e que o `OK` dos outros cenários significa que a sincronização funciona."

🎙️ "Resumindo: usamos threads para cada entidade dos problemas, semáforos para exclusão mútua (os `mutex`) e para sinalização entre threads (como `clientes`, `vazios` e `caldeiraoVazio`). Evitamos deadlock nunca bloqueando com o mutex na mão quando outra thread precisa dele, mantendo uma ordem fixa de aquisição e quebrando a espera circular nos filósofos. Obrigado!"

---

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
