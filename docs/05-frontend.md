# 05. Front-end pronto

O desafio é o **backend**. O front já vem pronto na pasta [`frontend/`](../frontend), com o **mesmo menu do V1**, para você ver cada pedaço da sua API funcionando numa tela de verdade.

Você **não precisa** alterar o front. Pode ler o código para entender como ele conversa com a API.

## 1. O que é

- Um site de várias páginas, em HTML, CSS e JavaScript puros. Sem framework, sem `npm install`, sem build.
- O menu inicial tem as mesmas 6 opções do V1. Cada opção abre uma página.
- Ele **não guarda nenhum dado**. Tudo que aparece vem da sua API, e se ela não responder a tela mostra um erro.
- Fala com a sua API pelo [contrato](04-contrato-da-api.md). Se a sua API seguir o contrato, o front funciona.

## 2. Como rodar

O front precisa ser servido por um servidor web simples, **na porta 5500**. Escolha uma opção:

```bash
# Python 3 (já vem no macOS e no Linux)
python3 -m http.server 5500 --directory frontend

# ou Node.js
npx serve -l 5500 frontend
```

Ou, no VS Code, a extensão **Live Server** (a porta padrão dela já é 5500).

Depois abra no navegador: **http://localhost:5500**

> Rode o comando na raiz do repositório. A porta **5500** importa, por causa do CORS (seção 7).

O que você deve ver com a sua API **desligada**: o menu abre normalmente. Ao entrar numa tela que precisa da API, aparece um aviso explicando que não foi possível falar com ela. Isso é esperado, e é assim que você sabe que o front está de pé e falta o backend.

## 3. As telas e as rotas

```
 Navegador                                     Sua API                  Banco
┌──────────────────────┐   HTTP + JSON   ┌──────────────────┐        ┌────────────┐
│ http://localhost:5500│ ──────────────► │ localhost:8080   │ ─────► │ PostgreSQL │
│ (frontend/)          │ ◄────────────── │ (Spring Boot)    │ ◄───── │            │
└──────────────────────┘                 └──────────────────┘        └────────────┘
```

| Menu | Página | O que o usuário faz | Rota chamada |
|---|---|---|---|
| 1 | `cadastrar.html` | Preenche as 7 perguntas e clica em **Salvar** | `POST /pets` |
| 2 | `alterar.html` | Busca, clica em **Alterar** no pet, edita e salva | `GET /pets?...` e depois `PUT /pets/{id}` |
| 3 | `deletar.html` | Busca, clica em **Excluir**, confirma **SIM** ou **NÃO** | `GET /pets?...` e depois `DELETE /pets/{id}` |
| 4 | `listar.html` | Só abre a página | `GET /pets` |
| 5 | `buscar.html` | Escolhe o tipo e 1 ou 2 critérios, e clica em **Buscar** | `GET /pets?...` |
| 6 | `sair.html` | Só vê a despedida | (nenhuma) |

As rotas estão todas em [`frontend/js/config.js`](../frontend/js/config.js), no objeto `ROTAS`. Se um dia você quiser renomear uma rota, é lá que muda (e no [contrato](04-contrato-da-api.md)).

## 4. Como funciona a busca (menu 2, 3 e 5)

A busca é a mesma do V1, nas três telas:

1. O usuário escolhe **primeiro o tipo do animal** (Cachorro ou Gato). É obrigatório.
2. Depois escolhe **1 ou 2 critérios**, diferentes entre si, e informa o valor de cada um.
3. A tela monta a chamada e mostra o resultado numerado.

Como cada critério vira parâmetro na sua API:

| Critério na tela | Parâmetros enviados |
|---|---|
| Tipo do animal | `tipo=CACHORRO` ou `tipo=GATO` |
| Nome ou sobrenome | `nome=flor` |
| Sexo | `sexo=MACHO` ou `sexo=FEMEA` |
| Idade (anos) | `idadeMin=3&idadeMax=3` (o mesmo valor nos dois) |
| Peso (kg) | `pesoMin=5&pesoMax=5` (o mesmo valor nos dois) |
| Raça | `raca=siames` |
| Endereço (cidade ou rua) | `endereco=colombo` |

Exemplo de chamada completa: `GET /pets?tipo=GATO&sexo=FEMEA&raca=siames`

## 5. O que o front espera da sua API

| Situação | O que o front faz |
|---|---|
| `200` na listagem | Mostra os pets numerados. Lista vazia mostra "Nenhum pet encontrado." |
| Campo opcional `null` | Mostra `NÃO INFORMADO` |
| `tipo` e `sexo` em maiúsculas (`GATO`, `FEMEA`) | Converte para `Gato` e `Femea` na tela |
| `201` no `POST`, `200` no `PUT`, `204` no `DELETE` | Mostra aviso de sucesso |
| `400` com `campos` | Mostra cada mensagem **embaixo do campo** certo do formulário |
| `404` ou `500` | Mostra a `mensagem` da resposta em um aviso |
| Sem resposta (API desligada) | Mostra aviso de que não conseguiu falar com a API |

