# 05. Front-end pronto

O desafio é o **backend**. O front já vem pronto na pasta [`frontend/`](../frontend), para você ter uma tela de verdade conversando com a sua API e ver cada passo funcionando.

Você **não precisa** alterar o front. Pode ler o código para entender como ele conversa com a API.

## 1. O que é

- Uma página HTML com CSS e JavaScript puros. Sem framework, sem `npm install`, sem build.
- Lista os pets, busca com filtros, cadastra, altera e exclui.
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

> Rode o comando na raiz do repositório. A porta **5500** importa, por causa do CORS (seção 5).

O que você deve ver com a sua API **desligada**: a página abre, a lista fica vazia e aparece um aviso explicando que não foi possível falar com a API. Isso é esperado. É assim que você sabe que o front está de pé e falta o backend.

## 3. Como o front se integra com a sua API

```
 Navegador                                     Sua API                  Banco
┌──────────────────────┐   HTTP + JSON   ┌──────────────────┐        ┌────────────┐
│ http://localhost:5500│ ──────────────► │ localhost:8080   │ ─────► │ PostgreSQL │
│ (frontend/)          │ ◄────────────── │ (Spring Boot)    │ ◄───── │            │
└──────────────────────┘                 └──────────────────┘        └────────────┘
```

| O que o usuário faz na tela | Chamada que o front faz |
|---|---|
| Abre a página | `GET /pets` |
| Clica em **Buscar** | `GET /pets?nome=...&tipo=...` (só os filtros preenchidos) |
| Clica em **Limpar** | `GET /pets` |
| **Novo pet** e Salvar | `POST /pets` |
| **Alterar** e Salvar | `PUT /pets/{id}` |
| **Excluir** e confirmar | `DELETE /pets/{id}` |

O endereço da API está em [`frontend/config.js`](../frontend/config.js) (`API_URL`, padrão `http://localhost:8080`).

## 4. O que o front espera da sua API

| Situação | O que o front faz |
|---|---|
| `200` na listagem | Mostra os pets. Lista vazia mostra "Nenhum pet encontrado." |
| Campo opcional `null` | Mostra `NÃO INFORMADO` |
| `tipo` e `sexo` em maiúsculas (`GATO`, `FEMEA`) | Converte para `Gato` e `Femea` na tela |
| `201` no `POST`, `200` no `PUT`, `204` no `DELETE` | Mostra aviso de sucesso e recarrega a lista |
| `400` com `campos` | Mostra cada mensagem **embaixo do campo** certo do formulário |
| `404` ou `500` | Mostra a `mensagem` da resposta em um aviso |
| Sem resposta (API desligada) | Mostra aviso de que não conseguiu falar com a API |

### Nomes de campo que ele reconhece no erro `400`

O valor de `campo` na lista de erros precisa ser um destes para a mensagem aparecer no lugar certo:

`nomeCompleto`, `tipo`, `sexo`, `endereco.numeroCasa`, `endereco.cidade`, `endereco.rua`, `idade`, `peso`, `raca`

Campo com outro nome aparece em um aviso geral dentro do formulário.

### O front NÃO valida nada. De propósito

O formulário não tem `required`, nem limite de peso, nem máscara. Tudo que o usuário digita vai para a API, que decide se aceita. Duas razões:

1. **Quem manda é o backend.** Validação no front é só conforto. Qualquer pessoa pode chamar a sua API sem o front (`curl`, Postman). Se a regra só existe na tela, ela não existe.
2. **É assim que você testa a sua validação.** Um peso `61` digitado na tela precisa ser barrado pela API.

As únicas coisas que o front faz com o texto digitado:

- Corta espaços nas pontas.
- Campo vazio vira `null`.
- Troca vírgula por ponto em idade e peso (`3,5` vira `3.5`).
- Se idade ou peso não forem número (`abc`), envia o texto como veio, para a **API** rejeitar.

## 5. CORS: por que o front dá erro mesmo com a API funcionando

O front está em `http://localhost:5500` e a API em `http://localhost:8080`. Para o navegador, **portas diferentes são origens diferentes**. Por segurança, ele só entrega a resposta ao front se a API disser explicitamente que aceita aquela origem. Isso é o **CORS**.

**Sintomas:**

- O Postman e o `curl` funcionam, o front não.
- O aviso do front fala em CORS, e o console do navegador (F12) mostra algo como `blocked by CORS policy` ou `Access-Control-Allow-Origin`.
- O `GET` funciona e o `PUT` ou o `DELETE` falham. Isso é o *preflight*: o navegador pergunta antes (`OPTIONS`) se pode.

**O que a API precisa liberar:** a configuração está no [contrato, seção 4](04-contrato-da-api.md#4-cors-o-front-não-é-a-mesma-origem-da-api). Descobrir **como** fazer isso no Spring Boot faz parte do Passo 4.

> Dica de segurança: libere **só** a origem do front. Liberar `*` (qualquer origem) "resolve" o erro, mas é o tipo de atalho que reprova em code review.

## 6. Ordem sugerida para testar com o front

Vá construindo a API e testando cada pedaço pela tela:

1. `GET /pets`: a lista aparece.
2. `POST /pets` com dado válido: o pet entra na lista.
3. `POST /pets` com dado inválido: os erros aparecem embaixo dos campos.
4. `GET /pets` com filtros: a busca funciona.
5. `PUT /pets/{id}`: alterar um pet (repare que tipo e sexo ficam travados).
6. `DELETE /pets/{id}`: excluir.
7. Desligue a API: o aviso aparece e a tela não quebra.

## 7. Problemas comuns

| Sintoma | Causa provável |
|---|---|
| Aviso "não foi possível falar com a API" | API desligada, porta diferente de `8080` ou CORS. Veja o console (F12) |
| Lista vazia, sem aviso | A API respondeu `200` com `[]`. Confira o banco |
| O pet é salvo mas nada aparece embaixo do campo com erro | O `campo` do erro tem um nome diferente do esperado (seção 4) |
| `PUT` ou `DELETE` falham e `GET` e `POST` funcionam | CORS sem liberar todos os métodos |
| Os dados ficam em branco na edição | A API não devolveu o objeto `endereco` no formato do contrato |
| Página em branco ou sem estilo | O servidor do front não foi iniciado na raiz do repositório |

## 8. Para você pensar

- Por que o front usa `textContent` em vez de `innerHTML` ao mostrar o nome do pet? O que aconteceria se um pet se chamasse `<img src=x onerror=alert(1)>`?
- Por que o `Content-Type: application/json` aparece em toda chamada?
- O que é uma requisição *preflight* e quando o navegador a faz?
