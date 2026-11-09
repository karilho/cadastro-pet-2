package com.devmagro.cadastropet.repository.jpa;

import com.devmagro.cadastropet.model.Pet;
import com.devmagro.cadastropet.repository.FiltroPet;
import com.devmagro.cadastropet.repository.PetRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementação padrão a partir do Passo 4: Spring Data JPA.
 * As outras continuam disponíveis pelos profiles "jdbc" e "memoria".
 */
@Repository
@Profile("!memoria & !jdbc")
public class JpaPetRepository implements PetRepository {

    private final PetJpa jpa;

    public JpaPetRepository(PetJpa jpa) {
        this.jpa = jpa;
    }

    @Override
    @Transactional
    public Pet salvar(Pet pet) {
        if (pet.id() == null) {
            return jpa.save(PetEntity.de(pet)).paraPet();
        }
        // Alteração: carrega a linha e troca só os campos editáveis. O JPA gera o UPDATE no fim da transação.
        PetEntity existente = jpa.findById(pet.id()).orElseThrow();
        existente.copiarCamposEditaveis(pet);
        return existente.paraPet();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pet> buscarPorId(long id) {
        return jpa.findById(id).map(PetEntity::paraPet);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pet> buscar(FiltroPet filtro) {
        return jpa.findAll(FiltroComoSpecification.de(filtro)).stream().map(PetEntity::paraPet).toList();
    }

    @Override
    @Transactional
    public boolean excluir(long id) {
        if (!jpa.existsById(id)) {
            return false;
        }
        jpa.deleteById(id);
        return true;
    }
}
