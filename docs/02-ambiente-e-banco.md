# 02. Ambiente e banco de dados

Antes do Passo 1, deixe o ambiente pronto. Aqui você descobre **o que** precisa existir. O **como** fazer no banco é com você: pesquise na documentação oficial e no que aprendeu no curso de SQL.

## 1. O que instalar

| Ferramenta | Versão | Para que serve |
|---|---|---|
| JDK | 17 ou superior | Compilar e rodar o Java |
| Maven | 3.9 ou superior | Dependências e build |
| PostgreSQL | 15 ou superior | Banco de dados |
| Cliente de banco (DBeaver, pgAdmin ou o `psql`) | Qualquer | Rodar SQL e ver os dados |
| IntelliJ IDEA (Community) | Qualquer recente | IDE |
| Git | Qualquer recente | Versionamento |
| Cliente HTTP (Postman, Insomnia ou `curl`) | Qualquer | Testar a API no Passo 5 |

Confira no terminal:

```bash
java -version
mvn -v
psql --version
git --version
```

Os quatro comandos precisam responder com a versão. Se algum falhar, instale antes de continuar.

### Instalando o PostgreSQL

- **macOS:** Homebrew (`brew install postgresql@16`) e depois subir o serviço
- **Windows:** instalador oficial em postgresql.org
- **Linux (Ubuntu/Debian):** pacote `postgresql` do `apt`

Anote a senha do usuário administrador (`postgres`) que você definir na instalação.

> O curso de SQL do guia usa MySQL nas aulas. A linguagem SQL é a mesma e os conceitos valem igualmente. Onde o PostgreSQL se comportar diferente, a documentação oficial é a sua fonte.

## 2. Preparando o banco

Você precisa deixar três coisas prontas antes do Passo 2:

1. Um **banco de dados** chamado `cadastro_pet`, com encoding **UTF-8**.
2. Um **usuário só da aplicação**, sem poderes de administrador, com acesso apenas a esse banco.
3. A capacidade de **conectar com esse usuário**, pelo `psql` ou pelo cliente gráfico, e de criar tabelas nele.

Por que um usuário separado do administrador? Porque a aplicação nunca deve rodar com o poder de apagar o banco inteiro. Se o Java for comprometido, o estrago fica limitado.

Pontos para pesquisar:

- Como criar banco e usuário (`role`) no PostgreSQL
- Como dar ao usuário os privilégios certos, **inclusive** sobre o schema onde as tabelas serão criadas (nas versões 15 ou superiores isso muda em relação às antigas)
- Como o `psql` conecta com usuário, banco e porta

### Pronto quando

- [ ] Você conecta com o usuário da aplicação (não com `postgres`)
- [ ] Você cria e apaga uma tabela de teste com esse usuário
- [ ] O usuário da aplicação **não** consegue criar outro banco nem outro usuário
- [ ] Você sabe explicar a diferença entre banco, schema e tabela no PostgreSQL

## 3. Configuração por variáveis de ambiente

A URL, o usuário e a senha do banco **nunca** ficam no código nem sobem pro Git.

1. Copie o arquivo de exemplo deste repositório:

   ```bash
   cp .env.example .env
   ```

2. Edite o `.env` com os seus valores.
3. O `.env` já está no `.gitignore`. Confira com `git status`: ele não pode aparecer.

Variáveis usadas:

| Variável | Exemplo | Para que serve |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/cadastro_pet` | Endereço do banco |
| `DB_USER` | (o usuário da aplicação) | Usuário do banco |
| `DB_PASSWORD` | (a sua senha) | Senha do usuário |

### Como o Java enxerga essas variáveis

- No terminal: `export DB_USER=...` (macOS/Linux) antes de rodar.
- No IntelliJ: *Run > Edit Configurations > Environment variables*.
- No código: `System.getenv("DB_USER")`.
- No Spring Boot (Passo 5): `spring.datasource.url=${DB_URL}` no `application.properties`.

O `.env` não é lido sozinho pelo Java. Quem carrega é você (terminal ou IntelliJ).

## 4. Estrutura de pastas sugerida

Não é obrigatória. É um ponto de partida.

```
cadastro-pet-2/
├── database/
│   ├── schema.sql          (Passo 2)
│   ├── seed.sql            (Passo 2)
│   └── queries.sql         (Passo 2)
├── formulario.txt          (já vem pronto neste repositório)
├── pom.xml
├── .env                    (seu, não sobe pro Git)
├── .env.example
├── .gitignore
├── README.md               (o SEU, descrevendo o seu projeto)
└── src/
    ├── main/java/...
    └── test/java/...
```

Dentro de `src/main/java`, uma organização possível para o projeto crescer sem virar bagunça:

| Pacote | O que mora aqui |
|---|---|
| `model` | `Pet`, `Endereco`, enums |
| `exception` | Exceções de regra de negócio |
| `repository` | Acesso ao banco |
| `service` | Regras de negócio que não cabem no model |
| `cli` | Menu de terminal (Passo 4) |
| `controller` | Endpoints (Passo 5) |
| `dto` | Objetos de entrada e saída da API (Passo 5) |

## 5. Problemas comuns de conexão

| Erro | Pista |
|---|---|
| `password authentication failed for user` | Senha ou usuário errados. Confira o `.env` |
| `Connection refused` | O serviço do PostgreSQL não está rodando, ou a porta está errada |
| `database "cadastro_pet" does not exist` | O banco ainda não foi criado |
| `No suitable driver found` | Falta a dependência do driver do PostgreSQL no `pom.xml` |
| `permission denied for schema public` | O usuário da aplicação não tem permissão no schema. Revise os privilégios |
| Acentos viram `?` | Confira o encoding do banco e do terminal |

## 6. Pronto quando

- [ ] Os quatro comandos de versão respondem
- [ ] Você conecta no banco `cadastro_pet` com o usuário da aplicação
- [ ] O `.env` existe e NÃO aparece no `git status`
- [ ] Você conhece a diferença entre o usuário `postgres` e o usuário da aplicação
