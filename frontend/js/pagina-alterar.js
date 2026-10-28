import { ROTAS } from './config.js'
import { chamarApi, mensagemDeErro } from './api.js'
import { mostrarAviso, esconderAviso } from './avisos.js'
import { montarBusca } from './busca.js'
import { desenharResultados } from './resultados.js'
import {
  montarFormulario, travarTipoESexo, preencherFormulario,
  lerFormulario, limparErros, mostrarErros,
} from './formulario-pet.js'

const resultados = document.getElementById('resultados')
const edicao = document.getElementById('edicao')
const formulario = document.getElementById('form-pet')

let ultimaConsulta = null
let petEmEdicao = null

montarFormulario(formulario)
travarTipoESexo(formulario, true)

async function buscar(consulta) {
  try {
    const pets = await chamarApi(ROTAS.buscar, { consulta })
    desenharResultados(resultados, pets, { rotuloDaAcao: 'Alterar', aoEscolher: abrirEdicao })
  } catch (erro) {
    resultados.replaceChildren()
    mostrarAviso(mensagemDeErro(erro), 'erro')
  }
}

function abrirEdicao(pet) {
  petEmEdicao = pet
  esconderAviso()
  limparErros(formulario)
  preencherFormulario(formulario, pet)
  edicao.hidden = false
  edicao.scrollIntoView({ behavior: 'smooth' })
}

montarBusca(document.getElementById('form-busca'), (consulta) => {
  ultimaConsulta = consulta
  edicao.hidden = true
  esconderAviso()
  buscar(consulta)
})

formulario.addEventListener('submit', async (evento) => {
  evento.preventDefault()
  limparErros(formulario)

  try {
    await chamarApi(ROTAS.alterar, { id: petEmEdicao.id, corpo: lerFormulario(formulario, petEmEdicao) })
  } catch (erro) {
    mostrarErros(formulario, erro)
    return
  }

  edicao.hidden = true
  await buscar(ultimaConsulta)
  mostrarAviso('Pet alterado com sucesso!', 'ok')
  window.scrollTo({ top: 0, behavior: 'smooth' })
})

document.getElementById('botao-cancelar').addEventListener('click', () => {
  edicao.hidden = true
})
