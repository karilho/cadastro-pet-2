# 01. Regras de negócio

Este é o documento mais importante do desafio. Ele vale para TODOS os passos. Se uma regra está aqui, ela precisa existir no seu código, no seu banco (quando o banco consegue garantir) e nos seus testes.

> As regras vêm do [V1](https://github.com/karilho/desafioCadastro). Onde o V1 era ambíguo, a decisão do V2 está na seção 9. Não reinterprete: siga o que está escrito aqui.

## 1. O que é um Pet

Um pet é um animal encontrado e cadastrado por um abrigo para adoção.

| Campo | Tipo de negócio | Obrigatório | Observação |
|---|---|---|---|
| Nome completo | Texto (nome e sobrenome) | Não | Se vazio vira `NÃO INFORMADO` |
| Tipo | `Cachorro` ou `Gato` | Sim | ENUM |
| Sexo | `Macho` ou `Femea` | Sim | ENUM (sem acento, como no V1) |
| Número da casa | Texto numérico | Não | Se vazio vira `NÃO INFORMADO` |
| Cidade | Texto | Sim | Onde o pet foi encontrado |
| Rua | Texto | Sim | Onde o pet foi encontrado |
| Idade | Número decimal em anos | Não | Se vazio vira `NÃO INFORMADO` |
| Peso | Número decimal em kg | Não | Se vazio vira `NÃO INFORMADO` |
| Raça | Texto | Não | Se vazio vira `NÃO INFORMADO` |
| Data de cadastro | Data e hora | Sim | Gerada pelo sistema, o usuário nunca informa |

## 2. Regras por campo

### 2.1 Nome completo

| Entrada | Resultado |
|---|---|
| `Florzinha da Silva` | Válido |
| `Rex Almeida` | Válido |
| `Cármen Lúcia` | Válido (acentos são letras) |
| (vazio ou só espaços) | Válido, vira `NÃO INFORMADO` |
| `Rex` | **Erro**: falta sobrenome |
| `Rex 2` | **Erro**: contém número |
| `Rex@Silva` | **Erro**: contém caractere especial |

- O nome tem pelo menos duas palavras separadas por espaço.
- Só letras (com ou sem acento) e espaços. Nada de número, hífen ou símbolo.
- Espaços nas pontas são removidos antes de validar.
- Nome informado mas sem sobrenome é erro. Nome em branco NÃO é erro (ver seção 9).

### 2.2 Tipo

- Valores aceitos: `Cachorro` e `Gato`.
- A comparação ignora maiúsculas e minúsculas (`gato`, `GATO` e `Gato` valem).
- Qualquer outro valor é erro.
- Obrigatório. Deve ser um ENUM no código.

### 2.3 Sexo

- Valores aceitos: `Macho` e `Femea`.
- A comparação ignora maiúsculas e minúsculas.
- Qualquer outro valor é erro.
- Obrigatório. Deve ser um ENUM no código.

### 2.4 Endereço

O endereço tem três partes. Elas são perguntadas e guardadas separadas.

| Parte | Obrigatório | Regra |
|---|---|---|
| Número da casa | Não | Somente dígitos (`123`). Vazio vira `NÃO INFORMADO` |
| Cidade | Sim | Texto livre, não pode ser vazio |
| Rua | Sim | Texto livre, não pode ser vazio |

### 2.5 Idade

A idade é sempre em **anos**, como número decimal.

| Entrada | Resultado |
|---|---|
| `3` | Válido, 3 anos |
| `3,5` | Válido, 3.5 anos (vírgula vira ponto) |
| `3.5` | Válido, 3.5 anos |
| `0,5` | Válido, 0.5 ano (6 meses) |
| `20` | Válido (20 é o limite) |
| (vazio) | Válido, vira `NÃO INFORMADO` |
| `20,1` | **Erro**: maior que 20 |
| `0` ou `-2` | **Erro**: precisa ser maior que 0 |
| `três` ou `3a` | **Erro**: não é número |

- Aceita vírgula OU ponto como separador decimal, nunca os dois.
- Maior que 0 e no máximo 20.
- Pet com menos de 1 ano é informado como `0,x`. Exemplo: 6 meses é `0,5`.
- O sistema guarda com ponto (`0.5`), nunca com vírgula.

### 2.6 Peso

O peso é sempre em **kg**, como número decimal.

| Entrada | Resultado |
|---|---|
| `5` | Válido |
| `5,2` ou `5.2` | Válido |
| `0,5` | Válido (0.5 é o limite mínimo) |
| `60` | Válido (60 é o limite máximo) |
| (vazio) | Válido, vira `NÃO INFORMADO` |
| `0,4` | **Erro**: menor que 0.5kg |
| `60,1` | **Erro**: maior que 60kg |
| `pesado` | **Erro**: não é número |

### 2.7 Raça

| Entrada | Resultado |
|---|---|
| `Vira-lata` | Válido (hífen é aceito) |
| `Border Collie` | Válido |
| `Siamês` | Válido |
| (vazio) | Válido, vira `NÃO INFORMADO` |
| `Poodle 2` | **Erro**: contém número |
| `Poodle!` | **Erro**: contém caractere especial |

- Só letras (com ou sem acento), espaços e hífen.

## 3. `NÃO INFORMADO`

- Vale para: **nome, número da casa, idade, peso e raça**.
- Deve ser uma **constante em um único lugar** do código. Proibido digitar a string solta em vários arquivos.
- No **banco**, campo não informado é guardado como `NULL`. A constante é usada só para EXIBIR (terminal e mensagens).
- Na **API**, campo não informado volta como `null` no JSON.

## 4. Mensagens de erro padrão

Use estas mensagens exatas. Assim os seus testes e os de outras pessoas são comparáveis.

| Situação | Mensagem |
|---|---|
| Nome sem sobrenome | `Informe nome e sobrenome do pet.` |
| Nome com caractere inválido | `O nome deve conter apenas letras.` |
| Tipo inválido | `Tipo inválido. Use Cachorro ou Gato.` |
| Sexo inválido | `Sexo inválido. Use Macho ou Femea.` |
| Número da casa inválido | `O número da casa deve conter apenas dígitos.` |
| Cidade vazia | `Informe a cidade.` |
| Rua vazia | `Informe a rua.` |
| Idade inválida | `A idade deve ser um número maior que 0 e no máximo 20 anos.` |
| Peso inválido | `O peso deve ser um número entre 0,5kg e 60kg.` |
| Raça inválida | `A raça deve conter apenas letras, espaços e hífen.` |
| Pet não encontrado | `Pet não encontrado.` |
| Alterar tipo ou sexo | `Tipo e sexo não podem ser alterados.` |

## 5. Busca de pets

### 5.1 Critérios disponíveis

| Critério | Como busca |
|---|---|
| Nome ou sobrenome | Por **parte** do texto (`FLOR` encontra `Florzinha da Silva`) |
| Sexo | Valor exato (`Macho` ou `Femea`) |
| Idade | Valor exato |
| Peso | Valor exato |
| Raça | Por **parte** do texto |
| Endereço | Por **parte** da cidade ou da rua |

### 5.2 Regras da busca

1. A busca ignora maiúsculas e minúsculas e também **acentos** (`siames` encontra `Siamês`).
2. No terminal, o usuário SEMPRE escolhe o tipo do animal (Cachorro ou Gato) PRIMEIRO.
3. O usuário pode combinar de 1 a 2 critérios além do tipo. Exemplo: nome e idade, ou idade e peso.
4. Quando mais de um critério é usado, o pet precisa atender a TODOS (E, nunca OU).
5. O resultado é SEMPRE uma lista numerada, mesmo com um só resultado.
6. Busca sem resultado mostra uma mensagem amigável (`Nenhum pet encontrado.`), não é erro.

### 5.3 Formato de exibição

Cada pet aparece em uma linha:

```
1. Rex Almeida - Cachorro - Macho - Rua das Flores, 123 - Curitiba - 2 anos - 5kg - Vira-lata
2. Florzinha da Silva - Gato - Femea - Rua Azul, 456 - Seilândia - 6 anos - 5kg - Siamês
```

Regras de formatação:

- Ordem: nome, tipo, sexo, `rua, número`, cidade, idade, peso, raça.
- Idade: `<valor> anos`. Peso: `<valor>kg`. Sem zeros à direita (`5kg`, não `5.00kg`; `32.5kg`, não `32.50kg`).
- Campo `NULL` aparece como `NÃO INFORMADO`.
- Exemplo com campos faltando: `Bob Costa - Cachorro - Macho - Rua do Sol, NÃO INFORMADO - Pinhais - 0.8 anos - 3.5kg - Poodle`.

### 5.4 Regra nível 2 (não obrigatória)

1. Buscar por data de cadastro (mês e ano), combinável com 1 ou 2 critérios.
2. Imprimir o termo pesquisado em **negrito** quando ele aparece no resultado (no terminal, use o código ANSI `\u001B[1m` e `\u001B[0m`).

## 6. Alteração de pet

1. O usuário busca (regras da seção 5), escolhe o pet da lista pelo número e altera.
2. Pode alterar TODOS os campos, EXCETO **tipo** e **sexo**.
3. As regras de validação da seção 2 valem na alteração. Valor inválido não grava nada.
4. Na alteração, campo deixado em branco **mantém o valor atual** (no terminal).

## 7. Exclusão de pet

1. O usuário busca, escolhe o pet da lista pelo número.
2. Precisa confirmar com `SIM` ou `NÃO` (aceita qualquer caixa).
3. Só um pet por vez.
4. Só `SIM` exclui, e então aparece uma mensagem de sucesso. `NÃO` cancela sem apagar.

## 8. Resumo para a API (Passo 5)

As mesmas regras viram:

- **Bean Validation** nos DTOs de entrada.
- Validação no service para o que a anotação não cobre.
- Resposta de erro `400` com as mensagens da seção 4. Contrato completo em [05-contrato-da-api.md](05-contrato-da-api.md).

## 9. Decisões do V2 onde o V1 era ambíguo

| Ponto | Decisão do V2 |
|---|---|
| O V1 exigia nome e sobrenome, mas também mandava `NÃO INFORMADO` para nome em branco | Nome em **branco** vira `NÃO INFORMADO`. Nome **informado sem sobrenome** é erro |
| O V1 dizia "somente letras A a Z", mas a busca ignora acentos e há raças como `Siamês` | Acentos contam como letras |
| O V1 listava `Vira-lata` como raça de exemplo | Hífen é aceito na raça (e só nela) |
| O V1 dizia "idade em meses vira 0.x anos" | A idade é sempre em anos. Menos de 1 ano é digitado como `0,x` |
| O V1 guardava `NÃO INFORMADO` como texto no arquivo | No banco é `NULL`. A constante só aparece na exibição |
| O V1 não definia o limite exato de idade e peso | Limites inclusivos: idade de mais de 0 até 20, peso de 0.5 a 60 |
