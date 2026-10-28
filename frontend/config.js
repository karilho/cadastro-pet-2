// Única configuração do front. Se a sua API subir em outra porta, é aqui que você muda.
window.CONFIG = {
  API_URL: 'http://localhost:8080',

  // Texto mostrado quando a API devolve null em um campo opcional.
  NAO_INFORMADO: 'NÃO INFORMADO',

  // A API trabalha com o valor em maiúsculas. O front mostra o texto à direita.
  TIPOS: { CACHORRO: 'Cachorro', GATO: 'Gato' },
  SEXOS: { MACHO: 'Macho', FEMEA: 'Femea' },
}
