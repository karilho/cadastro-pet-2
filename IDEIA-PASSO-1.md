# Ideia de solução: Passo 1 (API completa, sem banco)

> **Spoiler.** Esta branch é **uma ideia** de como resolver o Passo 1. Não é o único caminho. Tente fazer o seu antes de olhar.

## O que esta branch entrega

Os 5 endpoints do README funcionando de ponta a ponta, com camada de controller e de serviço, validação e tratamento de erros. O front pronto (`frontend/`) funciona **inteiro** contra ela.

**A única coisa que falta é o banco de dados.** Os pets ficam guardados em **memória** e somem quando a aplicação reinicia. É de propósito: o Passo 1 prova que a API e o front conversam direito. O Passo 2 troca só o armazenamento.

## Como rodar

```bash
# Backend (porta 8080)
mvn spring-boot:run

# Front (porta 5500), em outro terminal, na raiz do repositório
python3 -m http.server 5500 --directory frontend
```

Abra http://localhost:5500. A lista começa **vazia**: cadastre pelo menu. Testes: `mvn test`.

## Estrutura

| Pacote | O que mora aqui |
|---|---|
| `controller` | `PetController` (só traduz HTTP) e `TratadorDeErros` (todo erro vira o mesmo JSON) |
| `service` | `PetService`: regras que não cabem em anotação (tipo e sexo não mudam, normalização) |
| `repository` | `PetRepository` (o contrato) e `InMemoryPetRepository` (a implementação em memória) |
| `model` | `Pet`, `Endereco`, `TipoPet`, `SexoPet` |
| `dto` | O que entra (`PetRequest`) e o que sai (`PetResponse`, `ErroResponse`) |
| `validation` | Validação do nome e as mensagens padrão |
| `config` | CORS e configuração do Jackson |

## Decisões e o porquê

| Decisão | Por quê |
|---|---|
| `PetRepository` é uma **interface** | O Passo 2 cria uma implementação com banco e o service e o controller **não mudam** |
| Armazenamento em memória com `ConcurrentHashMap` | Simples, sem banco, e seguro para várias requisições ao mesmo tempo |
| Entidade `Pet` é um `record` | Imutável: alterar um pet cria um novo, sem setters espalhados |
| DTOs separados da entidade | A API não expõe o modelo interno, e o `PetRequest` carrega a validação |
| Bean Validation nos DTOs | As regras ficam declaradas perto dos dados |
| Validador próprio para o nome | O nome tem duas mensagens diferentes (sem sobrenome e caractere inválido), e anotação simples não cobre |
| Grupo de validação `Cadastro` | Tipo e sexo são obrigatórios no cadastro, mas podem nem vir no `PUT` (nunca mudam) |
| `TratadorDeErros` único | Todo erro sai no mesmo formato, e o `500` não vaza detalhe |
| Texto vazio em enum vira `null` | O select vazio do front chega como `""`. Sem isso, o erro de `tipo` escondia os outros erros |
| CORS por propriedade (`app.cors.allowed-origin`) | A origem do front fica em configuração, não no código |

## O que muda no Passo 2

Só a camada de armazenamento:

1. Modelar as tabelas e criar o `schema.sql`.
2. Criar uma segunda implementação de `PetRepository` que usa o PostgreSQL.
3. Apontar a aplicação para ela.

O `PetController`, o `PetService`, os DTOs e os testes de API continuam iguais. Esse é o teste de que a separação em camadas está certa.
