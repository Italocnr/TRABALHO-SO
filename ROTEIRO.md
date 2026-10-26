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

## 3. Roteiro do vídeo (2 min por pessoa, ~8 min no total)

Cada pessoa fala **no máximo 2 minutos**, sem interrupção. Siga os passos na ordem:
📂 = arquivo para abrir · 🖱️ = linhas para selecionar · ⌨️ = comando no terminal · 🎙️ = o que falar.

### 3.1 Antes de apertar o REC (fazer uma vez só)

1. Abra a pasta `TRABALHO-SO` no **VS Code** (`File > Open Folder`).
2. Abra o terminal: **Ctrl + '** (PowerShell, já na pasta do projeto).
3. Aumente a fonte: **Ctrl + =** algumas vezes.
4. Teste: `.\run.ps1 robos`. Se aparecer `Bart`, `Lisa`, `Maggie`, está tudo certo.
   * Se o PowerShell bloquear o script: `Set-ExecutionPolicy -Scope Process Bypass`
5. **Não precisa compilar à parte:** o `.\run.ps1 <opção>` compila e executa.
6. Antes de cada execução, digite `cls` para limpar o terminal.

**Regras do enunciado:** todos aparecem e falam (faltou um = zero para o grupo), e cada um diz **o próprio nome** e **o que fez**.
**Vocabulário:** `acquire()` = **down** · `release()` = **up**.

| Ordem | Integrante                                | Parte                                              | Tempo |
|-------|-------------------------------------------|----------------------------------------------------|-------|
| 1     | João Victor Vial Leite Soares             | Abertura · Parte I · Produtor/Consumidor semáforo  | 2 min |
| 2     | Ítalo Eduardo Carneiro da Silva           | Barbeiro · Filósofos                               | 2 min |
| 3     | Guilherme Augusto Martins de Carvalho     | Leitores/Escritores · Fumantes                     | 2 min |
| 4     | Vinicius Matos Oliveira Rocha             | Selvagens · Robôs · Testes + encerramento          | 2 min |

---

### 👤 1. João Victor — Abertura + Produtor/Consumidor (2 min)

**Abertura (20 s).** 🎙️ "Somos João Victor, Ítalo, Guilherme e Vinicius, e este é o TP1 de Sistemas Operacionais, em Java. Eu sou o João Victor e fiz a Parte I e o produtor/consumidor com semáforos. Cada problema tem uma classe com o recurso compartilhado e os semáforos, e uma classe por tipo de thread."

**Parte I — monitor (40 s)**

1. 📂 [Buffer.java](src/ProdutorConsumidor/Buffer.java) → 🖱️ linhas **20–31**
   🎙️ "O buffer tem uma posição. Os métodos são `synchronized`, então só uma thread entra por vez. Se está cheio, o produtor faz `wait()`. Quando grava, `notifyAll()` acorda o consumidor."
2. ⌨️ `cls` → `.\run.ps1 prodcons`
   🎙️ "Sempre alterna `put`, `get`: nunca dois `put` seguidos."

**Produtor/Consumidor com semáforos (1 min)**

1. 📂 [BufferLimitado.java](src/prodconssemaforo/BufferLimitado.java) → 🖱️ linhas **34–48**
   🎙️ "Três semáforos: `vazios` conta as vagas, `cheios` conta os itens e `mutex` protege o vetor. O produtor faz down em `vazios`, depois no `mutex`, grava, e faz up em `cheios`. **Contra deadlock:** o semáforo de contagem vem **antes** do mutex, para ninguém dormir segurando o mutex."
2. ⌨️ `cls` → `.\run.ps1 prodcons-sem`
   🎙️ "A ocupação nunca passa de 3/3, e a validação confere que os 12 itens foram consumidos uma vez cada."

---

### 👤 2. Ítalo — Barbeiro + Filósofos (2 min)

🎙️ "Eu sou o Ítalo e fiz o Barbeiro Dorminhoco e o Jantar dos Filósofos."

**Barbeiro (1 min)**

1. 📂 [Barbearia.java](src/barbeiro/Barbearia.java) → 🖱️ linhas **81–105**
   🎙️ "O barbeiro dorme no semáforo `clientes`. O cliente pega o `mutex`: se não tem cadeira, vai embora; se tem, senta e faz up em `clientes`, que acorda o barbeiro. Depois espera em `barbeiros`. **Contra deadlock:** ninguém bloqueia segurando o mutex."
2. ⌨️ `cls` → `.\run.ps1 barbeiro`
   🎙️ "O barbeiro dorme, acorda quando chega cliente, as cadeiras enchem até 3/3 e o próximo encontra a barbearia **LOTADA** e vai embora."

