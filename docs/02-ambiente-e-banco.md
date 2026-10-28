# 02. Ambiente e banco de dados

Antes do Passo 1, deixe o ambiente pronto. Este documento tem tudo, na ordem.

## 1. O que instalar

| Ferramenta | Versão | Para que serve |
|---|---|---|
| JDK | 17 ou superior | Compilar e rodar o Java |
| Maven | 3.9 ou superior | Dependências e build |
| MySQL | 8.x | Banco de dados |
| IntelliJ IDEA (Community) | Qualquer recente | IDE |
| Git | Qualquer recente | Versionamento |
| Cliente HTTP (Postman, Insomnia ou `curl`) | Qualquer | Testar a API no Passo 5 |

Confira tudo no terminal:

```bash
java -version
mvn -v
mysql --version
git --version
```

Os quatro comandos precisam responder com a versão. Se algum falhar, instale antes de continuar.

### Instalando o MySQL

- **macOS:** `brew install mysql` e depois `brew services start mysql`
- **Windows:** baixe o MySQL Installer no site oficial e escolha "Server only"
- **Linux (Ubuntu/Debian):** `sudo apt install mysql-server` e `sudo systemctl start mysql`

O curso de SQL do guia (Parte 2, seção 2) tem uma aula só sobre a instalação do MySQL.

## 2. Criando o banco e o usuário

Entre no MySQL como administrador:

```bash
mysql -u root -p
```

Rode o script abaixo. Ele cria o banco, um usuário só para a aplicação e dá a esse usuário acesso somente a esse banco:

```sql
CREATE DATABASE cadastro_pet
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;

CREATE USER 'pet_app'@'localhost' IDENTIFIED BY 'troque_esta_senha';

GRANT ALL PRIVILEGES ON cadastro_pet.* TO 'pet_app'@'localhost';

FLUSH PRIVILEGES;
```

Troque `troque_esta_senha` por uma senha sua.

### Por que `utf8mb4_0900_ai_ci`

| Parte | Significado |
|---|---|
| `utf8mb4` | Charset que aceita acentos e emojis |
| `ai` | **A**ccent **i**nsensitive: `siames` e `Siamês` são iguais |
| `ci` | **C**ase **i**nsensitive: `FLOR` e `flor` são iguais |

Essa collation resolve no banco a regra de busca "ignorar maiúsculas, minúsculas e acentos" da [seção 5 das regras](01-regras-de-negocio.md#5-busca-de-pets). Ao fazer o Passo 2, comprove isso com uma consulta.

### Conferindo que deu certo

Saia do MySQL (`exit`) e entre com o usuário da aplicação:

```bash
mysql -u pet_app -p cadastro_pet
```

Dentro do MySQL:

```sql
SELECT DATABASE();
SHOW TABLES;
```

`SELECT DATABASE();` deve mostrar `cadastro_pet`. `SHOW TABLES;` mostra vazio por enquanto, é o esperado.

## 3. Configuração por variáveis de ambiente

A URL, o usuário e a senha do banco **nunca** ficam no código nem sobem pro Git.

1. Copie o arquivo de exemplo deste repositório:

   ```bash
   cp .env.example .env
   ```

2. Edite o `.env` com a sua senha.
3. O `.env` já está no `.gitignore`. Confira com `git status`: ele não pode aparecer.

Variáveis usadas:

| Variável | Exemplo | Para que serve |
|---|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3306/cadastro_pet?serverTimezone=America/Sao_Paulo` | Endereço do banco |
| `DB_USER` | `pet_app` | Usuário do banco |
| `DB_PASSWORD` | (a sua senha) | Senha do usuário |

### Como o Java enxerga essas variáveis

- No terminal: `export DB_USER=pet_app` (macOS/Linux) antes de rodar.
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

| Erro | Causa provável | Solução |
|---|---|---|
| `Access denied for user 'pet_app'` | Senha errada ou usuário não criado | Refaça o `CREATE USER` e o `GRANT` |
| `Communications link failure` | MySQL parado | Inicie o serviço do MySQL |
| `Unknown database 'cadastro_pet'` | Banco não criado | Rode o `CREATE DATABASE` |
| `The server time zone value ... is unrecognized` | Faltou `serverTimezone` na URL | Use a `DB_URL` do `.env.example` |
| `Public Key Retrieval is not allowed` | Autenticação do MySQL 8 | Acrescente `&allowPublicKeyRetrieval=true` na URL (só em ambiente local) |
| `No suitable driver found` | Driver do MySQL fora do `pom.xml` | Adicione a dependência `mysql-connector-j` |
| Acentos viram `?` | Charset errado | Confira `utf8mb4` no banco e na URL |

## 6. Pronto quando

- [ ] Os quatro comandos de versão respondem
- [ ] Você entra no MySQL com `mysql -u pet_app -p cadastro_pet`
- [ ] O `.env` existe e NÃO aparece no `git status`
- [ ] Você sabe explicar o que é `utf8mb4_0900_ai_ci`
