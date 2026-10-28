package com.devmagro.cadastropet.dto;

import com.devmagro.cadastropet.model.Endereco;
import com.devmagro.cadastropet.model.Pet;
import com.devmagro.cadastropet.model.SexoPet;
import com.devmagro.cadastropet.model.TipoPet;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PetResponse(
        Long id,
        String nomeCompleto,
        TipoPet tipo,
        SexoPet sexo,
        EnderecoResponse endereco,
        BigDecimal idade,
        BigDecimal peso,
        String raca,
        LocalDateTime dataCadastro) {

    public record EnderecoResponse(String numeroCasa, String cidade, String rua) {

        static EnderecoResponse de(Endereco endereco) {
            return new EnderecoResponse(endereco.numeroCasa(), endereco.cidade(), endereco.rua());
        }
    }

    public static PetResponse de(Pet pet) {
        return new PetResponse(
                pet.id(),
                pet.nomeCompleto(),
                pet.tipo(),
                pet.sexo(),
                EnderecoResponse.de(pet.endereco()),
                pet.idade(),
                pet.peso(),
                pet.raca(),
                pet.dataCadastro());
    }
}
