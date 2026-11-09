package com.devmagro.cadastropet;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Sobe UM PostgreSQL descartável (Testcontainers) para todos os testes que precisam de banco.
 * O schema.sql roda a cada contexto do Spring e recria a tabela do zero.
 */
public abstract class ComBancoDeTeste {

    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

    static {
        POSTGRES.start();
    }

    @DynamicPropertySource
    static void apontarParaOBancoDeTeste(DynamicPropertyRegistry propriedades) {
        propriedades.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        propriedades.add("spring.datasource.username", POSTGRES::getUsername);
        propriedades.add("spring.datasource.password", POSTGRES::getPassword);
        propriedades.add("spring.sql.init.mode", () -> "always");
        propriedades.add("spring.sql.init.schema-locations", () -> "file:database/schema.sql");
    }
}
