# 05. Contrato da API

Especificação dos endpoints do Passo 4. Quem consome a sua API (Postman, front-end, outro serviço) depende deste contrato. Siga os nomes, os formatos e os status exatamente.

- **Base:** `http://localhost:8080`
- **Formato:** JSON (`Content-Type: application/json`)
- **Front que consome este contrato:** [05-frontend.md](05-frontend.md)
- **Regras de negócio:** [01-regras-de-negocio.md](01-regras-de-negocio.md)

## Menu da tela e rotas

O front tem o mesmo menu do V1. Cada opção abre uma tela, e cada tela chama as rotas abaixo. Os nomes das rotas ficam em um lugar só, [`frontend/js/config.js`](../frontend/js/config.js).

| Opção do menu | Tela | Rota(s) que a sua API precisa ter |
|---|---|---|
| 1 - Cadastrar um novo pet | `cadastrar.html` | `POST /pets` |
| 2 - Alterar os dados de um pet | `alterar.html` | `GET /pets?...` (buscar) e `PUT /pets/{id}` |
| 3 - Deletar um pet | `deletar.html` | `GET /pets?...` (buscar) e `DELETE /pets/{id}` |
| 4 - Listar todos os pets | `listar.html` | `GET /pets` |
| 5 - Listar pets por critério | `buscar.html` | `GET /pets?...` |
| 6 - Sair | `sair.html` | (nenhuma) |

Detalhes de cada rota nas seções abaixo.

## 1. Representação do pet

### Resposta (o que a API devolve)

```json
{
  "id": 2,
  "nomeCompleto": "Florzinha da Silva",
  "tipo": "GATO",
  "sexo": "FEMEA",
  "endereco": {
    "numeroCasa": "456",
    "cidade": "Seilândia",
    "rua": "Rua Azul"
  },
  "idade": 6,
  "peso": 5.0,
  "raca": "Siamês",
  "dataCadastro": "2026-10-27T14:32:10"
}
```

### Requisição (o que a API recebe no `POST` e no `PUT`)

O mesmo JSON, **sem** `id` e **sem** `dataCadastro`. Esses dois a API gera.

```json
{
  "nomeCompleto": "Florzinha da Silva",
  "tipo": "GATO",
  "sexo": "FEMEA",
  "endereco": {
    "numeroCasa": "456",
    "cidade": "Seilândia",
    "rua": "Rua Azul"
  },
  "idade": 6,
  "peso": 5.0,
  "raca": "Siamês"
}
```

### Campos

| Campo | Tipo JSON | Obrigatório | Observação |
|---|---|---|---|
| `id` | número | (só na resposta) | Gerado pelo banco |
| `nomeCompleto` | texto ou `null` | Não | Nome e sobrenome. Ausente ou vazio vira `null` |
| `tipo` | texto | Sim | Exatamente `CACHORRO` ou `GATO` |
| `sexo` | texto | Sim | Exatamente `MACHO` ou `FEMEA` |
| `endereco.numeroCasa` | texto ou `null` | Não | Só dígitos |
| `endereco.cidade` | texto | Sim | |
| `endereco.rua` | texto | Sim | |
| `idade` | número ou `null` | Não | Em anos, com ponto decimal. Maior que 0 e até 20 |
| `peso` | número ou `null` | Não | Em kg, com ponto decimal. De 0.5 a 60 |
| `raca` | texto ou `null` | Não | Letras, espaços e hífen |
| `dataCadastro` | texto ISO 8601 | (só na resposta) | `AAAA-MM-DDTHH:MM:SS` |

Pontos importantes:

- Na API, `idade` e `peso` são **números JSON** com ponto. O front converte a vírgula digitada em ponto antes de enviar, e a API **não** aceita `"3,5"`.
- Campo opcional não informado volta como `null`. O texto `NÃO INFORMADO` não existe na API, é o front que mostra.
- `tipo` e `sexo` não aceitam outras variações. `"gato"` minúsculo é erro `400`.

## 2. Endpoints

