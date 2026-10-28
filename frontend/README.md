# Front-end do Abrigo de Pets

HTML, CSS e JavaScript puros, sem build. Já vem pronto: o desafio é o backend.

## Rodar

Na **raiz do repositório**, escolha uma opção:

```bash
python3 -m http.server 5500 --directory frontend
# ou
npx serve -l 5500 frontend
```

Abra **http://localhost:5500**. A porta 5500 importa por causa do CORS.

## Páginas

| Arquivo | Opção do menu |
|---|---|
| `index.html` | Menu inicial |
| `cadastrar.html` | 1 - Cadastrar um novo pet |
| `alterar.html` | 2 - Alterar os dados de um pet |
| `deletar.html` | 3 - Deletar um pet |
| `listar.html` | 4 - Listar todos os pets |
| `buscar.html` | 5 - Listar pets por critério |
| `sair.html` | 6 - Sair |

## Código

| Pasta ou arquivo | Para que serve |
|---|---|
| `js/config.js` | Endereço da API, **as rotas do backend** (`ROTAS`) e o menu |
| `js/api.js` | Faz as chamadas HTTP |
| `js/formulario-pet.js` | Formulário com as 7 perguntas do V1 |
| `js/busca.js` | Formulário de busca (tipo e critérios) |
| `js/pagina-*.js` | Uma por página |
| `style.css` | Visual |

A explicação completa (rotas por tela, busca, CORS, o que o front espera da API) está em [docs/05-frontend.md](../docs/05-frontend.md).
