package com.devmagro.cadastropet.repository;

import com.devmagro.cadastropet.model.SexoPet;
import com.devmagro.cadastropet.model.TipoPet;

import java.math.BigDecimal;

/**
 * Todos os campos são opcionais. Os preenchidos se combinam com E.
 */
public record FiltroPet(
        String nome,
        String raca,
        String endereco,
        TipoPet tipo,
        SexoPet sexo,
        BigDecimal idadeMin,
        BigDecimal idadeMax,
        BigDecimal pesoMin,
        BigDecimal pesoMax) {
}
