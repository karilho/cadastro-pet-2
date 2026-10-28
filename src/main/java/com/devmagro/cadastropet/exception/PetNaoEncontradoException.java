package com.devmagro.cadastropet.exception;

import com.devmagro.cadastropet.validation.Mensagens;

public class PetNaoEncontradoException extends RuntimeException {

    public PetNaoEncontradoException() {
        super(Mensagens.PET_NAO_ENCONTRADO);
    }
}
