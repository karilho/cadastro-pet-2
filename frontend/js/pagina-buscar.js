import { ROTAS } from './config.js'
import { chamarApi, mensagemDeErro } from './api.js'
import { mostrarAviso, esconderAviso } from './avisos.js'
import { montarBusca } from './busca.js'
import { desenharResultados } from './resultados.js'

const resultados = document.getElementById('resultados')


montarBusca(document.getElementById('form-busca'), async (consulta) => {
  try {
    const pets = await chamarApi(ROTAS.buscar, { consulta })
    esconderAviso()
    desenharResultados(resultados, pets)
  } catch (erro) {
    resultados.replaceChildren()
    mostrarAviso(mensagemDeErro(erro), 'erro')
  }
})
