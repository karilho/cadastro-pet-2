import { ROTAS } from './config.js'
import { chamarApi, mensagemDeErro } from './api.js'
import { mostrarAviso, esconderAviso } from './avisos.js'
import { mostrarRotas } from './rotas-usadas.js'
import { desenharResultados } from './resultados.js'

const resultados = document.getElementById('resultados')

mostrarRotas(document.getElementById('rotas'), [ROTAS.listarTodos])

try {
  const pets = await chamarApi(ROTAS.listarTodos)
  esconderAviso()
  desenharResultados(resultados, pets)
} catch (erro) {
  mostrarAviso(mensagemDeErro(erro), 'erro')
}
