import { TIPOS, SEXOS } from './config.js'
import { mensagemDeErro } from './api.js'

// As mesmas 7 perguntas do formulário do V1, na mesma ordem.

function criarEntrada({ nome, tipo = 'text', opcoes, atributos = {} }) {
  if (tipo === 'select') {
    const select = document.createElement('select')
    select.name = nome
    select.append(new Option('Selecione', ''))
    for (const [valor, texto] of Object.entries(opcoes)) select.append(new Option(texto, valor))
    return select
  }
  const input = document.createElement('input')
  input.type = 'text'
  input.name = nome
  input.autocomplete = 'off'
  Object.assign(input, atributos)
  return input
}

function criarCampo(campo) {
  const rotulo = document.createElement('label')
  if (campo.largo) rotulo.className = 'largo'
  const texto = document.createElement('span')
  texto.textContent = campo.rotulo
  const erro = document.createElement('small')
  erro.className = 'erro'
  erro.dataset.erroDe = campo.erroDe ?? campo.nome
  rotulo.append(texto, criarEntrada(campo), erro)
  return rotulo
}

function criarGrupo(titulo, campos) {
  const grupo = document.createElement('fieldset')
  grupo.className = 'grupo'
  const legenda = document.createElement('legend')
  legenda.textContent = titulo
  const grade = document.createElement('div')
  grade.className = 'grade'
  grade.append(...campos.map(criarCampo))
  grupo.append(legenda, grade)
  return grupo
}

export function montarFormulario(formulario) {
  const perguntas = [
    criarGrupo('1 - Qual o nome e sobrenome do pet?', [{ nome: 'nomeCompleto', rotulo: 'Nome e sobrenome', largo: true }]),
    criarGrupo('2 - Qual o tipo do pet (Cachorro/Gato)? *', [{ nome: 'tipo', rotulo: 'Tipo', tipo: 'select', opcoes: TIPOS }]),
    criarGrupo('3 - Qual o sexo do animal? *', [{ nome: 'sexo', rotulo: 'Sexo', tipo: 'select', opcoes: SEXOS }]),
    criarGrupo('4 - Qual endereço e bairro que ele foi encontrado? *', [
      { nome: 'numeroCasa', rotulo: 'Número da casa', erroDe: 'endereco.numeroCasa', atributos: { inputMode: 'numeric' } },
      { nome: 'cidade', rotulo: 'Cidade', erroDe: 'endereco.cidade' },
      { nome: 'rua', rotulo: 'Rua', erroDe: 'endereco.rua', largo: true },
    ]),
    criarGrupo('5 - Qual a idade aproximada do pet?', [{ nome: 'idade', rotulo: 'Idade em anos (ex: 3 ou 0,5)', atributos: { inputMode: 'decimal' } }]),
    criarGrupo('6 - Qual o peso aproximado do pet?', [{ nome: 'peso', rotulo: 'Peso em kg (ex: 5 ou 7,5)', atributos: { inputMode: 'decimal' } }]),
    criarGrupo('7 - Qual a raça do pet?', [{ nome: 'raca', rotulo: 'Raça' }]),
  ]

  const erroGeral = document.createElement('p')
  erroGeral.className = 'erro-geral'
  erroGeral.setAttribute('role', 'alert')
  erroGeral.hidden = true

  formulario.replaceChildren(...perguntas, erroGeral)
}

export function travarTipoESexo(formulario, travar) {
  formulario.elements.tipo.disabled = travar
  formulario.elements.sexo.disabled = travar
}

export function preencherFormulario(formulario, pet) {
  const campos = formulario.elements
  campos.nomeCompleto.value = pet.nomeCompleto ?? ''
  campos.tipo.value = pet.tipo
  campos.sexo.value = pet.sexo
  campos.numeroCasa.value = pet.endereco.numeroCasa ?? ''
  campos.cidade.value = pet.endereco.cidade
  campos.rua.value = pet.endereco.rua
  campos.idade.value = pet.idade ?? ''
  campos.peso.value = pet.peso ?? ''
  campos.raca.value = pet.raca ?? ''
}

function textoDoCampo(formulario, nome) {
  const valor = formulario.elements[nome].value.trim()
  return valor === '' ? null : valor
}

function numeroDoCampo(formulario, nome) {
  const texto = textoDoCampo(formulario, nome)
  if (texto === null) return null
  const numero = Number(texto.replace(',', '.'))
  // texto que não é número segue como veio: quem rejeita é a API
  return Number.isNaN(numero) ? texto : numero
}

// `petOriginal` é informado na alteração: tipo e sexo nunca mudam.
export function lerFormulario(formulario, petOriginal) {
  return {
    nomeCompleto: textoDoCampo(formulario, 'nomeCompleto'),
    tipo: petOriginal ? petOriginal.tipo : textoDoCampo(formulario, 'tipo'),
    sexo: petOriginal ? petOriginal.sexo : textoDoCampo(formulario, 'sexo'),
    endereco: {
      numeroCasa: textoDoCampo(formulario, 'numeroCasa'),
      cidade: textoDoCampo(formulario, 'cidade'),
      rua: textoDoCampo(formulario, 'rua'),
    },
    idade: numeroDoCampo(formulario, 'idade'),
    peso: numeroDoCampo(formulario, 'peso'),
    raca: textoDoCampo(formulario, 'raca'),
  }
}

export function limparErros(formulario) {
  formulario.querySelector('.erro-geral').hidden = true
  formulario.querySelectorAll('[data-erro-de]').forEach((marca) => {
    marca.textContent = ''
  })
}

export function mostrarErros(formulario, erro) {
  const campos = erro.corpo?.campos ?? []
  const semLugar = []

  for (const { campo, mensagem } of campos) {
    const marca = formulario.querySelector(`[data-erro-de="${campo}"]`)
    if (marca) marca.textContent = mensagem
    else semLugar.push(`${campo}: ${mensagem}`)
  }

  const geral = campos.length === 0 || semLugar.length > 0
    ? [mensagemDeErro(erro), ...semLugar].join(' ')
    : ''
  const destino = formulario.querySelector('.erro-geral')
  destino.textContent = geral
  destino.hidden = geral === ''
}