| Método | Rota | Sucesso | Erros possíveis |
|---|---|---|---|
| `POST` | `/pets` | `201 Created` | `400` |
| `GET` | `/pets` | `200 OK` | `400` (filtro inválido) |
| `GET` | `/pets/{id}` | `200 OK` | `400` (id não numérico), `404` |
| `PUT` | `/pets/{id}` | `200 OK` | `400`, `404` |
| `DELETE` | `/pets/{id}` | `204 No Content` | `400` (id não numérico), `404` |

### 2.1 `POST /pets`: cadastrar

- **Corpo:** requisição da seção 1.
- **Sucesso:** `201 Created`, header `Location` com a URL do novo pet (`http://localhost:8080/pets/11`) e o pet criado no corpo.
- **Erro:** `400` com a lista de campos inválidos (seção 3).

```bash
curl -i -X POST http://localhost:8080/pets \
  -H "Content-Type: application/json" \
  -d '{
    "nomeCompleto": "Rex Almeida",
    "tipo": "CACHORRO",
    "sexo": "MACHO",
    "endereco": { "numeroCasa": "123", "cidade": "Curitiba", "rua": "Rua das Flores" },
    "idade": 2,
    "peso": 5.0,
    "raca": "Vira-lata"
  }'
```

### 2.2 `GET /pets`: listar e buscar

Sem filtros devolve todos, ordenados por `id` crescente. Com filtros, devolve só quem atende a **todos** (E).

| Parâmetro | Tipo | Como busca |
|---|---|---|
| `nome` | texto | Parte do nome, sem caixa e sem acento |
| `raca` | texto | Parte da raça, sem caixa e sem acento |
| `endereco` | texto | Parte da cidade **ou** da rua, sem caixa e sem acento |
| `tipo` | `CACHORRO` ou `GATO` | Exato |
| `sexo` | `MACHO` ou `FEMEA` | Exato |
| `idadeMin` | número | Idade maior ou igual |
| `idadeMax` | número | Idade menor ou igual |
| `pesoMin` | número | Peso maior ou igual |
| `pesoMax` | número | Peso menor ou igual |
| `cadastroMes` e `cadastroAno` | número | Data de cadastro (regra nível 2, não obrigatório) |

- Nenhum resultado devolve `200 OK` com `[]`. **Não** é `404`.
- Valor inválido de filtro (`tipo=XYZ`, `idadeMin=abc`) devolve `400`.

