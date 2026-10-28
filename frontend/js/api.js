import { API_URL } from './config.js'

function lerJson(texto) {
  try {
    return texto ? JSON.parse(texto) : null
  } catch {
    return null
  }
}

export function descreverRota(rota) {
  return `${rota.metodo} ${rota.caminho}`
}

// Chama uma rota de ROTAS. `id` troca o {id} do caminho, `consulta` vira query string
// e `corpo` vira o JSON enviado.
export async function chamarApi(rota, { id, consulta, corpo } = {}) {
  const caminho = rota.caminho.replace('{id}', id ?? '')
  const parametros = consulta ? new URLSearchParams(consulta).toString() : ''
  const url = `${API_URL}${caminho}${parametros ? `?${parametros}` : ''}`

  let resposta
  try {
    resposta = await fetch(url, {
      method: rota.metodo,
      headers: { 'Content-Type': 'application/json' },
      body: corpo ? JSON.stringify(corpo) : undefined,
    })
  } catch (erro) {
    console.error('Falha de rede ou CORS ao chamar a API:', erro)
    throw { offline: true }
  }

  const resultado = lerJson(await resposta.text())
  if (!resposta.ok) throw { status: resposta.status, corpo: resultado }
  return resultado
}

export function mensagemDeErro(erro) {
  if (erro.offline) {
    return `Não foi possível falar com a API em ${API_URL}. Ela está rodando? Se estiver, pode ser CORS: abra o console do navegador (F12) para ver o detalhe.`
  }
  return erro.corpo?.mensagem ?? `A API respondeu com erro ${erro.status}.`
}
