// Única configuração do front. Se a sua API subir em outra porta, é aqui que você muda.
export const API_URL = 'http://localhost:8080'

// Texto mostrado quando a API devolve null em um campo opcional.
export const NAO_INFORMADO = 'NÃO INFORMADO'

// A API trabalha com o valor em maiúsculas. O front mostra o texto à direita.
export const TIPOS = { CACHORRO: 'Cachorro', GATO: 'Gato' }
export const SEXOS = { MACHO: 'Macho', FEMEA: 'Femea' }

// As rotas que o BACKEND precisa ter. É aqui que cada tela descobre qual rota chamar.
// {id} é trocado pelo id do pet.
export const ROTAS = {
  cadastrar: { metodo: 'POST', caminho: '/pets' },
  listarTodos: { metodo: 'GET', caminho: '/pets' },
  buscar: { metodo: 'GET', caminho: '/pets' },
  alterar: { metodo: 'PUT', caminho: '/pets/{id}' },
  excluir: { metodo: 'DELETE', caminho: '/pets/{id}' },
}

// O menu da tela inicial, na mesma ordem do menu do V1.
export const MENU = [
  { numero: 1, titulo: 'Cadastrar um novo pet', pagina: 'cadastrar.html', rotas: [ROTAS.cadastrar] },
  { numero: 2, titulo: 'Alterar os dados de um pet cadastrado', pagina: 'alterar.html', rotas: [ROTAS.buscar, ROTAS.alterar] },
  { numero: 3, titulo: 'Deletar um pet cadastrado', pagina: 'deletar.html', rotas: [ROTAS.buscar, ROTAS.excluir] },
  { numero: 4, titulo: 'Listar todos os pets cadastrados', pagina: 'listar.html', rotas: [ROTAS.listarTodos] },
  { numero: 5, titulo: 'Listar pets por algum critério (idade, nome, raça)', pagina: 'buscar.html', rotas: [ROTAS.buscar] },
  { numero: 6, titulo: 'Sair', pagina: 'sair.html', rotas: [] },
]
