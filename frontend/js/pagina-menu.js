import { MENU } from './config.js'

const lista = document.getElementById('menu')

for (const opcao of MENU) {
  const item = document.createElement('li')
  const link = document.createElement('a')
  link.href = opcao.pagina
  link.textContent = `${opcao.numero} - ${opcao.titulo}`
  item.append(link)
  lista.append(item)
}
