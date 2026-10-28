import { ROTAS } from './config.js'
import { chamarApi, mensagemDeErro } from './api.js'
import { mostrarAviso, esconderAviso } from './avisos.js'
import { mostrarRotas } from './rotas-usadas.js'
import { montarBusca } from './busca.js'
import { desenharResultados } from './resultados.js'

const resultados = document.getElementById('resultados')

mostrarRotas(document.getElementById('rotas'), [ROTAS.buscar])

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
