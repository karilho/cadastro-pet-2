package com.devmagro.cadastropet.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Formato único de erro de toda a API.
 */
public record ErroResponse(
        int status,
        String erro,
        String mensagem,
        List<CampoComErro> campos,
        String caminho,
        LocalDateTime timestamp) {

    public static ErroResponse de(int status, String erro, String mensagem, List<CampoComErro> campos, String caminho) {
        return new ErroResponse(status, erro, mensagem, campos, caminho, LocalDateTime.now().withNano(0));
    }
}
