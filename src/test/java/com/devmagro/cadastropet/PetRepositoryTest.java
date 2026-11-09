package com.devmagro.cadastropet;

import com.devmagro.cadastropet.model.Endereco;
import com.devmagro.cadastropet.model.Pet;
import com.devmagro.cadastropet.model.SexoPet;
import com.devmagro.cadastropet.model.TipoPet;
import com.devmagro.cadastropet.repository.FiltroPet;
import com.devmagro.cadastropet.repository.PetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testa a implementação ativa de PetRepository contra o PostgreSQL real, com o seed da documentação.
 */
@SpringBootTest
@DisplayName("PetRepository no PostgreSQL")
class PetRepositoryTest extends ComBancoDeTeste {

    @Autowired
    private PetRepository repositorio;

    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void recriarBancoComSeed() throws Exception {
        try (Connection conexao = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(conexao, new FileSystemResource("database/schema.sql"));
            ScriptUtils.executeSqlScript(conexao, new FileSystemResource("database/seed.sql"));
        }
    }

    private List<Long> ids(FiltroPet filtro) {
        return repositorio.buscar(filtro).stream().map(Pet::id).toList();
    }

    private FiltroPet filtro(String nome, String raca, String endereco, TipoPet tipo,
                             BigDecimal idadeMin, BigDecimal idadeMax, BigDecimal pesoMin, BigDecimal pesoMax) {
        return new FiltroPet(nome, raca, endereco, tipo, null, idadeMin, idadeMax, pesoMin, pesoMax);
    }

    @Test
    void listsAllSeedPetsInIdOrder() {
        assertThat(ids(filtro(null, null, null, null, null, null, null, null)))
                .containsExactly(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);
    }

    @Test
    void searchesTextIgnoringCaseAndAccents() {
        assertThat(ids(filtro("FLOR", null, null, null, null, null, null, null))).containsExactly(2L);
        assertThat(ids(filtro(null, "siames", null, null, null, null, null, null))).containsExactly(2L);
        assertThat(ids(filtro(null, null, "flor", null, null, null, null, null))).containsExactly(1L);
        assertThat(ids(filtro(null, null, "colombo", null, null, null, null, null))).containsExactly(7L, 10L);
    }

    @Test
    void appliesRangesAndExcludesNullValues() {
        assertThat(ids(filtro(null, null, null, null, new BigDecimal("1"), new BigDecimal("5"), null, null)))
                .containsExactly(1L, 3L, 8L, 10L);
        assertThat(ids(filtro(null, null, null, null, null, null, null, new BigDecimal("5"))))
                .containsExactly(1L, 2L, 4L, 5L, 8L);
        assertThat(ids(filtro(null, null, null, TipoPet.GATO, new BigDecimal("1"), null, null, null)))
                .containsExactly(2L, 7L, 8L);
    }

    @Test
    void doesNotTreatSearchTextAsSql() {
        assertThat(ids(filtro("' OR '1'='1", null, null, null, null, null, null, null))).isEmpty();
    }

    @Test
    void insertsAndReturnsGeneratedIdKeepingNulls() {
        Pet novo = new Pet(null, null, TipoPet.GATO, SexoPet.MACHO, new Endereco(null, "Curitiba", "Rua X"),
                null, null, null, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        Pet salvo = repositorio.salvar(novo);

        assertThat(salvo.id()).isEqualTo(11L);
        Pet lido = repositorio.buscarPorId(11L).orElseThrow();
        assertThat(lido.nomeCompleto()).isNull();
        assertThat(lido.idade()).isNull();
        assertThat(lido.peso()).isNull();
        assertThat(lido.endereco().numeroCasa()).isNull();
    }

    @Test
    void updatesEditableFieldsOnly() {
        Pet atual = repositorio.buscarPorId(2L).orElseThrow();
        Pet alterado = new Pet(2L, "Florzinha Souza", TipoPet.CACHORRO, SexoPet.MACHO,
                new Endereco("1", "Colombo", "Rua Verde"), new BigDecimal("7"), null, "Persa", atual.dataCadastro());

        repositorio.salvar(alterado);

        Pet lido = repositorio.buscarPorId(2L).orElseThrow();
        assertThat(lido.nomeCompleto()).isEqualTo("Florzinha Souza");
        assertThat(lido.tipo()).isEqualTo(TipoPet.GATO);
        assertThat(lido.sexo()).isEqualTo(SexoPet.FEMEA);
        assertThat(lido.peso()).isNull();
    }

    @Test
    void deletesAndReportsWhetherSomethingWasRemoved() {
        assertThat(repositorio.excluir(3L)).isTrue();
        assertThat(repositorio.excluir(3L)).isFalse();
        assertThat(repositorio.buscarPorId(3L)).isEmpty();
    }
}
