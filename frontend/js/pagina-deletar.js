import { ROTAS } from './config.js'
import { chamarApi, mensagemDeErro } from './api.js'
import { mostrarAviso, esconderAviso } from './avisos.js'
import { montarBusca } from './busca.js'
import { desenharResultados } from './resultados.js'
import { textoOuPadrao } from './formato.js'

const resultados = document.getElementById('resultados')
const dialogo = document.getElementById('confirmacao')
const pergunta = document.getElementById('pergunta')

let ultimaConsulta = null
let petParaExcluir = null


async function buscar(consulta) {
  try {
    const pets = await chamarApi(ROTAS.buscar, { consulta })
    desenharResultados(resultados, pets, { rotuloDaAcao: 'Excluir', aoEscolher: pedirConfirmacao })
  } catch (erro) {
    resultados.replaceChildren()
    mostrarAviso(mensagemDeErro(erro), 'erro')
  }
}

function pedirConfirmacao(pet) {
  petParaExcluir = pet
  pergunta.textContent = `Deseja realmente excluir "${textoOuPadrao(pet.nomeCompleto)}"?`
  dialogo.showModal()
}

montarBusca(document.getElementById('form-busca'), (consulta) => {
  ultimaConsulta = consulta
  esconderAviso()
  buscar(consulta)
})

document.getElementById('botao-nao').addEventListener('click', () => dialogo.close())

document.getElementById('botao-sim').addEventListener('click', async () => {
  dialogo.close()
  try {
    await chamarApi(ROTAS.excluir, { id: petParaExcluir.id })
  } catch (erro) {
    mostrarAviso(mensagemDeErro(erro), 'erro')
    return
  }
  await buscar(ultimaConsulta)
  mostrarAviso('Pet excluído com sucesso!', 'ok')
})
