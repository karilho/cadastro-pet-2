package com.devmagro.cadastropet.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class NomeCompletoValidator implements ConstraintValidator<NomeCompleto, String> {

    private static final Pattern SO_LETRAS_E_ESPACOS = Pattern.compile("^[\\p{L} ]+$");
    private static final Pattern ESPACOS = Pattern.compile("\\s+");

    @Override
    public boolean isValid(String valor, ConstraintValidatorContext contexto) {
        if (valor == null || valor.isBlank()) {
            return true;
        }

        String nome = valor.strip();
        String mensagem = null;
        if (!SO_LETRAS_E_ESPACOS.matcher(nome).matches()) {
            mensagem = Mensagens.NOME_INVALIDO;
        } else if (ESPACOS.split(nome).length < 2) {
            mensagem = Mensagens.NOME_SEM_SOBRENOME;
        }

        if (mensagem == null) {
            return true;
        }
        contexto.disableDefaultConstraintViolation();
        contexto.buildConstraintViolationWithTemplate(mensagem).addConstraintViolation();
        return false;
    }
}
