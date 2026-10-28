# 03. Modelo de dados

Este documento define COMO os dados são guardados (banco) e COMO são representados no Java (models). Você escreve o código. Aqui está o contrato.

## 1. Tabela `pet`

### Diagrama

```
┌──────────────────────────────────────────────────────┐
│ pet                                                  │
├──────────────────┬───────────────────────────────────┤
│ id               │ PK, auto incremento               │
│ nome_completo    │ texto, aceita NULL                │
│ tipo             │ CACHORRO | GATO, obrigatório      │
│ sexo             │ MACHO | FEMEA, obrigatório        │
│ numero_casa      │ texto, aceita NULL                │
│ cidade           │ texto, obrigatório                │
│ rua              │ texto, obrigatório                │
│ idade_anos       │ decimal, aceita NULL              │
│ peso_kg          │ decimal, aceita NULL              │
│ raca             │ texto, aceita NULL                │
│ data_cadastro    │ data e hora, obrigatório          │
└──────────────────┴───────────────────────────────────┘
```

### Contrato das colunas

| Coluna | Tipo sugerido | Nulo? | Padrão | Restrição |
|---|---|---|---|---|
| `id` | `BIGINT` | Não | auto incremento | Chave primária |
| `nome_completo` | `VARCHAR(120)` | Sim | | |
| `tipo` | `ENUM('CACHORRO','GATO')` | Não | | |
| `sexo` | `ENUM('MACHO','FEMEA')` | Não | | |
| `numero_casa` | `VARCHAR(10)` | Sim | | |
| `cidade` | `VARCHAR(80)` | Não | | |
| `rua` | `VARCHAR(120)` | Não | | |
| `idade_anos` | `DECIMAL(4,2)` | Sim | | `CHECK` maior que 0 e até 20 |
| `peso_kg` | `DECIMAL(5,2)` | Sim | | `CHECK` de 0.5 a 60 |
| `raca` | `VARCHAR(60)` | Sim | | |
| `data_cadastro` | `DATETIME` | Não | agora (`CURRENT_TIMESTAMP`) | |

### Por que cada decisão

