package com.devmagro.cadastropet.repository;

import com.devmagro.cadastropet.model.Pet;

import java.util.List;
import java.util.Optional;

/**
 * Contrato de armazenamento. No Passo 1 a implementação guarda em memória.
 * No Passo 2 ela é trocada por uma que usa o banco, sem mexer em service nem controller.
 */
public interface PetRepository {

    /** Insere (id nulo) ou atualiza (id existente) e devolve o pet salvo. */
    Pet salvar(Pet pet);

    Optional<Pet> buscarPorId(long id);

    /** Devolve os pets que atendem ao filtro, em ordem crescente de id. */
    List<Pet> buscar(FiltroPet filtro);

    /** Devolve true se existia e foi removido. */
    boolean excluir(long id);
}
