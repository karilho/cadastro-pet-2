# Próximos passos: a Parte 3 aplicada ao V2

Concluiu o Passo 5? O seu V2 já é um projeto de portfólio. Daqui pra frente é com você: siga a **Parte 3** do guia ([devmagro.com/roadmaps/virando-junior](https://devmagro.com/roadmaps/virando-junior)) e aplique cada tema **neste mesmo projeto**.

Esta parte é **aberta**. Não tem requisito fechado nem solução pronta. Cada tema traz um objetivo e ideias pra explorar. Você decide o escopo, integra do seu jeito e, se quiser, faz o front também.

## Como usar esta parte

- Cada tema é independente. Faça na ordem que quiser, não é obrigatório fazer todos.
- A ordem do guia (Docker, Swagger, CI/CD, Cloud, Segurança, NoSQL, Cache) é uma boa ordem, porque cada tema ajuda o seguinte.
- Cada tema vira **um commit ou um grupo de commits** próprio.
- Mantenha o `mvn test` passando.
- Atualize o **seu** README a cada tema: o que fez, por que fez assim e como rodar.

## Resumo

| Tema | Sugestão pro V2 | Seção da Parte 3 |
|---|---|---|
| Docker | Subir API, PostgreSQL e front com um comando | Docker |
| Swagger | Documentar os endpoints com OpenAPI | Swagger |
| CI/CD | Pipeline rodando build e testes a cada push | CI/CD |
| Cloud | Foto do pet no S3 e deploy na AWS | Cloud |
| Segurança | Login com JWT protegendo a escrita | Segurança |
| NoSQL | Histórico de alterações no MongoDB | NoSQL |
| Cache | Cache com Redis na leitura | Cache |

---

## 1. Docker

**Objetivo:** subir o sistema inteiro com um comando, em qualquer máquina.

**Ideias pra explorar:**

- `Dockerfile` da API, de preferência multi-stage e com usuário sem root.
- `docker-compose.yml` com a API, o PostgreSQL e o front.
- O banco carregando o `schema.sql` e o `seed.sql` no primeiro boot.
- A API esperando o banco ficar saudável (`healthcheck` e `depends_on`).
- Senha e URL do banco em variáveis de ambiente (`.env` fora do Git).
- Volume pro banco, pra `docker compose down` não apagar os pets.

## 2. Swagger (OpenAPI)

**Objetivo:** quem usa a sua API entende tudo sem perguntar nada.

**Ideias pra explorar:**

- `springdoc-openapi` gerando a documentação no navegador.
- Descrição de cada endpoint e dos status possíveis (`200`, `201`, `204`, `400`, `404`).
- O formato de erro padrão como schema.
- Descrição e exemplo pra cada filtro do `GET /pets` e pro corpo de `POST` e `PUT`.

## 3. CI/CD (GitHub Actions)

**Objetivo:** o GitHub verifica o seu código a cada alteração, sem você pedir.

**Ideias pra explorar:**

- `.github/workflows/ci.yml` disparando em todo `push` e `pull request`.
- JDK com cache das dependências do Maven e `mvn -B verify`.
- Testes de repositório com Testcontainers (o runner do GitHub já tem Docker).
- Badge do pipeline no topo do README.
- Proteger a `main` pra só aceitar merge com pipeline verde.

## 4. Cloud (AWS)

**Objetivo:** o sistema fora do seu computador, com um recurso de nuvem de verdade.

**Ideias pra explorar:**

- Endpoint pra enviar a foto do pet, guardando o arquivo no S3 e só a chave no banco.
- Link temporário (presigned URL) pra ver a foto.
- Rodar o S3 local com LocalStack pra estudar sem gastar.
- Deploy numa máquina EC2 com o seu `docker-compose`, abrindo só as portas necessárias.
- Uma fila SQS recebendo uma mensagem a cada pet cadastrado.

**Cuidado:** credenciais da AWS nunca no código nem no Git, nem no histórico. Configure alerta de custo antes de criar qualquer recurso.

## 5. Segurança (Spring Security + JWT)

**Objetivo:** ler é público, escrever exige login.

**Ideias pra explorar:**

- Tabela de usuário com senha em hash BCrypt, nunca em texto puro.
- `POST /auth/login` devolvendo um token JWT com expiração.
- Segredo de assinatura em variável de ambiente.
- Papéis (`USER` e `ADMIN`): leitura aberta, escrita só pra `ADMIN`.
- `401` sem token e `403` sem permissão, no formato padrão de erro.
- Tela de login no front.

## 6. NoSQL (MongoDB)

**Objetivo:** usar o banco certo pro dado certo. Histórico de alterações é dado de log, ótimo pra documento.

**Ideias pra explorar:**

- Um documento por criação, alteração e exclusão de pet, com o estado antes e depois.
- `GET /pets/{id}/historico` do mais recente pro mais antigo.
- O PostgreSQL continua sendo a fonte da verdade. O Mongo só guarda o histórico.
- Decidir e explicar no README: se o Mongo cair, o cadastro falha ou segue?

## 7. Cache (Redis)

**Objetivo:** responder mais rápido sem sobrecarregar o banco.

**Ideias pra explorar:**

- Cache em `GET /pets/{id}` e na listagem.
- A chave da listagem incluindo os filtros (`nome=flor` e `nome=rex` não podem compartilhar resposta).
- Um TTL com justificativa.
- `POST`, `PUT` e `DELETE` invalidando o que ficou desatualizado.
- Medir antes e depois (tempo de resposta e consultas ao banco) e colocar os números no README.

---

## Quando terminar

Você terá um projeto com banco relacional e NoSQL, API REST documentada e testada, pipeline de CI, containers, autenticação, cache e deploy na nuvem. Esse é o projeto que você coloca no topo do currículo e do LinkedIn, e do qual consegue falar por uma hora em entrevista.
