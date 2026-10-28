package com.devmagro.cadastropet.validation;

/**
 * Mensagens de erro padrão do desafio. Ficam em um lugar só.
 */
public final class Mensagens {

    public static final String NOME_SEM_SOBRENOME = "Informe nome e sobrenome do pet.";
    public static final String NOME_INVALIDO = "O nome deve conter apenas letras.";
    public static final String TIPO_INVALIDO = "Tipo inválido. Use Cachorro ou Gato.";
    public static final String SEXO_INVALIDO = "Sexo inválido. Use Macho ou Femea.";
    public static final String NUMERO_CASA_INVALIDO = "O número da casa deve conter apenas dígitos.";
    public static final String CIDADE_OBRIGATORIA = "Informe a cidade.";
    public static final String RUA_OBRIGATORIA = "Informe a rua.";
    public static final String ENDERECO_OBRIGATORIO = "Informe a cidade e a rua.";
    public static final String IDADE_INVALIDA = "A idade deve ser um número maior que 0 e no máximo 20 anos.";
    public static final String PESO_INVALIDO = "O peso deve ser um número entre 0,5kg e 60kg.";
    public static final String RACA_INVALIDA = "A raça deve conter apenas letras, espaços e hífen.";
    public static final String PET_NAO_ENCONTRADO = "Pet não encontrado.";
    public static final String TIPO_E_SEXO_IMUTAVEIS = "Tipo e sexo não podem ser alterados.";

    private Mensagens() {
    }
}
