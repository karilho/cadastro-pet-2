# 03. Modelo de dados

Este documento define **o que** o sistema guarda: as entidades, os atributos e as regras. O **como** é com você: tipos de coluna, restrições, nomes, chaves e índices. É de propósito. Modelar banco é decidir, e decidir é onde se aprende.

## 1. Entidade Pet

Um pet é um animal encontrado e cadastrado por um abrigo para adoção.

| Atributo | Natureza | Obrigatório | Regra |
|---|---|---|---|
| Identificador | Único, gerado pelo banco | Sim | Nunca informado pelo usuário |
| Nome completo | Texto | Não | Nome e sobrenome ([regras, seção 2.1](01-regras-de-negocio.md#21-nome-completo)) |
| Tipo | Um entre `CACHORRO` e `GATO` | Sim | Valores fixos |
| Sexo | Um entre `MACHO` e `FEMEA` | Sim | Valores fixos |
| Número da casa | Texto de dígitos | Não | Só dígitos |
| Cidade | Texto | Sim | Onde o pet foi encontrado |
| Rua | Texto | Sim | Onde o pet foi encontrado |
| Idade | Número decimal, em anos | Não | Maior que 0 e até 20 |
| Peso | Número decimal, em kg | Não | De 0.5 a 60 |
| Raça | Texto | Não | Letras, espaços e hífen |
| Data de cadastro | Data e hora | Sim | Gerada pelo sistema, nunca informada |

## 2. O que o banco garante e o que o código garante

Toda regra da [seção 2 das regras](01-regras-de-negocio.md#2-regras-por-campo) precisa existir no Java. O banco **também** deve se defender onde conseguir.

Pense, antes de escrever o `schema.sql`:

- Quais regras o banco consegue garantir sozinho? Quais só o Java consegue?
- Um dado inválido que escapa do Java (um `INSERT` direto, por exemplo) deveria ser recusado pelo banco?
- O que acontece com os campos opcionais? O que o banco guarda quando o usuário não informa?

> Regra do V2: o que não foi informado é guardado como **ausência de valor** (`NULL`). O texto `NÃO INFORMADO` só existe na exibição.

## 3. Decisões que são suas

Estas decisões não estão prontas. Tome, e esteja pronto para explicar o porquê.

| Decisão | Pergunta para você responder |
|---|---|
| Chave primária | Qual o tipo? Como é gerada? |
| Tipos das colunas | Texto de que tamanho? Qual tipo para idade e peso? Por que **não** usar ponto flutuante para isso? |
| Valores fixos | Como guardar tipo e sexo? Existe mais de uma forma, qual a vantagem de cada? |
| Restrições | Quais colunas aceitam ausência? Quais limites o banco pode conferir? |
| Índices | Quais colunas serão mais pesquisadas? Um índice ajuda em toda busca? |
| Busca sem caixa e sem acento | O banco que você escolheu faz isso sozinho? Se não, como se faz? |
| Nomes | Português ou inglês? Singular ou plural? `snake_case`? Mantenha um padrão só |
| Data de cadastro | Quem preenche: o Java ou o banco? |

## 4. Valores dos ENUMs

Defina onde a conversão acontece e escreva isso uma vez só.

| Armazenado e usado na API | Exibição no terminal |
|---|---|
| `CACHORRO` | `Cachorro` |
| `GATO` | `Gato` |
| `MACHO` | `Macho` |
| `FEMEA` | `Femea` |

## 5. Regra nível 2: endereço separado

O endereço deixa de ser parte do pet e vira sua própria entidade.

| Atributo de Endereço | Obrigatório |
|---|---|
| Identificador | Sim |
| Número da casa | Não |
| Cidade | Sim |
| Rua | Sim |

Relacionamento: **um pet tem um endereço**. Como o pet referencia o endereço, e o que acontece com o endereço quando o pet é excluído, é decisão sua.

## 6. Passo EXTRA: perguntas e respostas

Só para quem fizer o formulário dinâmico.

### Entidade Pergunta

| Atributo | Obrigatório | Observação |
|---|---|---|
| Identificador | Sim | |
| Número | Sim | Numeração exibida (1, 2, 3...). Não repete |
| Texto | Sim | |
| Original | Sim | Verdadeiro para as perguntas de 1 a 7 |

### Entidade Resposta

| Atributo | Obrigatório | Observação |
|---|---|---|
| Identificador | Sim | |
| Pet | Sim | A quem pertence a resposta |
| Pergunta | Sim | A qual pergunta responde |
| Texto | Não | Ausente vira `NÃO INFORMADO` na exibição |

Um pet responde cada pergunta **no máximo uma vez**.

### Relacionamento

```
pet 1 ───── N resposta N ───── 1 pergunta
```

Um pet tem várias respostas. Uma pergunta é respondida por vários pets. A resposta é a ponte entre os dois. Nome e tipo de cada coluna: decisão sua.

## 7. Models no Java

Os nomes abaixo são sugestão. Você pode usar inglês ou português, desde que mantenha o padrão em todo o projeto.

### Classes e tipos

| Elemento | Tipo | Campos / valores | Observação |
|---|---|---|---|
| `Pet` | Classe | identificador, nome completo, tipo, sexo, endereço, idade, peso, raça, data de cadastro | Entidade principal |
| `Endereco` | Classe ou record | número da casa, cidade, rua | Agrupa as três partes do endereço |
| `TipoPet` | Enum | `CACHORRO`, `GATO` | Guarda também o texto de exibição |
| `SexoPet` | Enum | `MACHO`, `FEMEA` | Guarda também o texto de exibição |
| Constante `NAO_INFORMADO` | `String` | `"NÃO INFORMADO"` | Em UM único lugar |

### Tipos Java dos campos

| Campo | Tipo Java sugerido | Por quê |
|---|---|---|
| Identificador | `Long` | Nulo antes de gravar |
| Nome completo, raça, número da casa | `String` | `null` quando não informado |
| Idade e peso | `BigDecimal` | Número exato. `Double` também funciona, mas entenda a diferença |
| Data de cadastro | `LocalDateTime` | API moderna de datas |
| Tipo e sexo | Enums acima | Nunca `String` solta |

### Exceções

Crie exceções próprias para as regras de negócio. Uma por situação ou uma família com herança, você decide. O que importa:

- Todas representam erro de **regra de negócio** (dado inválido do usuário).
- Carregam a mensagem padrão da [tabela de mensagens](01-regras-de-negocio.md#4-mensagens-de-erro-padrão).
- Não são a mesma coisa que erro técnico (banco fora do ar). Esse é outro tipo de exceção.

Sugestão de nomes: `NomeInvalidoException`, `IdadeInvalidaException`, `PesoInvalidoException`, `PetNaoEncontradoException`.

## 8. Dados de exemplo (seed)

Use estes 10 pets no seu `seed.sql`. Todos os resultados esperados abaixo foram calculados com estes dados. Se o seu resultado for diferente, o erro está na sua consulta.

| id | Nome completo | Tipo | Sexo | Número da casa | Cidade | Rua | Idade (anos) | Peso (kg) | Raça |
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
| Quantidade por tipo | CACHORRO = 6, GATO = 4 |

Peso médio por tipo e sexo:

| Tipo | Sexo | Peso médio |
|---|---|---|
| CACHORRO | FEMEA | 16.25 |
| CACHORRO | MACHO | 21.50 |
| GATO | FEMEA | 3.10 |
| GATO | MACHO | 6.80 |

> Os seus testes (Passo 6) também devem usar este seed ou parte dele. Dados fixos tornam o teste previsível.
