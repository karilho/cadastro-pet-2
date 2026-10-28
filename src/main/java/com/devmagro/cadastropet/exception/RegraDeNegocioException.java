package com.devmagro.cadastropet.exception;

/**
 * Violação de regra de negócio que a Bean Validation não cobre. Aponta o campo envolvido.
 */
public class RegraDeNegocioException extends RuntimeException {

    private final String campo;

    public RegraDeNegocioException(String campo, String mensagem) {
        super(mensagem);
        this.campo = campo;
    }

    public String getCampo() {
        return campo;
    }
}
