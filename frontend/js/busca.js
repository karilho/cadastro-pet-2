import { TIPOS, SEXOS } from './config.js'

// Mesmos critérios do V1: o tipo do animal vem SEMPRE primeiro,
// depois 1 ou 2 critérios entre os abaixo (sempre combinados com E).
const CRITERIOS = {
  nome: { rotulo: 'Nome ou sobrenome', parametros: (v) => ({ nome: v }) },
  sexo: { rotulo: 'Sexo', opcoes: SEXOS, parametros: (v) => ({ sexo: v }) },
  idade: { rotulo: 'Idade (anos)', numerico: true, parametros: (v) => ({ idadeMin: v, idadeMax: v }) },
  peso: { rotulo: 'Peso (kg)', numerico: true, parametros: (v) => ({ pesoMin: v, pesoMax: v }) },
  raca: { rotulo: 'Raça', parametros: (v) => ({ raca: v }) },
  endereco: { rotulo: 'Endereço (cidade ou rua)', parametros: (v) => ({ endereco: v }) },
}

function criarEntradaDoValor(chave) {
  const criterio = CRITERIOS[chave]
  if (criterio?.opcoes) {
    const select = document.createElement('select')
    select.name = 'valor'
    select.append(new Option('Selecione', ''))
    for (const [valor, texto] of Object.entries(criterio.opcoes)) select.append(new Option(texto, valor))
    return select
  }
  const input = document.createElement('input')
  input.type = 'text'
  input.name = 'valor'
  input.autocomplete = 'off'
  return input
}

function criarBlocoDeCriterio(posicao, opcional) {
  const bloco = document.createElement('div')
  bloco.className = 'criterio grade'
  bloco.dataset.posicao = posicao

  const rotuloSelect = document.createElement('label')
  const textoSelect = document.createElement('span')
  textoSelect.textContent = `Critério ${posicao}${opcional ? ' (opcional)' : ''}`
  const select = document.createElement('select')
  select.name = 'criterio'
  select.append(new Option(opcional ? 'Nenhum' : 'Selecione', ''))
  for (const [chave, { rotulo }] of Object.entries(CRITERIOS)) select.append(new Option(rotulo, chave))
  rotuloSelect.append(textoSelect, select)

  const rotuloValor = document.createElement('label')
  const textoValor = document.createElement('span')
  textoValor.textContent = 'Valor'
  rotuloValor.append(textoValor, criarEntradaDoValor(''))

  select.addEventListener('change', () => {
    rotuloValor.querySelector('[name="valor"]').replaceWith(criarEntradaDoValor(select.value))
  })

  bloco.append(rotuloSelect, rotuloValor)
  return bloco
}

function criarGrupoDoTipo() {
  const grupo = document.createElement('fieldset')
  grupo.className = 'grupo'
  const legenda = document.createElement('legend')
  legenda.textContent = '1 - Qual o tipo de animal? *'
  const opcoes = document.createElement('div')
  opcoes.className = 'opcoes'
  for (const [valor, texto] of Object.entries(TIPOS)) {
    const rotulo = document.createElement('label')
    rotulo.className = 'opcao'
    const radio = document.createElement('input')
    radio.type = 'radio'
    radio.name = 'tipo'
    radio.value = valor
    rotulo.append(radio, document.createTextNode(` ${texto}`))
    opcoes.append(rotulo)
  }
  grupo.append(legenda, opcoes)
  return grupo
}

function lerCriterios(formulario) {
  return [...formulario.querySelectorAll('.criterio')].map((bloco) => ({
    chave: bloco.querySelector('[name="criterio"]').value,
    valor: bloco.querySelector('[name="valor"]').value.trim(),
  }))
}

// Devolve a mensagem de erro de preenchimento, ou null se está tudo certo.
function validarBusca(tipo, criterios) {
  if (!tipo) return 'Escolha o tipo do animal.'
  const [primeiro, segundo] = criterios
  if (!primeiro.chave) return 'Escolha pelo menos um critério de busca.'
  if (primeiro.valor === '') return `Informe o valor do critério "${CRITERIOS[primeiro.chave].rotulo}".`
  if (!segundo.chave) return null
  if (segundo.chave === primeiro.chave) return 'Os dois critérios precisam ser diferentes.'
  if (segundo.valor === '') return `Informe o valor do critério "${CRITERIOS[segundo.chave].rotulo}".`
  return null
}

function montarConsulta(tipo, criterios) {
  const consulta = { tipo }
  for (const { chave, valor } of criterios) {
    if (!chave) continue
    const limpo = CRITERIOS[chave].numerico ? valor.replace(',', '.') : valor
    Object.assign(consulta, CRITERIOS[chave].parametros(limpo))
  }
  return consulta
}

// Desenha o formulário de busca dentro de `formulario`. Quando o usuário envia
// um preenchimento válido, chama `aoBuscar(consulta)`.
export function montarBusca(formulario, aoBuscar) {
  const erro = document.createElement('p')
  erro.className = 'erro-geral'
  erro.setAttribute('role', 'alert')
  erro.hidden = true

  const acoes = document.createElement('div')
  acoes.className = 'acoes'
  const buscar = document.createElement('button')
  buscar.type = 'submit'
  buscar.className = 'botao botao-principal'
  buscar.textContent = 'Buscar'
  acoes.append(buscar)

  const criterios = document.createElement('fieldset')
  criterios.className = 'grupo'
  const legenda = document.createElement('legend')
  legenda.textContent = '2 - Por qual critério buscar? (escolha 1 ou 2)'
  criterios.append(legenda, criarBlocoDeCriterio(1, false), criarBlocoDeCriterio(2, true))

  formulario.replaceChildren(criarGrupoDoTipo(), criterios, erro, acoes)

  formulario.addEventListener('submit', (evento) => {
    evento.preventDefault()
    const tipo = formulario.querySelector('[name="tipo"]:checked')?.value
    const lidos = lerCriterios(formulario)
    const mensagem = validarBusca(tipo, lidos)
    erro.textContent = mensagem ?? ''
    erro.hidden = mensagem === null
    if (mensagem === null) aoBuscar(montarConsulta(tipo, lidos))
  })
}
