# 04. Menu de terminal (CLI)

Especificação completa do programa de terminal que você refaz no Passo 4. É o mesmo comportamento do V1, agora em cima do banco.

## 1. O arquivo `formulario.txt`

O arquivo já vem pronto na raiz deste repositório:

```
1 - Qual o nome e sobrenome do pet?
2 - Qual o tipo do pet (Cachorro/Gato)?
3 - Qual o sexo do animal?
4 - Qual endereço e bairro que ele foi encontrado?
5 - Qual a idade aproximada do pet?
6 - Qual o peso aproximado do pet?
7 - Qual a raça do pet?
```

Regras:

- As perguntas são **lidas do arquivo**. Escrever o texto da pergunta no código reprova o desafio.
- Cada linha tem o formato `N - texto`. Ao exibir, mostre a linha como está.
- A pergunta 4 (endereço) vira **três** perguntas seguidas: número da casa, cidade e rua. O texto dessas três pode ficar no código, só a pergunta 4 vem do arquivo.
- Se o arquivo não existir ou estiver vazio, o programa avisa e encerra, sem exceção na tela.

## 2. Menu inicial

O menu aparece assim e volta a aparecer depois de cada operação:

```
===== ABRIGO DE PETS =====
1 - Cadastrar um novo pet
2 - Alterar os dados de um pet
3 - Deletar um pet
4 - Listar todos os pets
5 - Listar pets por algum critério
6 - Sair
Escolha uma opção:
```

Regras de entrada:

| Entrada | Comportamento |
|---|---|
| Número de 1 a 6 | Executa a opção |
| `0` ou número negativo | Mensagem de opção inválida e o menu aparece de novo |
| Número maior que 6 | Mensagem de opção inválida e o menu aparece de novo |
| Letras ou símbolos | Mensagem de opção inválida e o menu aparece de novo |
| Vazio | Mensagem de opção inválida e o menu aparece de novo |

O programa **nunca** pode quebrar com exceção na tela por causa de entrada do usuário.

## 3. Opção 1: cadastrar

O sistema faz as perguntas na ordem do formulário. Exemplo de sessão:

```
Qual o nome e sobrenome do pet?
> Florzinha da Silva
Qual o tipo do pet (Cachorro/Gato)?
> gato
Qual o sexo do animal?
> femea
Qual endereço e bairro que ele foi encontrado?
Número da casa:
> 456
Cidade:
> Seilândia
Rua:
> Rua Azul
Qual a idade aproximada do pet?
> 6
Qual o peso aproximado do pet?
> 5
Qual a raça do pet?
> Siamês

Pet cadastrado com sucesso!
```

Regras:

1. Todas as regras da [seção 2 das regras de negócio](01-regras-de-negocio.md#2-regras-por-campo) valem.
2. Resposta inválida mostra a mensagem padrão do erro e **pergunta de novo o mesmo campo** (o usuário não perde o que já respondeu).
3. Campo opcional em branco vira `NÃO INFORMADO`.
4. Ao final, o pet é gravado no banco pelo repositório do Passo 3.

## 4. Busca (usada nas opções 2, 3 e 5)

A busca é a mesma para listar, alterar e deletar. Escreva uma vez e reaproveite.

### Passo a passo da tela

1. O sistema pergunta o **tipo do animal** (obrigatório):

   ```
   Qual o tipo de animal?
   1 - Cachorro
   2 - Gato
   ```

2. O sistema pergunta quantos critérios o usuário quer usar (1 ou 2) e mostra o menu de critérios:

   ```
   Escolha o critério de busca:
   1 - Nome ou sobrenome
   2 - Sexo
   3 - Idade
   4 - Peso
   5 - Raça
   6 - Endereço
   ```

3. O usuário informa o valor de cada critério escolhido.
4. O sistema mostra o resultado numerado:

   ```
   1. Rex Almeida - Cachorro - Macho - Rua das Flores, 123 - Curitiba - 2 anos - 5kg - Vira-lata
   2. Thor Oliveira - Cachorro - Macho - Avenida Brasil, 78 - Curitiba - 4 anos - 32.5kg - Labrador
   ```

### Regras

- Critério inválido (fora de 1 a 6): o menu de critérios aparece de novo.
- Os dois critérios precisam ser **diferentes**.
- Resultado sem registros: `Nenhum pet encontrado.` e volta ao menu.
- Formato de cada linha, parcial, sem acento e sem caixa: [regras, seção 5](01-regras-de-negocio.md#5-busca-de-pets).

## 5. Opção 2: alterar

1. Faz a busca (seção 4).
2. O usuário digita o **número** do pet na lista. Número fora da lista: mensagem e volta ao menu.
3. O sistema pergunta campo a campo (nome, endereço em 3 partes, idade, peso, raça). **Tipo e sexo não são perguntados.**
4. Campo deixado em branco **mantém o valor atual**.
5. Valor inválido: mensagem padrão e pergunta o mesmo campo de novo.
6. Ao final: `Pet alterado com sucesso!`

## 6. Opção 3: deletar

1. Faz a busca (seção 4).
2. O usuário digita o **número** do pet na lista.
3. O sistema confirma:

   ```
   Deseja realmente excluir "Rex Almeida"? (SIM/NÃO)
   ```

4. `SIM` (qualquer caixa) exclui e mostra `Pet excluído com sucesso!`.
5. `NÃO` cancela e volta ao menu. Qualquer outra resposta pergunta de novo.
6. Só um pet por vez.

## 7. Opção 4: listar todos

Mostra todos os pets no mesmo formato numerado da busca. Banco vazio mostra `Nenhum pet cadastrado.`

## 8. Opção 5: listar por critério

Faz a busca (seção 4) e mostra o resultado. Não altera nada.

## 9. Opção 6: sair

Encerra o programa com uma mensagem de despedida e libera os recursos (conexão com o banco).

## 10. Erros técnicos

Banco fora do ar, senha errada, tabela inexistente: o programa mostra uma mensagem amigável (`Não foi possível acessar o banco de dados.`) e não imprime stack trace para o usuário. O detalhe técnico vai para um log (pode ser `System.err` no começo).

## 11. Pronto quando

- [ ] As perguntas vêm do `formulario.txt`
- [ ] O menu reage a todas as entradas inválidas da tabela da seção 2
- [ ] Cadastro, alteração, exclusão, listagem e busca funcionam contra o PostgreSQL
- [ ] A busca acha `siames` para `Siamês` e `FLOR` para `Florzinha da Silva`
- [ ] Tipo e sexo nunca podem ser alterados
- [ ] Nenhuma exceção aparece para o usuário
