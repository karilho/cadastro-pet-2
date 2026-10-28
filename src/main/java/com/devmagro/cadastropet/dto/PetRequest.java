package com.devmagro.cadastropet.dto;

import com.devmagro.cadastropet.model.SexoPet;
import com.devmagro.cadastropet.model.TipoPet;
import com.devmagro.cadastropet.validation.Cadastro;
import com.devmagro.cadastropet.validation.Mensagens;
import com.devmagro.cadastropet.validation.NomeCompleto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public record PetRequest(
        @NomeCompleto
        String nomeCompleto,

        @NotNull(groups = Cadastro.class, message = Mensagens.TIPO_INVALIDO)
        TipoPet tipo,

        @NotNull(groups = Cadastro.class, message = Mensagens.SEXO_INVALIDO)
        SexoPet sexo,

        @NotNull(message = Mensagens.ENDERECO_OBRIGATORIO)
        @Valid
        EnderecoRequest endereco,

        @DecimalMin(value = "0", inclusive = false, message = Mensagens.IDADE_INVALIDA)
        @DecimalMax(value = "20", message = Mensagens.IDADE_INVALIDA)
        BigDecimal idade,

        @DecimalMin(value = "0.5", message = Mensagens.PESO_INVALIDO)
        @DecimalMax(value = "60", message = Mensagens.PESO_INVALIDO)
        BigDecimal peso,

        @Pattern(regexp = "^[\\p{L} \\-]*$", message = Mensagens.RACA_INVALIDA)
        String raca) {
}
