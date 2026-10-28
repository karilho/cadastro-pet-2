# Desafio PROGRAMAÇÃO - Cadastro de Pets V2

### Desafio criado por Lucas Carrilho - [@devmagro](https://www.linkedin.com/in/karilho/)

Twitter: [@devmagro](https://x.com/devmagro)

Instagram: [@devmagro](https://instagram.com/devmagro)

YouTube: [@devmagro](https://www.youtube.com/@devmagro)

## Introdução
Este é o **segundo grande desafio do guia**. Você já fez o [Sistema de Cadastros V1](https://github.com/karilho/desafioCadastro), com Java puro e arquivos `.txt`. Agora vamos evoluir o MESMO sistema usando o que você aprendeu na **Parte 2** do guia: SQL, JDBC, Maven e Spring Boot.

No fim, o abrigo de animais deixa de ter um programa de terminal salvando arquivos e passa a ter uma **API REST** com banco de dados, validações, tratamento de erros e testes.

Leia TODO o desafio com ATENÇÃO antes de começar. Evite o uso do GPT ao máximo e use-o com sabedoria quando necessário. Travar faz parte, é aí que você aprende.

Guia completo: [devmagro.com/roadmaps/virando-estagiario](https://devmagro.com/roadmaps/virando-estagiario)

## Objetivo
Pegar o domínio do V1 (um abrigo que cadastra pets para adoção) e reconstruí-lo em camadas:

1. Projeto Maven com o domínio e as regras do V1
2. Banco de dados relacional modelado com SQL
3. Persistência com JDBC puro
4. O menu do V1 rodando em cima do banco
5. API REST com Spring Boot
6. Testes automatizados

Depois do Passo 6, você segue para os [próximos passos](docs/proximos-passos.md), que são a **Parte 3** do guia aplicada no mesmo projeto.

## Conhecimentos que você irá aplicar
- Maven e gerenciamento de dependências
- Modelagem e consultas SQL (PK, FK, índice, filtros, agregações)
- JDBC (`PreparedStatement`, `try-with-resources`, transação)
- Spring Boot, Spring Web e Spring Data JPA
- Validação e tratamento de erros em API
- Códigos de status HTTP
- Testes automatizados com JUnit e Mockito
- Git com Conventional Commits

## Como usar este repositório
Este repositório tem **só as orientações**. O código é seu.

1. Crie um **Fork** e clone na sua máquina, e deixe sua ⭐ no repositório.
2. Deixe o ambiente pronto seguindo [02-ambiente-e-banco.md](docs/02-ambiente-e-banco.md).
3. Leia as [regras de negócio](docs/01-regras-de-negocio.md) por inteiro. Elas valem para todos os passos.
4. Siga os passos **em ordem**. Cada passo tem o seu documento com requisitos e checklist.
5. Crie o projeto você mesmo, do zero. Usar o gerador pronto sem entender o que ele gera é o caminho mais fácil de não aprender.
6. Faça **um commit por funcionalidade** concluída, seguindo [Conventional Commits](https://www.youtube.com/watch?v=sbK9h45Jc5o).
7. Organize pastas, pacotes e nomes como achar melhor. **Não existe um caminho certo.**
8. Só olhe as ideias de solução depois de tentar. Elas estão em outras branches, e o guia explica onde ao final da Parte 2.

**Requisitos de ambiente:** Java 17 ou superior, Maven, MySQL 8, Git e uma IDE (IntelliJ, por exemplo).

## Mapa do desafio

### Documentos de referência (leia antes de começar)

| Documento | O que tem |
|---|---|
| [01. Regras de negócio](docs/01-regras-de-negocio.md) | Todos os campos, validações, mensagens de erro, busca, alteração e exclusão |
| [02. Ambiente e banco](docs/02-ambiente-e-banco.md) | O que instalar, como criar o banco e o usuário, variáveis de ambiente, estrutura de pastas |
| [03. Modelo de dados](docs/03-modelo-de-dados.md) | Tabelas, colunas, índices, models do Java, dados de exemplo e resultados esperados |
| [04. Menu de terminal](docs/04-menu-cli.md) | Telas, perguntas e fluxos do programa de terminal |
| [05. Contrato da API](docs/05-contrato-da-api.md) | Endpoints, JSON, filtros, status e formato de erro |

### Passos (faça em ordem)

| Passo | Documento | Resultado |
|---|---|---|
| 1 | [Projeto Maven e domínio](docs/passos/passo-1-projeto-maven-e-dominio.md) | Pet e regras de negócio em Java |
| 2 | [Modelagem SQL](docs/passos/passo-2-modelagem-sql.md) | `schema.sql`, `seed.sql` e `queries.sql` |
| 3 | [Persistência com JDBC](docs/passos/passo-3-persistencia-jdbc.md) | Repositório de pets com JDBC puro |
| 4 | [O menu em cima do banco](docs/passos/passo-4-menu-no-banco.md) | O V1 refatorado, rodando no MySQL |
| 5 | [API REST com Spring Boot](docs/passos/passo-5-api-rest.md) | Cinco endpoints, validação e erros |
| 6 | [Testes](docs/passos/passo-6-testes.md) | Regras, service, controller e repository testados |
| EXTRA | [Formulário dinâmico](docs/passos/passo-extra-formulario-dinamico.md) | Perguntas e respostas no banco (opcional) |

### Depois

| Documento | O que tem |
|---|---|
| [Próximos passos](docs/proximos-passos.md) | Docker, Swagger, CI/CD, Cloud, Segurança, NoSQL e Cache aplicados no V2 |
| [Checklist de entrega](docs/checklist-de-entrega.md) | O que conferir antes de dizer "terminei" |

## Arquivos prontos neste repositório

| Arquivo | Para que serve |
|---|---|
| [`formulario.txt`](formulario.txt) | As perguntas do formulário, que o seu programa deve ler |
| [`.env.example`](.env.example) | Modelo das variáveis de ambiente do banco |
| [`.gitignore`](.gitignore) | Já ignora `target/`, IDEs e arquivos de segredo (inclusive o `.env`) |

## Início rápido

```bash
# 1. Fork no GitHub, depois:
git clone https://github.com/SEU_USUARIO/cadastro-pet-2.git
cd cadastro-pet-2

# 2. Variáveis do banco
cp .env.example .env     # e edite o .env com a sua senha

# 3. Banco de dados: siga docs/02-ambiente-e-banco.md
```

Depois é só abrir o [Passo 1](docs/passos/passo-1-projeto-maven-e-dominio.md).

## Considerações Finais

- **Faça commits à medida que concluir cada funcionalidade, seguindo sempre o padrão de [Conventional Commits](https://www.youtube.com/watch?v=sbK9h45Jc5o).**
- **Crie um `README.md` próprio, com a descrição do seu projeto e como executá-lo.** O [checklist de entrega](docs/checklist-de-entrega.md) diz o que ele precisa ter.
- **Seu projeto precisa compilar e passar nos testes em uma máquina limpa.**

### Gostou do desafio?
Se você chegou até aqui, parabéns! Você saiu de um programa de terminal com arquivos e chegou numa API com banco e testes. Esse é o tipo de projeto que dá o que falar em entrevista. 🚀
