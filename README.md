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
Pegar o domínio do V1 (um abrigo que cadastra pets para adoção) e reconstruí-lo em camadas, em cinco evoluções:

1. Projeto Maven com o domínio e as regras do V1
2. Banco de dados relacional modelado com SQL
3. Persistência com JDBC puro
4. O menu do V1 rodando em cima do banco
5. API REST com Spring Boot, validação e testes

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
2. Crie o projeto você mesmo, do zero. Usar o gerador pronto sem entender o que ele gera é o caminho mais fácil de não aprender.
3. Faça **um commit por funcionalidade** concluída, seguindo [Conventional Commits](https://www.youtube.com/watch?v=sbK9h45Jc5o).
4. Organize pastas, pacotes e nomes como achar melhor. **Não existe um caminho certo.**
5. Só olhe as ideias de solução depois de tentar. Elas estão em outras branches, e o guia explica onde ao final da Parte 2.

**Requisitos de ambiente:** Java 17 ou superior, Maven, MySQL 8 e uma IDE (IntelliJ, por exemplo).

# Regras de negócio (herdadas do V1)

Estas regras valem para TODOS os passos. Elas viram exceção no código e, a partir do Passo 5, viram resposta de erro da API.

| Campo | Regra |
|---|---|
| Nome e sobrenome | Obrigatórios. Somente letras de A a Z (sem números e sem caracteres especiais) |
| Tipo | `Cachorro` ou `Gato`, usando ENUM |
| Sexo | `Macho` ou `Femea`, usando ENUM |
| Endereço | Três campos: número da casa, cidade e rua |
| Idade | Aceita vírgula ou ponto. Maior que 20 anos lança exceção. Menor que 1 ano (em meses) vira `0.x` |
| Peso | Aceita vírgula ou ponto. Menor que 0.5kg ou maior que 60kg lança exceção |
| Raça | Sem números e sem caracteres especiais |

Se NOME, RAÇA, PESO, IDADE ou o NÚMERO da casa não forem informados, o valor deve ser `NÃO INFORMADO` (uma constante, nunca uma string solta espalhada pelo código).

---

## 📍Passo 1 - Projeto Maven e domínio
Crie um projeto Maven e leve para ele o que você já tinha no V1.

### _O que fazer ⚠️_
1. Crie o projeto com Maven (Java 17 ou superior) e organize os pacotes por responsabilidade.
2. Crie a classe `Pet` e os ENUMs de tipo e sexo.
3. Implemente TODAS as regras da tabela acima, lançando exceções próprias e com mensagem clara.
4. A constante `NÃO INFORMADO` deve existir em um único lugar.
5. Nada de banco ainda. Um `main` simples que cria um pet válido e tenta criar um inválido já basta para ver as regras funcionando.

### _Commit sugerido_
`feat: add pet domain with validation rules`

## 📍Passo 2 - Modelagem SQL
Antes de escrever Java, desenhe o banco.

### _O que fazer ⚠️_
1. Crie o arquivo `schema.sql` com a tabela `pet`.
2. Defina chave primária com auto incremento e `NOT NULL` onde fizer sentido.
3. Use `CHECK` (ou equivalente) para as regras de peso e idade que o banco consegue garantir.
4. Crie um índice para o campo mais pesquisado.
5. Crie o arquivo `seed.sql` com pelo menos 8 pets variados (cachorros e gatos, machos e fêmeas, filhotes e adultos).
6. Crie o arquivo `queries.sql` com as consultas abaixo, todas funcionando contra o seu seed:
   - Listar todos os pets
   - Buscar por PARTE do nome, ignorando maiúsculas e acentos
   - Buscar por raça
   - Buscar por faixa de idade
   - Quantidade de pets por tipo (`GROUP BY`)
   - Peso médio por tipo e sexo

### _Regra nível 2 (não obrigatória) ⚠️_
1. Separe o endereço em uma tabela `endereco` ligada ao pet por chave estrangeira.
2. Reescreva a listagem usando `INNER JOIN` e depois `LEFT JOIN`. Descubra a diferença na prática.

### _Commit sugerido_
`feat: add database schema, seed and queries`

## 📍Passo 3 - Persistência com JDBC
Agora o Java fala com o banco, SEM framework.

### _O que fazer ⚠️_
1. Adicione o driver do MySQL pelo Maven.
2. Crie um repositório de pets com JDBC puro, com estas operações:
   - Salvar
   - Buscar por id
   - Listar todos
   - Buscar por critério (nome parcial, raça ou idade)
   - Alterar
   - Excluir
3. Use SEMPRE `PreparedStatement`. Concatenar valor em SQL é falha de segurança (SQL Injection) e reprova no desafio.
4. Use `try-with-resources` para não vazar conexão.
5. A URL, o usuário e a senha do banco NÃO podem estar escritos no código nem subir pro Git. Leia de variável de ambiente ou de um arquivo de configuração ignorado pelo `.gitignore`.
6. Como no V1, ao alterar um pet o TIPO e o SEXO não podem mudar.

### _Regra nível 2 (não obrigatória) ⚠️_
1. Faça a alteração dentro de uma transação, com `commit` em caso de sucesso e `rollback` em caso de erro.

### _Commit sugerido_
`feat: add jdbc pet repository`

## 📍Passo 4 - O menu do V1 em cima do banco
Aqui está a refatoração que dá nome ao desafio. O terminal do V1 volta, mas agora quem guarda os dados é o banco.

### _O que fazer ⚠️_
1. Recrie o menu inicial do V1 (cadastrar, alterar, deletar, listar todos, listar por critério e sair), com as mesmas regras de entrada: sem zero, sem negativos, sem letras.
2. As perguntas continuam sendo LIDAS do arquivo `formulario.txt`. Não é permitido escrever as perguntas no código.
3. Troque o armazenamento: nada de arquivo `.txt` por pet. Tudo passa pelo repositório do Passo 3.
4. A busca continua devolvendo PARTES do nome, ignorando maiúsculas e acentos.
5. Para alterar e deletar, o usuário busca, escolhe o pet da lista e, no caso do delete, confirma com SIM ou NÃO.
6. Erros de validação aparecem para o usuário com mensagem amigável. O programa não pode quebrar com exceção na tela.

### _Commit sugerido_
`refactor: replace file storage with database in cli`

## 📍Passo 5 - API REST com Spring Boot
O terminal sai de cena. Quem consome o sistema agora é qualquer cliente HTTP.

### _O que fazer ⚠️_
1. Transforme o projeto em uma aplicação Spring Boot 3 (Spring Web e Spring Data JPA).
2. Troque o repositório JDBC por um repositório Spring Data JPA. Compare os dois e entenda o que o Spring gera por baixo.
3. Separe em camadas: controller, service e repository. O controller não conversa com o banco.
4. Use DTOs de entrada e de saída. A entidade não deve ser exposta direto na API.
5. Implemente os endpoints:

| Método | Rota | Resposta de sucesso |
|---|---|---|
| POST | `/pets` | `201 Created` com o pet criado e o header `Location` |
| GET | `/pets` | `200 OK` com a lista. Aceita filtros `nome`, `raca`, `idadeMin` e `idadeMax` |
| GET | `/pets/{id}` | `200 OK` com o pet |
| PUT | `/pets/{id}` | `200 OK` com o pet alterado (tipo e sexo não mudam) |
| DELETE | `/pets/{id}` | `204 No Content` |

6. As regras de negócio viram **Bean Validation** (`@NotBlank`, `@Pattern`, `@DecimalMin` e afins) e, onde não couber em anotação, validação no service.
7. Centralize o tratamento de erros em um `@RestControllerAdvice`, devolvendo SEMPRE o mesmo formato de erro. Use o status certo:
   - `400 Bad Request` para dado inválido
   - `404 Not Found` para pet inexistente
   - `500 Internal Server Error` somente para o que você não previu, sem vazar stack trace

### _Commit sugerido_
`feat: add pet rest api with spring boot`

## 📍Passo 6 - Testes
Sistema sem teste é sistema que ninguém tem coragem de mexer.

### _O que fazer ⚠️_
1. Teste as regras de negócio com JUnit 5: cada linha da tabela de regras precisa de pelo menos um teste que passa e um que falha.
2. Teste o service com Mockito, isolando o repositório.
3. Teste o controller com `@WebMvcTest`, conferindo status e corpo da resposta.
4. Teste o repositório com `@DataJpaTest` usando H2 em memória.
5. Teste de COMPORTAMENTO, não de implementação. Nome do teste diz o que o sistema faz, por exemplo: `rejects pet weighing more than 60kg`.
6. Todo bug que você corrigir daqui pra frente nasce com um teste que o reproduz.

### _Commit sugerido_
`test: cover pet rules, service, controller and repository`

## 📍Passo EXTRA ☠️☠️☠️
Lembra do formulário dinâmico do V1? Agora ele vive no banco.

### _O que fazer ⚠️_
1. Crie as tabelas `pergunta` e `resposta`. Cada pet pode ter várias respostas, e cada resposta pertence a uma pergunta.
2. As perguntas de 1 a 7 são as originais e NÃO podem ser alteradas nem excluídas. Só as que o usuário adicionar.
3. Crie os endpoints para criar, alterar e excluir perguntas, e inclua as respostas extras no `POST /pets` e no `GET /pets/{id}`.
4. Ao excluir uma pergunta, a numeração das seguintes deve ser reorganizada, sem buracos.
5. Pergunta nova não é obrigatória. Se o usuário não responder, salve `NÃO INFORMADO`.

---

# Próximos passos (Parte 3)

Concluiu o Passo 6? Seu V2 já é um projeto de portfólio. A partir daqui você segue a **Parte 3** do guia e aplica cada tema neste mesmo projeto. Faça na ordem que fizer sentido para você, não é obrigatório fazer todos.

| Tema | O que aplicar no V2 |
|---|---|
| Docker | `Dockerfile` da API e `docker-compose` subindo API e banco juntos |
| Swagger | Documentar todos os endpoints com OpenAPI |
| CI/CD | Pipeline no GitHub Actions rodando build e testes a cada push |
| Cloud | Subir a aplicação na AWS e guardar a foto do pet no S3 |
| Segurança | Login com Spring Security e JWT, protegendo criar, alterar e excluir |
| Cache | Cache com Redis na listagem de pets |
| NoSQL | Guardar o histórico de alterações dos pets no MongoDB |

Detalhes e materiais de cada tema: [devmagro.com/roadmaps/virando-junior](https://devmagro.com/roadmaps/virando-junior)

## Considerações Finais

- **Faça commits à medida que concluir cada funcionalidade, seguindo sempre o padrão de [Conventional Commits](https://www.youtube.com/watch?v=sbK9h45Jc5o).**
- **Crie um `README.md` próprio, com a descrição do seu projeto e como executá-lo (incluindo como subir o banco e quais variáveis de ambiente configurar).**
- **Seu projeto precisa compilar e passar nos testes em uma máquina limpa.**

### Gostou do desafio?
Se você chegou até aqui, parabéns! Você saiu de um programa de terminal com arquivos e chegou numa API com banco e testes. Esse é o tipo de projeto que dá o que falar em entrevista. 🚀