Exemplos (com o [seed](03-modelo-de-dados.md#8-dados-de-exemplo-seed) carregado):

| Chamada | Pets devolvidos (ids) |
|---|---|
| `GET /pets` | 1 a 10 |
| `GET /pets?nome=flor` | 2 |
| `GET /pets?raca=siames` | 2 |
| `GET /pets?tipo=GATO&idadeMin=1` | 2, 7, 8 |
| `GET /pets?pesoMax=5` | 1, 2, 4, 5, 8 |
| `GET /pets?idadeMin=1&idadeMax=5` | 1, 3, 8, 10 |
| `GET /pets?endereco=colombo` | 7, 10 |
| `GET /pets?endereco=flor` | 1 (`Rua das Flores`) |
| `GET /pets?nome=zzzz` | (lista vazia) |

### 2.3 `GET /pets/{id}`: buscar um

- Sucesso: `200 OK` com o pet.
- Inexistente: `404`.

### 2.4 `PUT /pets/{id}`: alterar

- **Corpo:** o mesmo da requisição da seção 1 (a alteração substitui os dados editáveis).
- **Tipo e sexo não mudam.** Se vierem no corpo com valor **diferente** do cadastrado, `400` com a mensagem `Tipo e sexo não podem ser alterados.`. Se vierem iguais, ou nem vierem, tudo bem.
- `dataCadastro` e `id` nunca mudam.
- Sucesso: `200 OK` com o pet alterado.
- Pet inexistente: `404`.

### 2.5 `DELETE /pets/{id}`: excluir

- Sucesso: `204 No Content`, sem corpo.
- Inexistente: `404`.
- A confirmação de exclusão **não** existe na API. Quem confirma com o usuário é o cliente (o front).

## 3. Formato de erro

**Toda** resposta de erro usa o mesmo formato, em todos os endpoints.

```json
{
  "status": 400,
  "erro": "Dados inválidos",
  "mensagem": "Um ou mais campos são inválidos.",
  "campos": [
    { "campo": "peso", "mensagem": "O peso deve ser um número entre 0,5kg e 60kg." },
    { "campo": "tipo", "mensagem": "Tipo inválido. Use Cachorro ou Gato." }
  ],
  "caminho": "/pets",
  "timestamp": "2026-10-27T14:35:02"
}
```

| Campo | Conteúdo |
|---|---|
| `status` | Código HTTP |
| `erro` | Nome curto do erro (`Dados inválidos`, `Não encontrado`, `Erro interno`) |
| `mensagem` | Frase para humanos |
| `campos` | Lista de `{campo, mensagem}`. Só em erro de validação. Nos outros casos, lista vazia |
| `caminho` | URL chamada |
| `timestamp` | Momento do erro, ISO 8601 |

As mensagens de cada campo são as da [tabela de mensagens](01-regras-de-negocio.md#4-mensagens-de-erro-padrão).

### Quando cada status

| Status | Quando |
|---|---|
| `400 Bad Request` | Campo inválido, JSON malformado, tipo errado (`"idade": "abc"`), id não numérico, filtro inválido, tentativa de alterar tipo ou sexo |
| `404 Not Found` | Pet com esse `id` não existe. Mensagem `Pet não encontrado.` |
| `500 Internal Server Error` | Qualquer coisa que você não previu |

### O que `500` NUNCA pode mostrar

- Stack trace.
- Nome de classe, de tabela ou de coluna.
- Texto do erro do banco.

A mensagem é sempre genérica (`Erro interno. Tente novamente mais tarde.`). O detalhe real vai para o **log** do servidor.

### Exemplos de erro

`GET /pets/999`:

```json
{
  "status": 404,
  "erro": "Não encontrado",
  "mensagem": "Pet não encontrado.",
  "campos": [],
  "caminho": "/pets/999",
  "timestamp": "2026-10-27T14:36:11"
}
```

`POST /pets` com peso `61` e sem cidade:

```json
{
  "status": 400,
  "erro": "Dados inválidos",
  "mensagem": "Um ou mais campos são inválidos.",
  "campos": [
    { "campo": "peso", "mensagem": "O peso deve ser um número entre 0,5kg e 60kg." },
    { "campo": "endereco.cidade", "mensagem": "Informe a cidade." }
  ],
  "caminho": "/pets",
  "timestamp": "2026-10-27T14:37:40"
}
```

## 4. CORS: o front não é a mesma origem da API

O front roda em `http://localhost:5500` e a API em `http://localhost:8080`. Para o navegador, são **origens diferentes**. Por segurança ele bloqueia a resposta da API, a menos que a API diga que aceita aquela origem. Esse mecanismo se chama **CORS**.

O que a sua API precisa permitir:

| Item | Valor |
|---|---|
| Origem | `http://localhost:5500` |
| Métodos | `GET`, `POST`, `PUT`, `DELETE` e `OPTIONS` |
| Headers | `Content-Type` |

Sintoma de CORS faltando: o `curl` e o Postman funcionam, mas o front mostra erro e o console do navegador fala em `Access-Control-Allow-Origin`. Detalhes em [05-frontend.md](05-frontend.md).

Pesquise: o que é uma requisição *preflight* (`OPTIONS`) e por que o navegador faz isso antes de um `PUT` ou `DELETE`.

## 5. Pronto quando

- [ ] Os cinco endpoints respondem com o status da tabela da seção 2
- [ ] Os exemplos de `GET /pets` com filtros devolvem exatamente os ids esperados
- [ ] Todo erro (`400`, `404`, `500`) usa o formato da seção 3
- [ ] `400` lista TODOS os campos inválidos de uma vez, não só o primeiro
- [ ] `500` nunca vaza stack trace nem nome de tabela
- [ ] Tipo e sexo não conseguem ser alterados pelo `PUT`
- [ ] O front pronto (`frontend/`) cadastra, lista, busca, altera e exclui sem erro de CORS
