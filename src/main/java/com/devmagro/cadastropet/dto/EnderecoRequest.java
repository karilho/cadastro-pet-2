package com.devmagro.cadastropet.dto;

import com.devmagro.cadastropet.validation.Mensagens;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record EnderecoRequest(
        @Pattern(regexp = "^\\d*$", message = Mensagens.NUMERO_CASA_INVALIDO)
        String numeroCasa,

        @NotBlank(message = Mensagens.CIDADE_OBRIGATORIA)
        String cidade,

        @NotBlank(message = Mensagens.RUA_OBRIGATORIA)
        String rua) {
}
