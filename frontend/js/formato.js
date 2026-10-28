import { NAO_INFORMADO, TIPOS, SEXOS } from './config.js'

export function textoOuPadrao(valor) {
  return valor ?? NAO_INFORMADO
}

function numeroSemZeros(numero) {
  return String(Number(numero))
}

// Mesmo formato da lista do V1:
// Rex - Cachorro - Macho - Rua 1, 123 - Cidade 1 - 2 anos - 5kg - Vira-lata
export function descreverPet(pet) {
  const { endereco } = pet
  return [
    textoOuPadrao(pet.nomeCompleto),
    TIPOS[pet.tipo] ?? pet.tipo,
    SEXOS[pet.sexo] ?? pet.sexo,
    `${endereco.rua}, ${textoOuPadrao(endereco.numeroCasa)}`,
    endereco.cidade,
    pet.idade === null ? NAO_INFORMADO : `${numeroSemZeros(pet.idade)} anos`,
    pet.peso === null ? NAO_INFORMADO : `${numeroSemZeros(pet.peso)}kg`,
    textoOuPadrao(pet.raca),
  ].join(' - ')
}
