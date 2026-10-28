# Passo 1: Projeto Maven e domínio

Crie o núcleo do sistema: o pet e as regras de negócio, em um projeto Maven. Sem banco, sem Spring, sem tela. Só o domínio.

## Antes de começar

- Ambiente pronto: [02-ambiente-e-banco.md](../02-ambiente-e-banco.md)
- Guia, Parte 2, seção **Maven + JDBC**: o que é o `pom.xml` e como criar um projeto Maven do zero no IntelliJ
- Regras: [01-regras-de-negocio.md](../01-regras-de-negocio.md)
- Modelo: [03-modelo-de-dados.md](../03-modelo-de-dados.md), seção 4

## O que entregar

- Projeto Maven (Java 17 ou superior) que compila com `mvn compile`
- Classes `Pet`, `Endereco`, `TipoPet` e `SexoPet`
- Exceções de regra de negócio com as mensagens padrão
- Um `main` simples de demonstração

## Requisitos

1. Crie o projeto Maven sozinho, sem gerador pronto. Entenda cada linha do `pom.xml`.
2. Organize pacotes por responsabilidade (sugestão em [02, seção 4](../02-ambiente-e-banco.md#4-estrutura-de-pastas-sugerida)).
3. Crie `TipoPet` e `SexoPet` como ENUM, com os valores `CACHORRO`, `GATO`, `MACHO` e `FEMEA`. A conversão para o texto de exibição é do front.
4. Crie `Endereco` com `numeroCasa`, `cidade` e `rua`.
5. Crie `Pet` com todos os campos da [tabela de tipos](../03-modelo-de-dados.md#tipos-java-dos-campos).
6. Implemente TODAS as regras da [seção 2 das regras](../01-regras-de-negocio.md#2-regras-por-campo): nome, tipo, sexo, endereço, idade, peso e raça.
7. Dado inválido lança exceção própria com a **mensagem padrão** da [tabela de mensagens](../01-regras-de-negocio.md#4-mensagens-de-erro-padrão).
8. Idade e peso são números exatos (`BigDecimal`). O front já converte vírgula em ponto antes de enviar.
9. Campo opcional vazio fica `null` no objeto. O texto `NÃO INFORMADO` não existe no backend.
10. O `main` cria um pet válido e tenta criar pelo menos três inválidos (um de nome, um de peso, um de idade), mostrando a mensagem de cada erro.

## Pronto quando

- [ ] `mvn compile` termina sem erro
- [ ] Todas as linhas das tabelas "válido/erro" da seção 2 das regras se comportam como descrito
- [ ] Campo opcional vazio vira `null`, e não `0` nem texto vazio
- [ ] Nenhuma regra está duplicada em mais de um lugar
- [ ] Nenhum `System.out.println` dentro das classes de domínio

## Dicas

- Pense em **onde** a validação mora: no construtor, num método estático de criação ou numa classe separada. Escolha uma e mantenha em todos os campos.
- Um objeto `Pet` válido nunca deveria existir "pela metade". Evite setters que permitem pular a validação.
- Um objeto `Pet` pode ser válido com vários campos `null`. Pense em como isso aparece no seu código.
- Valide o formato (tem só letras?) separado da regra de negócio (é maior que 20?).

## Erros comuns

- Transformar campo vazio em `0` ou em `""` em vez de `null`.
- Validar só no `main` e deixar as classes de domínio aceitarem qualquer coisa.
- Usar `double` para peso e se surpreender com `0.30000000000000004`.
- Mensagem de erro genérica (`"Dado inválido"`) em vez da mensagem padrão.

## Commit sugerido

```
feat: add pet domain with validation rules
```
