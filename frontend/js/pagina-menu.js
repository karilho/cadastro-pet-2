import { MENU } from './config.js'
import { descreverRota } from './api.js'

const lista = document.getElementById('menu')

for (const opcao of MENU) {
  const item = document.createElement('li')
  const link = document.createElement('a')
  link.href = opcao.pagina

  const titulo = document.createElement('strong')
  titulo.textContent = `${opcao.numero} - ${opcao.titulo}`
  link.append(titulo)

  const rotas = document.createElement('span')
  rotas.className = 'rotas-do-menu'
  rotas.textContent = opcao.rotas.length === 0
    ? 'Não chama nenhuma rota'
    : opcao.rotas.map(descreverRota).join('  e  ')
  link.append(rotas)

  item.append(link)
  lista.append(item)
}
