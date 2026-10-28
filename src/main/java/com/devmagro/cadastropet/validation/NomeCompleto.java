package com.devmagro.cadastropet.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Nome em branco é válido (o campo é opcional). Nome informado precisa ter só letras
 * e espaços, e pelo menos duas palavras (nome e sobrenome).
 */
@Documented
@Constraint(validatedBy = NomeCompletoValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface NomeCompleto {

    String message() default Mensagens.NOME_INVALIDO;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
