package com.devmagro.cadastropet.repository.jpa;

import com.devmagro.cadastropet.model.Endereco;
import com.devmagro.cadastropet.model.Pet;
import com.devmagro.cadastropet.model.SexoPet;
import com.devmagro.cadastropet.model.TipoPet;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Mapeamento JPA da tabela pet. Fica separado do Pet do domínio: o domínio não conhece JPA.
 */
@Entity
@Table(name = "pet")
public class PetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_completo")
    private String nomeCompleto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private TipoPet tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private SexoPet sexo;

    @Column(name = "numero_casa")
    private String numeroCasa;

    @Column(nullable = false)
    private String cidade;

    @Column(nullable = false)
    private String rua;

    @Column(name = "idade_anos")
    private BigDecimal idade;

    @Column(name = "peso_kg")
    private BigDecimal peso;

    private String raca;

    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    protected PetEntity() {
        // exigido pelo JPA
    }

    static PetEntity de(Pet pet) {
        PetEntity entidade = new PetEntity();
        entidade.id = pet.id();
        entidade.tipo = pet.tipo();
        entidade.sexo = pet.sexo();
        entidade.dataCadastro = pet.dataCadastro();
        entidade.copiarCamposEditaveis(pet);
        return entidade;
    }

    /** Tipo, sexo, id e data de cadastro nunca mudam (também marcados como updatable = false). */
    void copiarCamposEditaveis(Pet pet) {
        nomeCompleto = pet.nomeCompleto();
        numeroCasa = pet.endereco().numeroCasa();
        cidade = pet.endereco().cidade();
        rua = pet.endereco().rua();
        idade = pet.idade();
        peso = pet.peso();
        raca = pet.raca();
    }

    Pet paraPet() {
        return new Pet(id, nomeCompleto, tipo, sexo, new Endereco(numeroCasa, cidade, rua),
                idade, peso, raca, dataCadastro);
    }
}
