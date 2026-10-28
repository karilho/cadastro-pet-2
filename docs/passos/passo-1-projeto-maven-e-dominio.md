# Passo 1: Projeto Maven e domínio

Leve para um projeto Maven tudo o que você já tinha no V1: o pet e as regras. Sem banco, sem terminal elaborado, sem Spring. Só o núcleo.

## Antes de começar

- Ambiente pronto: [02-ambiente-e-banco.md](../02-ambiente-e-banco.md)
- Guia, Parte 2, seção **Maven + JDBC**: o que é o `pom.xml` e como criar um projeto Maven do zero no IntelliJ
- Regras: [01-regras-de-negocio.md](../01-regras-de-negocio.md)
- Modelo: [03-modelo-de-dados.md](../03-modelo-de-dados.md), seção 4

## O que entregar

- Projeto Maven (Java 17 ou superior) que compila com `mvn compile`
- Classes `Pet`, `Endereco`, `TipoPet` e `SexoPet`
- Constante `NAO_INFORMADO` em um único lugar
- Exceções de regra de negócio com as mensagens padrão
- Um `main` simples de demonstração

## Requisitos

1. Crie o projeto Maven sozinho, sem gerador pronto. Entenda cada linha do `pom.xml`.
2. Organize pacotes por responsabilidade (sugestão em [02, seção 4](../02-ambiente-e-banco.md#4-estrutura-de-pastas-sugerida)).
3. Crie `TipoPet` e `SexoPet` como ENUM, com o texto de exibição (`Cachorro`, `Femea`...) dentro do próprio enum.
4. Crie `Endereco` com `numeroCasa`, `cidade` e `rua`.
5. Crie `Pet` com todos os campos da [tabela de tipos](../03-modelo-de-dados.md#tipos-java-dos-campos).
6. Implemente TODAS as regras da [seção 2 das regras](../01-regras-de-negocio.md#2-regras-por-campo): nome, tipo, sexo, endereço, idade, peso e raça.
7. Dado inválido lança exceção própria com a **mensagem padrão** da [tabela de mensagens](../01-regras-de-negocio.md#4-mensagens-de-erro-padrão).
8. Idade e peso aceitam entrada com vírgula ou ponto (`"3,5"` e `"3.5"`). Guardam com ponto.
9. Campo opcional vazio fica `null` no objeto. A constante `NAO_INFORMADO` só aparece quando você EXIBE.
10. O `main` cria um pet válido e tenta criar pelo menos três inválidos (um de nome, um de peso, um de idade), mostrando a mensagem de cada erro.

## Pronto quando

- [ ] `mvn compile` termina sem erro
- [ ] Todas as linhas das tabelas "válido/erro" da seção 2 das regras se comportam como descrito
- [ ] `NÃO INFORMADO` existe escrito em um único lugar do código (confira com uma busca no projeto)
- [ ] Nenhuma regra está duplicada em mais de um lugar
- [ ] Nenhum `System.out.println` dentro das classes de domínio

## Dicas

- Pense em **onde** a validação mora: no construtor, num método estático de criação ou numa classe separada. Escolha uma e mantenha em todos os campos.
- Um objeto `Pet` válido nunca deveria existir "pela metade". Evite setters que permitem pular a validação.
- Converter `"3,5"` em número exige trocar a vírgula antes de converter. Pense onde esse tratamento fica.
- Valide o formato (tem só letras?) separado da regra de negócio (é maior que 20?).

## Erros comuns

- Escrever `"NÃO INFORMADO"` direto em vários arquivos.
- Validar só no `main` e deixar as classes de domínio aceitarem qualquer coisa.
- Usar `double` para peso e se surpreender com `0.30000000000000004`.
- Mensagem de erro genérica (`"Dado inválido"`) em vez da mensagem padrão.

## Commit sugerido

```
feat: add pet domain with validation rules
```
