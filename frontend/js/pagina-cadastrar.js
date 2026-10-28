import { ROTAS } from './config.js'
import { chamarApi } from './api.js'
import { mostrarAviso, esconderAviso } from './avisos.js'
import { montarFormulario, lerFormulario, limparErros, mostrarErros } from './formulario-pet.js'

const formulario = document.getElementById('form-pet')

montarFormulario(formulario)

formulario.addEventListener('submit', async (evento) => {
  evento.preventDefault()
  limparErros(formulario)
  esconderAviso()

  try {
    await chamarApi(ROTAS.cadastrar, { corpo: lerFormulario(formulario) })
  } catch (erro) {
    mostrarErros(formulario, erro)
    return
  }

  formulario.reset()
  mostrarAviso('Pet cadastrado com sucesso!', 'ok')
  window.scrollTo({ top: 0, behavior: 'smooth' })
})
