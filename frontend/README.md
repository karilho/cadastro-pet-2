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

## Arquivos

| Arquivo | Para que serve |
|---|---|
| `index.html` | Estrutura da página, filtros e formulário |
| `app.js` | Chamadas à API e desenho da tela |
| `config.js` | Endereço da API e textos de exibição |
| `style.css` | Visual |

A explicação completa (integração, CORS, o que o front espera da API) está em [docs/05-frontend.md](../docs/05-frontend.md).