### Formato de cada pet na lista

Igual ao V1: `nome - tipo - sexo - rua, número - cidade - idade - peso - raça`

```
1. Rex Almeida - Cachorro - Macho - Rua das Flores, 123 - Curitiba - 2 anos - 5kg - Vira-lata
```

### Nomes de campo que ele reconhece no erro `400`

O valor de `campo` na lista de erros precisa ser um destes para a mensagem aparecer no lugar certo:

`nomeCompleto`, `tipo`, `sexo`, `endereco.numeroCasa`, `endereco.cidade`, `endereco.rua`, `idade`, `peso`, `raca`

Campo com outro nome aparece em um aviso geral dentro do formulário.

## 6. O front NÃO valida o cadastro. De propósito

O formulário não tem `required`, nem limite de peso, nem máscara. Tudo que o usuário digita vai para a API, que decide se aceita. Duas razões:

1. **Quem manda é o backend.** Validação no front é só conforto. Qualquer pessoa pode chamar a sua API sem o front (`curl`, Postman). Se a regra só existe na tela, ela não existe.
2. **É assim que você testa a sua validação.** Um peso `61` digitado na tela precisa ser barrado pela API.

As únicas coisas que o front faz com o texto digitado:

- Corta espaços nas pontas.
- Campo vazio vira `null`.
- Troca vírgula por ponto em idade e peso (`3,5` vira `3.5`).
- Se idade ou peso não forem número (`abc`), envia o texto como veio, para a **API** rejeitar.

A única validação que existe na tela é a do **formulário de busca** (escolher o tipo, escolher o critério, os dois critérios diferentes), porque ela é só navegação e não regra de negócio.

## 7. CORS: por que o front dá erro mesmo com a API funcionando

O front está em `http://localhost:5500` e a API em `http://localhost:8080`. Para o navegador, **portas diferentes são origens diferentes**. Por segurança, ele só entrega a resposta ao front se a API disser explicitamente que aceita aquela origem. Isso é o **CORS**.

**Sintomas:**

- O Postman e o `curl` funcionam, o front não.
- O aviso do front fala em CORS, e o console do navegador (F12) mostra algo como `blocked by CORS policy` ou `Access-Control-Allow-Origin`.
- O `GET` funciona e o `PUT` ou o `DELETE` falham. Isso é o *preflight*: o navegador pergunta antes (`OPTIONS`) se pode.

**O que a API precisa liberar:** a configuração está no [contrato, seção 4](04-contrato-da-api.md#4-cors-o-front-não-é-a-mesma-origem-da-api). Descobrir **como** fazer isso no Spring Boot faz parte do Passo 4.

> Dica de segurança: libere **só** a origem do front. Liberar `*` (qualquer origem) "resolve" o erro, mas é o tipo de atalho que reprova em code review.

## 8. Ordem sugerida para testar com o front

Vá construindo a API e testando cada pedaço pela tela:

1. `GET /pets` sem filtro: abra **4 - Listar todos**.
2. `POST /pets` com dado válido: **1 - Cadastrar**, depois confira na listagem.
3. `POST /pets` com dado inválido: os erros aparecem embaixo dos campos.
4. `GET /pets` com filtros: **5 - Listar por critério**.
5. `PUT /pets/{id}`: **2 - Alterar** (repare que tipo e sexo ficam travados).
6. `DELETE /pets/{id}`: **3 - Deletar**.
7. Desligue a API e abra qualquer tela: o aviso aparece e a página não quebra.

## 9. Problemas comuns

| Sintoma | Causa provável |
|---|---|
| Aviso "não foi possível falar com a API" | API desligada, porta diferente de `8080` ou CORS. Veja o console (F12) |
| Lista vazia, sem aviso | A API respondeu `200` com `[]`. Confira o banco |
| O pet é salvo mas nada aparece embaixo do campo com erro | O `campo` do erro tem um nome diferente do esperado (seção 5) |
| `PUT` ou `DELETE` falham e `GET` e `POST` funcionam | CORS sem liberar todos os métodos |
| A busca por "siames" não acha "Siamês" | A sua API não ignora acento. Veja o [Passo 2](passos/passo-2-modelagem-sql.md) |
| Os dados ficam em branco na edição | A API não devolveu o objeto `endereco` no formato do contrato |
| Página em branco ou sem estilo | O servidor do front não foi iniciado na raiz do repositório |

## 10. Para você pensar

- Por que o front usa `textContent` em vez de `innerHTML` ao mostrar o nome do pet? O que aconteceria se um pet se chamasse `<img src=x onerror=alert(1)>`?
- Por que a busca por idade manda `idadeMin` e `idadeMax` com o mesmo valor, em vez de um parâmetro `idade`?
- O que é uma requisição *preflight* e quando o navegador a faz?
- A exclusão pede confirmação na tela. Por que a API não deveria pedir?