| Decisão | Motivo |
|---|---|
| Idade e peso em `DECIMAL`, não `FLOAT` | `FLOAT` erra em casas decimais. Pesquise por que dinheiro e peso usam `DECIMAL` |
| `numero_casa` como texto | Preserva zero à esquerda e não faz conta com ele |
| Campos opcionais aceitam `NULL` | `NÃO INFORMADO` é só para exibir (ver [regras, seção 3](01-regras-de-negocio.md#3-não-informado)) |
| `CHECK` em idade e peso | O banco também se defende, não só o Java |
| `data_cadastro` com padrão | O usuário nunca informa essa data |

> **Pegadinha:** em SQL, `NULL` passa em qualquer `CHECK`. Isso é desejado aqui (idade e peso são opcionais), mas teste e entenda por quê.

### Valores dos ENUMs

No banco o valor é em maiúsculas. Na tela e no JSON da API, o tipo e o sexo aparecem de formas diferentes. Defina onde a conversão acontece e escreva isso uma vez só.

| Banco e JSON | Exibição no terminal |
|---|---|
| `CACHORRO` | `Cachorro` |
| `GATO` | `Gato` |
| `MACHO` | `Macho` |
| `FEMEA` | `Femea` |

### Índices

Crie índices para as colunas que a busca mais usa. No mínimo `nome_completo` e `raca`.

> **Descubra na prática:** rode `EXPLAIN` em duas buscas e compare.
> 1. `WHERE nome_completo LIKE 'Flor%'`
> 2. `WHERE nome_completo LIKE '%Flor%'`
>
> Um deles usa o índice, o outro não. Qual e por quê? Isso cai em entrevista.

## 2. Tabela `endereco` (regra nível 2)

Se você fizer o nível 2 do Passo 2, o endereço sai da tabela `pet` e vira outra tabela.

| Coluna | Tipo sugerido | Nulo? | Restrição |
|---|---|---|---|
| `id` | `BIGINT` | Não | Chave primária, auto incremento |
| `numero_casa` | `VARCHAR(10)` | Sim | |
| `cidade` | `VARCHAR(80)` | Não | |
| `rua` | `VARCHAR(120)` | Não | |

E a tabela `pet` ganha `endereco_id`, com chave estrangeira para `endereco.id` e sem as três colunas de endereço.

## 3. Tabelas do Passo EXTRA

Só para quem fizer o formulário dinâmico.

### `pergunta`

| Coluna | Tipo sugerido | Nulo? | Restrição |
|---|---|---|---|
| `id` | `BIGINT` | Não | Chave primária, auto incremento |
| `numero` | `INT` | Não | Único. É a numeração exibida (1, 2, 3...) |
| `texto` | `VARCHAR(255)` | Não | |
| `original` | `BOOLEAN` | Não | `TRUE` para as perguntas de 1 a 7 |

### `resposta`

| Coluna | Tipo sugerido | Nulo? | Restrição |
|---|---|---|---|
| `id` | `BIGINT` | Não | Chave primária, auto incremento |
| `pet_id` | `BIGINT` | Não | Chave estrangeira para `pet.id` |
| `pergunta_id` | `BIGINT` | Não | Chave estrangeira para `pergunta.id` |
| `texto` | `VARCHAR(255)` | Sim | `NULL` vira `NÃO INFORMADO` na exibição |

Restrição extra: o par (`pet_id`, `pergunta_id`) é único. Um pet responde cada pergunta uma vez.

### Relacionamentos

```
pet 1 ───── N resposta N ───── 1 pergunta
```

Um pet tem várias respostas. Uma pergunta é respondida por vários pets. A tabela `resposta` é a ponte.

## 4. Models no Java

Os nomes abaixo são sugestão. Você pode usar inglês ou português, desde que mantenha o padrão em todo o projeto.

### Classes e tipos

| Elemento | Tipo | Campos / valores | Observação |
|---|---|---|---|
| `Pet` | Classe | `id`, `nomeCompleto`, `tipo`, `sexo`, `endereco`, `idade`, `peso`, `raca`, `dataCadastro` | É a entidade principal |
| `Endereco` | Classe ou record | `numeroCasa`, `cidade`, `rua` | Agrupa as três partes do endereço |
| `TipoPet` | Enum | `CACHORRO`, `GATO` | Guarda também o texto de exibição |
| `SexoPet` | Enum | `MACHO`, `FEMEA` | Guarda também o texto de exibição |
| Constante `NAO_INFORMADO` | `String` | `"NÃO INFORMADO"` | Em UM único lugar |

### Tipos Java dos campos

| Campo | Tipo Java sugerido | Por quê |
|---|---|---|
| `id` | `Long` | Nulo antes de gravar |
| `nomeCompleto`, `raca`, `numeroCasa` | `String` | `null` quando não informado |
| `idade`, `peso` | `BigDecimal` | Casa com `DECIMAL`. `Double` também funciona, mas entenda a diferença |
| `dataCadastro` | `LocalDateTime` | API moderna de datas |
| `tipo`, `sexo` | Enums acima | Nunca `String` solta |

### Exceções

Crie exceções próprias para as regras de negócio. Uma exceção por situação ou uma família com herança, você decide. O que importa:

- Todas representam erro de **regra de negócio** (dado inválido do usuário).
- Carregam a mensagem padrão da [tabela de mensagens](01-regras-de-negocio.md#4-mensagens-de-erro-padrão).
- Não são a mesma coisa que erro técnico (banco fora do ar). Esse é outro tipo de exceção.

Sugestão de nomes: `NomeInvalidoException`, `IdadeInvalidaException`, `PesoInvalidoException`, `PetNaoEncontradoException`.

## 5. Dados de exemplo (seed)

Use estes 10 pets no seu `seed.sql`. Todos os resultados esperados abaixo foram calculados com estes dados. Se o seu resultado for diferente, o erro está na sua consulta.

| id | nome_completo | tipo | sexo | numero_casa | cidade | rua | idade_anos | peso_kg | raca |
|---|---|---|---|---|---|---|---|---|---|
| 1 | Rex Almeida | CACHORRO | MACHO | 123 | Curitiba | Rua das Flores | 2 | 5.00 | Vira-lata |
| 2 | Florzinha da Silva | GATO | FEMEA | 456 | Seilândia | Rua Azul | 6 | 5.00 | Siamês |
| 3 | Thor Oliveira | CACHORRO | MACHO | 78 | Curitiba | Avenida Brasil | 4 | 32.50 | Labrador |
| 4 | Mimi Santos | GATO | FEMEA | 910 | São José dos Pinhais | Rua Esmeralda | 0.5 | 1.20 | Persa |
| 5 | Bob Costa | CACHORRO | MACHO | NULL | Pinhais | Rua do Sol | 0.8 | 3.50 | Poodle |
| 6 | Luna Ferreira | CACHORRO | FEMEA | 205 | Curitiba | Rua das Palmeiras | 7 | 24.00 | Border Collie |
| 7 | Garfield Souza | GATO | MACHO | 33 | Colombo | Rua Verde | 9 | 6.80 | Vira-lata |
| 8 | Nina Rocha | GATO | FEMEA | 12 | Araucária | Rua dos Pinheiros | 1 | 3.10 | NULL |
| 9 | Max Pereira | CACHORRO | MACHO | 540 | Curitiba | Rua das Acácias | 12 | 45.00 | Pastor Alemão |
| 10 | Pipoca Lima | CACHORRO | FEMEA | 88 | Colombo | Rua Central | 3 | 8.50 | Vira-lata |

Repare nos casos de borda de propósito: pets com número de casa e raça `NULL` (ids 5 e 8), filhotes com menos de 1 ano (ids 4 e 5), raças e cidades com acento (ids 2, 4, 8 e 9) e raça composta com hífen (ids 1, 7 e 10).

### Resultados esperados

Estas são as consultas do `queries.sql` (Passo 2) e o que cada uma deve devolver com o seed acima.

| Consulta | Resultado esperado |
|---|---|
| Listar todos | 10 linhas |
| Nome contendo `flor` (qualquer caixa) | id 2 |
| Nome contendo `silva` | id 2 |
| Raça contendo `siames` (sem acento) | id 2 |
| Raça contendo `pastor` | id 9 |
| Raça igual a `Vira-lata` | ids 1, 7, 10 |
| Endereço contendo `flor` (cidade ou rua) | id 1 (`Rua das Flores`) |
| Cidade igual a `Colombo` | ids 7, 10 |
| Idade de 1 a 5 anos (inclusive) | ids 1, 3, 8, 10 |
| Peso de até 5kg (inclusive) | ids 1, 2, 4, 5, 8 |
| Tipo `GATO` e idade de pelo menos 1 | ids 2, 7, 8 |
| Tipo `CACHORRO` e peso de pelo menos 20 | ids 3, 6, 9 |
| Menos de 1 ano de idade | ids 4, 5 |
| Quantidade por tipo (`GROUP BY tipo`) | CACHORRO = 6, GATO = 4 |

Peso médio por tipo e sexo (`GROUP BY tipo, sexo`):

| tipo | sexo | peso médio |
|---|---|---|
| CACHORRO | FEMEA | 16.25 |
| CACHORRO | MACHO | 21.50 |
| GATO | FEMEA | 3.10 |
| GATO | MACHO | 6.80 |

> Sua tabela de testes (Passo 6) também deve usar este seed ou parte dele. Dados fixos tornam o teste previsível.
