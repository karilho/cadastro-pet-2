# Próximos passos: a Parte 3 aplicada ao V2

Concluiu o Passo 6? O seu V2 já é um projeto de portfólio. Agora você segue a **Parte 3** do guia ([devmagro.com/roadmaps/virando-junior](https://devmagro.com/roadmaps/virando-junior)) e aplica cada tema **neste mesmo projeto**.

## Como usar esta parte

- Cada tema é independente. Faça na ordem que quiser, não é obrigatório fazer todos.
- A ordem do guia (Docker, Swagger, CI/CD, Cloud, Segurança, NoSQL, Cache) é uma boa ordem, porque cada tema ajuda o seguinte.
- Cada tema vira **um commit ou um grupo de commits** próprio.
- Cada tema mantém o `mvn test` passando.
- Atualize o **seu** README a cada tema concluído.

## Resumo

| Tema | Entrega no V2 | Seção da Parte 3 |
|---|---|---|
| Docker | `Dockerfile` e `docker-compose` com API e PostgreSQL | Docker |
| Swagger | Todos os endpoints documentados com OpenAPI | Swagger |
| CI/CD | Pipeline do GitHub Actions rodando build e testes | CI/CD |
| Cloud | Deploy na AWS e foto do pet no S3 | Cloud |
| Segurança | Login com JWT protegendo escrita | Segurança |
| NoSQL | Histórico de alterações no MongoDB | NoSQL |
| Cache | Cache com Redis na leitura | Cache |

---

## 1. Docker

**Objetivo:** subir o sistema inteiro com um comando, em qualquer máquina.

**Entregar:**

- `Dockerfile` da API
- `.dockerignore`
- `docker-compose.yml` com a API e o PostgreSQL

**Requisitos:**

1. O `Dockerfile` gera a imagem a partir do projeto Maven.
2. O PostgreSQL do compose carrega o `schema.sql` e o `seed.sql` no primeiro boot.
3. A API só inicia depois que o banco está saudável (`healthcheck` e `depends_on`).
4. Usuário, senha e URL do banco entram por **variáveis de ambiente** (arquivo `.env`, que não sobe pro Git).
5. Os dados do banco persistem em um **volume**: `docker compose down` não apaga os pets.

**Pronto quando:**

- [ ] `docker compose up` sobe tudo do zero e `GET /pets` responde
- [ ] Os 10 pets do seed aparecem no primeiro boot
- [ ] `docker compose down` e `up` de novo mantêm os dados
- [ ] Nenhuma senha escrita no `docker-compose.yml`

## 2. Swagger (OpenAPI)

**Objetivo:** quem usa a sua API entende tudo sem perguntar nada.

**Entregar:** documentação gerada com `springdoc-openapi`, acessível pelo navegador.

**Requisitos:**

1. Todos os endpoints aparecem, com descrição do que fazem.
2. Cada endpoint documenta os status possíveis (`200`, `201`, `204`, `400`, `404`).
3. O formato de erro padrão aparece como schema.
4. Cada parâmetro de filtro do `GET /pets` tem descrição e exemplo.
5. Exemplos de corpo para `POST` e `PUT`.

**Pronto quando:**

- [ ] Dá para cadastrar, buscar, alterar e excluir um pet só pela tela do Swagger
- [ ] Os erros `400` e `404` aparecem documentados com exemplo
- [ ] A documentação não fica desatualizada em relação ao [contrato](05-contrato-da-api.md)

## 3. CI/CD (GitHub Actions)

**Objetivo:** o GitHub verifica o seu código a cada alteração, sem você pedir.

**Entregar:** `.github/workflows/ci.yml`.

**Requisitos:**

1. Dispara em todo `push` e em todo `pull request`.
2. Instala o JDK, usa **cache** das dependências do Maven e roda `mvn -B verify`.
3. Roda **sem PostgreSQL**. É por isso que o Passo 6 usa H2.
4. Publica o resultado dos testes como artefato do workflow.
5. Badge de status do pipeline no topo do **seu** README.

**Pronto quando:**

- [ ] Um commit com teste quebrado deixa o pipeline **vermelho**
- [ ] Corrigir deixa **verde**
- [ ] O badge aparece no README e reflete o estado real

**Desafio extra:** proteger a branch `main` para só aceitar merge com o pipeline verde.

## 4. Cloud (AWS)

**Objetivo:** o sistema fora do seu computador, com um recurso de nuvem de verdade.

### 4.1 Foto do pet no S3

1. Novo endpoint `POST /pets/{id}/foto` (envio de arquivo).
2. Aceita só imagem (`jpg` e `png`) e de até 2MB. Fora disso, `400` no formato padrão.
3. O arquivo vai para um bucket S3. No banco fica só a **chave** do arquivo (nova coluna no `pet`).
4. `GET /pets/{id}` devolve um link temporário para ver a foto.
5. **Credenciais da AWS nunca ficam no código** nem no Git.

### 4.2 Deploy

1. A aplicação roda em uma máquina na AWS (EC2 com o seu `docker-compose`, por exemplo).
2. Só as portas necessárias ficam abertas.
3. Segredos (senha do banco, chaves) vêm de variáveis de ambiente ou do Systems Manager, nunca do repositório.

### 4.3 Fila (opcional)

Ao cadastrar um pet, publique uma mensagem em uma fila SQS. Um consumidor simples lê e registra no log. Isso demonstra o conceito de processamento assíncrono.

**Pronto quando:**

- [ ] A URL pública responde `GET /pets`
- [ ] Subir uma foto e abri-la pelo link funciona
- [ ] Nenhuma credencial da AWS no repositório (inclusive no histórico)
- [ ] Você explica para que serve cada serviço que usou

## 5. Segurança (Spring Security + JWT)

**Objetivo:** ler é público, escrever exige login.

**Entregar:** autenticação com token JWT.

**Requisitos:**

1. Tabela `usuario`: `id`, `email` (único), `senha_hash`, `papel` (`USER` ou `ADMIN`).
2. A senha é guardada com **hash BCrypt**. Nunca em texto puro.
3. `POST /auth/login` recebe email e senha e devolve um token JWT.
4. O token tem **expiração**. O segredo de assinatura vem de variável de ambiente.
5. Regras de acesso:

| Rota | Quem pode |
|---|---|
| `GET /pets` e `GET /pets/{id}` | Qualquer pessoa |
| `POST`, `PUT` e `DELETE` em `/pets` | Somente `ADMIN` |
| `POST /auth/login` | Qualquer pessoa |

6. Sem token em rota protegida: `401`. Token válido sem permissão: `403`. Ambos no [formato padrão de erro](05-contrato-da-api.md#3-formato-de-erro).

**Pronto quando:**

- [ ] `POST /pets` sem token devolve `401`
- [ ] `POST /pets` com token `USER` devolve `403`
- [ ] `POST /pets` com token `ADMIN` devolve `201`
- [ ] Nenhuma senha em texto puro no banco nem nos logs
- [ ] Tem teste para cada linha da tabela de acesso

## 6. NoSQL (MongoDB)

**Objetivo:** usar o banco certo para o dado certo. O histórico de alterações é um dado de log, ótimo para documento.

**Entregar:** coleção `historico_pet` no MongoDB.

**Requisitos:**

1. Cada criação, alteração e exclusão de pet grava um documento com: `petId`, `acao` (`CRIADO`, `ALTERADO` ou `EXCLUIDO`), `em` (data e hora), `antes` e `depois` (o estado do pet).
2. Novo endpoint `GET /pets/{id}/historico`, do mais recente para o mais antigo.
3. O **PostgreSQL continua sendo a fonte da verdade**. O Mongo só guarda o histórico.
4. Decida e **documente no seu README**: se o Mongo cair, o cadastro do pet falha ou segue? Justifique.

**Pronto quando:**

- [ ] Criar, alterar e excluir geram um documento cada
- [ ] O histórico de um pet aparece em ordem
- [ ] Você explica por que esse dado ficou no Mongo e não no PostgreSQL

## 7. Cache (Redis)

**Objetivo:** responder mais rápido sem sobrecarregar o banco.

**Entregar:** cache na leitura de pets.

**Requisitos:**

1. Cache em `GET /pets/{id}` e na listagem com filtros.
2. A chave do cache da listagem **inclui os filtros** (`nome=flor` e `nome=rex` não podem compartilhar resposta).
3. Defina um tempo de expiração (TTL) e justifique o valor.
4. Todo `POST`, `PUT` e `DELETE` **invalida** o que ficou desatualizado.
5. **Meça** antes e depois (tempo de resposta e quantidade de consultas ao banco) e registre os números no seu README.

**Pronto quando:**

- [ ] A segunda chamada igual não consulta o banco
- [ ] Alterar um pet e buscar de novo devolve o dado novo, não o antigo
- [ ] Você mostra os números antes e depois
- [ ] Você explica o risco de dado desatualizado e como o seu projeto evita isso

---

## Quando terminar

Você terá um projeto com banco relacional e NoSQL, API REST documentada e testada, pipeline de CI, containers, autenticação, cache e deploy na nuvem. Esse é o projeto que você coloca no topo do currículo e do LinkedIn, e do qual consegue falar por uma hora em entrevista.
