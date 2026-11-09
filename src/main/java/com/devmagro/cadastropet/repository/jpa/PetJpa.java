package com.devmagro.cadastropet.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * O Spring Data gera a implementação desta interface sozinho.
 */
interface PetJpa extends JpaRepository<PetEntity, Long>, JpaSpecificationExecutor<PetEntity> {
}
