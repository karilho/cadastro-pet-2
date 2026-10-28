import { descreverRota } from './api.js'

// Mostra no topo de cada tela quais rotas o BACKEND precisa ter para ela funcionar.
export function mostrarRotas(container, rotas, titulo = 'Rotas do backend usadas nesta tela') {
  const rotulo = document.createElement('p')
  rotulo.textContent = `${titulo}:`
  const lista = document.createElement('ul')
  for (const rota of rotas) {
    const item = document.createElement('li')
    const codigo = document.createElement('code')
    codigo.textContent = descreverRota(rota)
    item.append(codigo)
    lista.append(item)
  }
  container.replaceChildren(rotulo, lista)
}
