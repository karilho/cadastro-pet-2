# Passo 4: Repositório com Spring Data JPA

Agora que você escreveu cada SQL na mão, deixe o Spring gerar. Crie uma **terceira** implementação de `PetRepository`, agora com Spring Data JPA, e compare com a de JDBC.

## Antes de começar

- Passo 3 concluído
- Guia, Parte 2, seção **Web em Java**, subseção **Spring Data JPA**

## O que entregar

- Starter `data-jpa` no `pom.xml`
- Entidade JPA mapeando a tabela que **você** modelou no Passo 2
- Implementação de `PetRepository` usando Spring Data JPA
- A aplicação usando a implementação JPA
- Um texto curto (no seu README) comparando JDBC e JPA

## A regra de ouro continua

**Controller e service não mudam.** De novo.

## Requisitos

1. O `schema.sql` do Passo 2 continua sendo a fonte da verdade. Configure o JPA para **validar** o schema, nunca para criar ou apagar tabelas (`ddl-auto=validate`).
2. Separe a **entidade JPA** do `Pet` do domínio, ou justifique no README por que usou a mesma classe.
3. Busca com filtros opcionais: escolha **Specifications**, `@Query` ou consulta derivada e justifique.
4. A busca de texto continua ignorando caixa e acento, como no Passo 2.
5. A implementação JDBC fica no projeto (ou no histórico do Git). Só uma é usada por vez.
6. O teste do repositório do Passo 3 roda também contra a implementação JPA, com o mesmo resultado.

## Pronto quando

- [ ] O front funciona exatamente igual ao Passo 3
- [ ] O diff do passo não toca em controller nem service
- [ ] Apagar uma coluna do banco faz a aplicação **não subir** (prova que o `validate` está ligado)
- [ ] Os mesmos testes de repositório passam com JDBC e com JPA
- [ ] Você explica, no README, o que o `save()` faz por baixo

## Para pensar (e responder no README)

- Quantas linhas tinha o repositório JDBC? E o JPA?
- Qual SQL o JPA gerou para a sua busca? Ligue `spring.jpa.show-sql=true` e compare com o seu.
- Em que situação você voltaria para o JDBC?

## Ideia de solução

Branch [`ideia/passo-4`](https://github.com/karilho/cadastro-pet-2/tree/ideia/passo-4).

## Commit sugerido

```
feat: add spring data jpa pet repository
```
