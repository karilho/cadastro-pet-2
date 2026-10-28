const { API_URL, NAO_INFORMADO, TIPOS, SEXOS } = window.CONFIG

const elementos = {
  aviso: document.getElementById('aviso'),
  lista: document.getElementById('lista'),
  vazio: document.getElementById('vazio'),
  contagem: document.getElementById('contagem'),
  formFiltros: document.getElementById('form-filtros'),
  botaoLimpar: document.getElementById('botao-limpar'),
  botaoNovo: document.getElementById('botao-novo'),
  dialogo: document.getElementById('dialogo'),
  tituloDialogo: document.getElementById('titulo-dialogo'),
  formPet: document.getElementById('form-pet'),
  botaoCancelar: document.getElementById('botao-cancelar'),
  erroGeral: document.getElementById('erro-geral'),
}

let petEmEdicao = null

// ---------- comunicação com a API ----------

function lerJson(texto) {
  try {
    return texto ? JSON.parse(texto) : null
  } catch {
    return null
  }
}

async function chamarApi(caminho, opcoes = {}) {
  let resposta
  try {
    resposta = await fetch(`${API_URL}${caminho}`, {
      headers: { 'Content-Type': 'application/json' },
      ...opcoes,
    })
  } catch (erro) {
    console.error('Falha de rede ou CORS ao chamar a API:', erro)
    throw { offline: true }
  }

  const texto = await resposta.text()
  const corpo = lerJson(texto)
  if (!resposta.ok) throw { status: resposta.status, corpo }
  return corpo
}

function mensagemDeErro(erro) {
  if (erro.offline) {
    return `Não foi possível falar com a API em ${API_URL}. Ela está rodando? Se estiver, pode ser CORS: abra o console do navegador (F12) para ver o detalhe.`
  }
  return erro.corpo?.mensagem ?? `A API respondeu com erro ${erro.status}.`
}

// ---------- avisos ----------

function mostrarAviso(mensagem, tipo) {
  elementos.aviso.textContent = mensagem
  elementos.aviso.className = `aviso aviso-${tipo}`
  elementos.aviso.hidden = false
}

function esconderAviso() {
  elementos.aviso.hidden = true
}

// ---------- formatação ----------

function semZerosAExcesso(numero) {
  return numero === null || numero === undefined ? NAO_INFORMADO : String(Number(numero))
}

function textoOuPadrao(valor) {
  return valor ?? NAO_INFORMADO
}

function descreverPet(pet) {
  const endereco = pet.endereco
  const partes = [
    TIPOS[pet.tipo] ?? pet.tipo,
    SEXOS[pet.sexo] ?? pet.sexo,
    `${endereco.rua}, ${textoOuPadrao(endereco.numeroCasa)}`,
    endereco.cidade,
    pet.idade === null ? NAO_INFORMADO : `${semZerosAExcesso(pet.idade)} anos`,
    pet.peso === null ? NAO_INFORMADO : `${semZerosAExcesso(pet.peso)}kg`,
    textoOuPadrao(pet.raca),
  ]
  return partes.join(' - ')
}

// ---------- lista ----------

function criarItem(pet) {
  const item = document.createElement('li')
  item.className = 'pet'

  const texto = document.createElement('div')
  const nome = document.createElement('strong')
  nome.textContent = textoOuPadrao(pet.nomeCompleto)
  const detalhes = document.createElement('p')
  detalhes.textContent = descreverPet(pet)
  texto.append(nome, detalhes)

  const acoes = document.createElement('div')
  acoes.className = 'acoes'
  const alterar = document.createElement('button')
  alterar.type = 'button'
  alterar.className = 'botao'
  alterar.textContent = 'Alterar'
  alterar.addEventListener('click', () => abrirEdicao(pet))
  const excluir = document.createElement('button')
  excluir.type = 'button'
  excluir.className = 'botao botao-perigo'
  excluir.textContent = 'Excluir'
  excluir.addEventListener('click', () => excluirPet(pet))
  acoes.append(alterar, excluir)

  item.append(texto, acoes)
  return item
}

function desenharLista(pets) {
  elementos.lista.replaceChildren(...pets.map(criarItem))
  elementos.vazio.hidden = pets.length > 0
  elementos.contagem.textContent = `(${pets.length})`
}

function lerFiltros() {
  const parametros = new URLSearchParams()
  for (const [campo, valor] of new FormData(elementos.formFiltros)) {
    const limpo = String(valor).trim().replace(',', '.')
    if (limpo !== '') parametros.set(campo, limpo)
  }
  return parametros.toString()
}

async function carregarPets() {
  const consulta = lerFiltros()
  try {
    const pets = await chamarApi(`/pets${consulta ? `?${consulta}` : ''}`)
    esconderAviso()
    desenharLista(pets)
  } catch (erro) {
    elementos.lista.replaceChildren()
    elementos.vazio.hidden = true
    elementos.contagem.textContent = ''
    mostrarAviso(mensagemDeErro(erro), 'erro')
  }
}

