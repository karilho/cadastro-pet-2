package com.devmagro.cadastropet;

import org.junit.jupiter.api.DisplayName;
import org.springframework.test.context.ActiveProfiles;

/**
 * Os MESMOS testes do PetRepositoryTest, rodando contra a implementação JDBC.
 * Se JDBC e JPA passam nos mesmos testes, as duas cumprem o mesmo contrato.
 */
@ActiveProfiles("jdbc")
@DisplayName("PetRepository no PostgreSQL (JDBC)")
class JdbcPetRepositoryTest extends PetRepositoryTest {
}
