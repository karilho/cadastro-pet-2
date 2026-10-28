import { descreverPet, textoOuPadrao } from './formato.js'

// Desenha a lista numerada, igual ao V1. Se `rotuloDaAcao` for informado,
// cada pet ganha um botão que chama `aoEscolher(pet)`.
export function desenharResultados(container, pets, { rotuloDaAcao, aoEscolher } = {}) {
  if (pets.length === 0) {
    const vazio = document.createElement('p')
    vazio.className = 'vazio'
    vazio.textContent = 'Nenhum pet encontrado.'
    container.replaceChildren(vazio)
    return
  }

  const lista = document.createElement('ol')
  lista.className = 'resultados'

  for (const pet of pets) {
    const item = document.createElement('li')
    const texto = document.createElement('span')
    texto.textContent = descreverPet(pet)
    item.append(texto)

    if (rotuloDaAcao) {
      const botao = document.createElement('button')
      botao.type = 'button'
      botao.className = 'botao'
      botao.textContent = rotuloDaAcao
      botao.setAttribute('aria-label', `${rotuloDaAcao}: ${textoOuPadrao(pet.nomeCompleto)}`)
      botao.addEventListener('click', () => aoEscolher(pet))
      item.append(botao)
    }
    lista.append(item)
  }

  container.replaceChildren(lista)
}
