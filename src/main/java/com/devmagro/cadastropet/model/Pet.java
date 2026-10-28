package com.devmagro.cadastropet.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Campos opcionais não informados são null. O texto "NÃO INFORMADO" só existe no front.
 */
public record Pet(
        Long id,
        String nomeCompleto,
        TipoPet tipo,
        SexoPet sexo,
        Endereco endereco,
        BigDecimal idade,
        BigDecimal peso,
        String raca,
        LocalDateTime dataCadastro) {

    public Pet comId(Long novoId) {
        return new Pet(novoId, nomeCompleto, tipo, sexo, endereco, idade, peso, raca, dataCadastro);
    }
}