// ---------- formulário ----------

function limparErrosDoFormulario() {
  elementos.erroGeral.hidden = true
  elementos.formPet.querySelectorAll('[data-erro-de]').forEach((marca) => {
    marca.textContent = ''
  })
}

function textoDoCampo(nome) {
  const valor = elementos.formPet.elements[nome].value.trim()
  return valor === '' ? null : valor
}

function numeroDoCampo(nome) {
  const texto = textoDoCampo(nome)
  if (texto === null) return null
  const numero = Number(texto.replace(',', '.'))
  // texto que não é número segue como veio: quem rejeita é a API
  return Number.isNaN(numero) ? texto : numero
}

function lerFormulario() {
  const campos = elementos.formPet.elements
  return {
    nomeCompleto: textoDoCampo('nomeCompleto'),
    tipo: petEmEdicao ? petEmEdicao.tipo : campos.tipo.value,
    sexo: petEmEdicao ? petEmEdicao.sexo : campos.sexo.value,
    endereco: {
      numeroCasa: textoDoCampo('numeroCasa'),
      cidade: textoDoCampo('cidade'),
      rua: textoDoCampo('rua'),
    },
    idade: numeroDoCampo('idade'),
    peso: numeroDoCampo('peso'),
    raca: textoDoCampo('raca'),
  }
}

function preencherFormulario(pet) {
  const campos = elementos.formPet.elements
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

function abrirCadastro() {
  petEmEdicao = null
  elementos.formPet.reset()
  limparErrosDoFormulario()
  elementos.formPet.elements.tipo.disabled = false
  elementos.formPet.elements.sexo.disabled = false
  elementos.tituloDialogo.textContent = 'Novo pet'
  elementos.dialogo.showModal()
}

function abrirEdicao(pet) {
  petEmEdicao = pet
  elementos.formPet.reset()
  limparErrosDoFormulario()
  preencherFormulario(pet)
  // tipo e sexo não podem ser alterados (regra de negócio)
  elementos.formPet.elements.tipo.disabled = true
  elementos.formPet.elements.sexo.disabled = true
  elementos.tituloDialogo.textContent = 'Alterar pet'
  elementos.dialogo.showModal()
}

function mostrarErrosDoFormulario(erro) {
  const campos = erro.corpo?.campos ?? []
  const semLugar = []

  for (const { campo, mensagem } of campos) {
    const marca = elementos.formPet.querySelector(`[data-erro-de="${campo}"]`)
    if (marca) marca.textContent = mensagem
    else semLugar.push(`${campo}: ${mensagem}`)
  }

  const geral = campos.length === 0 || semLugar.length > 0
    ? [mensagemDeErro(erro), ...semLugar].join(' ')
    : ''
  elementos.erroGeral.textContent = geral
  elementos.erroGeral.hidden = geral === ''
}

async function salvarPet(evento) {
  evento.preventDefault()
  limparErrosDoFormulario()

  const corpo = JSON.stringify(lerFormulario())
  const opcoes = petEmEdicao
    ? { method: 'PUT', body: corpo }
    : { method: 'POST', body: corpo }
  const caminho = petEmEdicao ? `/pets/${petEmEdicao.id}` : '/pets'

  try {
    await chamarApi(caminho, opcoes)
  } catch (erro) {
    mostrarErrosDoFormulario(erro)
    return
  }

  const acao = petEmEdicao ? 'alterado' : 'cadastrado'
  elementos.dialogo.close()
  await carregarPets()
  mostrarAviso(`Pet ${acao} com sucesso!`, 'ok')
}

async function excluirPet(pet) {
  const nome = textoOuPadrao(pet.nomeCompleto)
  if (!window.confirm(`Deseja realmente excluir "${nome}"?`)) return

  try {
    await chamarApi(`/pets/${pet.id}`, { method: 'DELETE' })
  } catch (erro) {
    mostrarAviso(mensagemDeErro(erro), 'erro')
    return
  }

  await carregarPets()
  mostrarAviso('Pet excluído com sucesso!', 'ok')
}

// ---------- inicialização ----------

function preencherOpcoes(nomeDoCampo, opcoes) {
  document.querySelectorAll(`select[name="${nomeDoCampo}"]`).forEach((select) => {
    for (const [valor, texto] of Object.entries(opcoes)) {
      select.append(new Option(texto, valor))
    }
  })
}

preencherOpcoes('tipo', TIPOS)
preencherOpcoes('sexo', SEXOS)

elementos.formFiltros.addEventListener('submit', (evento) => {
  evento.preventDefault()
  carregarPets()
})
elementos.botaoLimpar.addEventListener('click', () => {
  elementos.formFiltros.reset()
  carregarPets()
})
elementos.botaoNovo.addEventListener('click', abrirCadastro)
elementos.botaoCancelar.addEventListener('click', () => elementos.dialogo.close())
elementos.formPet.addEventListener('submit', salvarPet)

carregarPets()
