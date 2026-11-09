package com.devmagro.cadastropet.exception;

/**
 * Erro técnico de acesso ao banco. Não é culpa do usuário: vira 500 sem detalhe na resposta.
 */
public class ErroDeBancoException extends RuntimeException {

    public ErroDeBancoException(String operacao, Throwable causa) {
        super("Falha no banco ao " + operacao, causa);
    }
}
