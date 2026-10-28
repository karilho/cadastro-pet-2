# Passo 4: O menu do V1 em cima do banco

Este é o passo que dá nome ao desafio: a refatoração. O terminal do V1 volta, mas agora quem guarda os dados é o banco, não mais arquivos `.txt`.

## Antes de começar

- Passos 1, 2 e 3 concluídos
- Especificação completa da tela: [04-menu-cli.md](../04-menu-cli.md)
- Regras: [01-regras-de-negocio.md](../01-regras-de-negocio.md)
- O arquivo [`formulario.txt`](../../formulario.txt) já vem pronto na raiz do repositório

## O que entregar

- Menu de terminal funcional com as 6 opções
- Leitura das perguntas a partir do `formulario.txt`
- Cadastro, alteração, exclusão, listagem e busca usando o repositório do Passo 3
- Nenhum arquivo `.txt` por pet

## Requisitos

1. Implemente o menu exatamente como em [04-menu-cli.md, seção 2](../04-menu-cli.md#2-menu-inicial), incluindo todas as entradas inválidas.
2. As perguntas são **lidas do `formulario.txt`**. Escrever o texto das perguntas no código reprova.
3. Implemente o cadastro ([seção 3](../04-menu-cli.md#3-opção-1-cadastrar)): resposta inválida mostra a mensagem padrão e repete a pergunta do mesmo campo.
4. Implemente a busca **uma única vez** ([seção 4](../04-menu-cli.md#4-busca-usada-nas-opções-2-3-e-5)) e reaproveite em listar, alterar e deletar.
5. Implemente alterar ([seção 5](../04-menu-cli.md#5-opção-2-alterar)): tipo e sexo nunca são perguntados, campo em branco mantém o valor.
6. Implemente deletar ([seção 6](../04-menu-cli.md#6-opção-3-deletar)) com confirmação `SIM/NÃO`.
7. O formato de exibição de cada pet segue a [seção 5.3 das regras](../01-regras-de-negocio.md#53-formato-de-exibição).
8. Erro técnico (banco fora do ar) mostra mensagem amigável e **nunca** stack trace ([seção 10](../04-menu-cli.md#10-erros-técnicos)).
9. Deixe a regra de negócio nas classes do Passo 1. O menu só **conversa** com o usuário, não valida.

## Pronto quando

- [ ] Todos os itens do checklist de [04-menu-cli.md, seção 11](../04-menu-cli.md#11-pronto-quando)
- [ ] Cadastrar o pet de exemplo (`Florzinha da Silva`) e achá-lo depois buscando `FLOR` e `siames`
- [ ] Digitar `abc`, `0`, `-1` e `99` no menu não derruba o programa
- [ ] Cadastrar com nome `Rex` (sem sobrenome) mostra `Informe nome e sobrenome do pet.` e pergunta de novo
- [ ] Desligar o MySQL e abrir o programa mostra a mensagem amigável
- [ ] A pasta do projeto não tem nenhum `.txt` de pet

## Regra nível 2 (não obrigatória)

1. Buscar por **data de cadastro** (mês e ano), combinável com outros critérios.
2. Imprimir o termo da busca em **negrito** no resultado.

## Dicas

- Quem lê a entrada do teclado (`Scanner`) não deveria saber de SQL. Quem fala com o banco não deveria saber de `Scanner`. Cada um no seu pacote.
- Faça uma função só que pergunta, valida e repete. Você vai usá-la em todo campo do cadastro e da alteração.
- `nextInt()` seguido de `nextLine()` é uma armadilha clássica do `Scanner`. Leia sempre linha e converta você mesmo.
- Seu repositório do Passo 3 já tem a busca. Aqui você só monta os critérios a partir do que o usuário digitou.

## Erros comuns

- Colar as perguntas dentro do código "só para testar" e esquecer.
- Validar de novo no menu uma regra que já existe no domínio (duas fontes de verdade).
- `catch (Exception e)` genérico que esconde o problema.
- Deixar a conexão aberta ao sair do programa.

## Commit sugerido

```
refactor: replace file storage with database in cli
```