**Filósofos (1 min)**

1. ⌨️ `cls` → `.\run.ps1 filosofos-ingenuo` (demora ~3 s)
   🎙️ "Sem estratégia, cada filósofo pega um palito e espera o do vizinho: **espera circular, deadlock**."
2. 📂 [Mesa.java](src/filosofos/Mesa.java) → 🖱️ linhas **47–69**
   🎙️ "Cada palito é um semáforo iniciado em 1. A solução: pegar primeiro o palito de **menor índice**. O filósofo 4 pega o p0 antes do p4 e quebra o ciclo."
3. ⌨️ `cls` → `.\run.ps1 filosofos`
   🎙️ "Agora todos comem e vizinhos nunca comem juntos."

---

### 👤 3. Guilherme — Leitores/Escritores + Fumantes (2 min)

🎙️ "Eu sou o Guilherme e fiz Leitores e Escritores e o problema dos Fumantes."

**Leitores e Escritores (1 min)**

1. 📂 [MemoriaCompartilhada.java](src/leitoresescritores/MemoriaCompartilhada.java) → 🖱️ linhas **42–80**
   🎙️ "O vetor M tem soma sempre 20. O primeiro leitor tranca o semáforo `recurso` e os outros leitores entram juntos. O escritor passa pela catraca `fila`, que impede leitores de passarem na frente, e escreve sozinho. **Contra deadlock:** todos pegam os semáforos na mesma ordem."
2. ⌨️ `cls` → `.\run.ps1 leitores`
   🎙️ "Vários leitores leem no mesmo milissegundo, e toda leitura mostra `soma=20`."

**Fumantes (1 min)**

1. 📂 [Mesa.java](src/fumantes/Mesa.java) → 🖱️ linhas **38–71**
   🎙️ "Cada fumante tem um semáforo que começa em 0. O representante coloca dois ingredientes e acorda **só o fumante que tem o que falta**. Quando ele termina de fumar, libera o representante. **Contra deadlock:** os dois ingredientes são entregues juntos."
2. ⌨️ `cls` → `.\run.ps1 fumantes`
   🎙️ "Em cada rodada fuma o fumante certo, e nunca dois ao mesmo tempo."

---

### 👤 4. Vinicius — Selvagens + Robôs + Testes (2 min)

🎙️ "Eu sou o Vinicius e fiz o Jantar dos Selvagens, os Três Robôs e a suíte de testes."

**Selvagens (40 s)**

1. 📂 [Caldeirao.java](src/selvagens/Caldeirao.java) → 🖱️ linhas **45–76**
   🎙️ "O `mutex` faz um selvagem se servir por vez. Se o caldeirão está vazio, ele acorda o cozinheiro e espera. O cozinheiro **não usa o mutex**, então não há deadlock."
2. ⌨️ `cls` → `.\run.ps1 selvagens`
   🎙️ "As porções chegam a 0, o cozinheiro enche e os selvagens voltam a se servir."

**Robôs (40 s)**

1. 📂 [Controle.java](src/robos/Controle.java) → 🖱️ linhas **26–28**
   🎙️ "Um semáforo por robô: `semBart = 1`, os outros 0. É um bastão passado de mão em mão. A Lisa alterna entre passar para a Maggie e para o Bart. Quem não tem a vez dorme no `acquire()`, sem espera ocupada."
2. ⌨️ `cls` → `.\run.ps1 robos`
   🎙️ "A ordem sai sempre Bart, Lisa, Maggie, Lisa."

**Testes + encerramento (40 s)**

1. ⌨️ `cls` → `.\run.ps1 testes` (**demora ~30 s**, fale enquanto roda)
   🎙️ "A suíte roda cada problema dezenas de vezes e acusa deadlock se alguma thread travar. Tudo passou, e a última linha é a contraprova: os filósofos sem solução, onde o deadlock foi detectado. Obrigado!"

---

### Cola rápida dos comandos

```powershell
.\run.ps1 prodcons            # João Victor
.\run.ps1 prodcons-sem        # João Victor
.\run.ps1 barbeiro            # Ítalo
.\run.ps1 filosofos-ingenuo   # Ítalo (deadlock, ~3 s)
.\run.ps1 filosofos           # Ítalo
.\run.ps1 leitores            # Guilherme
.\run.ps1 fumantes            # Guilherme
.\run.ps1 selvagens           # Vinicius
.\run.ps1 robos               # Vinicius
.\run.ps1 testes              # Vinicius (~30 s)
```

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
