package com.devmagro.cadastropet;

import com.devmagro.cadastropet.dto.EnderecoRequest;
import com.devmagro.cadastropet.dto.PetRequest;
import com.devmagro.cadastropet.exception.PetNaoEncontradoException;
import com.devmagro.cadastropet.exception.RegraDeNegocioException;
import com.devmagro.cadastropet.model.Pet;
import com.devmagro.cadastropet.model.SexoPet;
import com.devmagro.cadastropet.model.TipoPet;
import com.devmagro.cadastropet.repository.FiltroPet;
import com.devmagro.cadastropet.repository.InMemoryPetRepository;
import com.devmagro.cadastropet.service.PetService;
import com.devmagro.cadastropet.validation.Mensagens;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("PetService")
class PetServiceTest {

    private PetService servico;

    @BeforeEach
    void criarServico() {
        servico = new PetService(new InMemoryPetRepository());
    }

    private PetRequest gato(String nome, String raca, BigDecimal idade) {
        return new PetRequest(nome, TipoPet.GATO, SexoPet.FEMEA,
                new EnderecoRequest("12", "Curitiba", "Rua Azul"), idade, new BigDecimal("4"), raca);
    }

    private FiltroPet semFiltro() {
        return new FiltroPet(null, null, null, null, null, null, null, null, null);
    }

    @Test
    void assignsIdAndRegistrationDateOnCreate() {
        Pet criado = servico.cadastrar(gato("Mimi Santos", "Persa", new BigDecimal("2")));

        assertThat(criado.id()).isEqualTo(1L);
        assertThat(criado.dataCadastro()).isNotNull();
        assertThat(servico.cadastrar(gato("Nina Rocha", null, null)).id()).isEqualTo(2L);
    }

    @Test
    void turnsBlankOptionalFieldsIntoNull() {
        PetRequest dados = new PetRequest("   ", TipoPet.CACHORRO, SexoPet.MACHO,
                new EnderecoRequest("", "Curitiba", "Rua Azul"), null, null, "  ");

        Pet criado = servico.cadastrar(dados);

        assertThat(criado.nomeCompleto()).isNull();
        assertThat(criado.raca()).isNull();
        assertThat(criado.endereco().numeroCasa()).isNull();
    }

    @Test
    void trimsTextFields() {
        Pet criado = servico.cadastrar(gato("  Mimi Santos  ", " Persa ", null));

        assertThat(criado.nomeCompleto()).isEqualTo("Mimi Santos");
        assertThat(criado.raca()).isEqualTo("Persa");
    }

    @Test
    void rejectsChangingTipoOnUpdate() {
        Pet criado = servico.cadastrar(gato("Mimi Santos", "Persa", null));
        PetRequest mudaTipo = new PetRequest("Mimi Santos", TipoPet.CACHORRO, SexoPet.FEMEA,
                new EnderecoRequest("12", "Curitiba", "Rua Azul"), null, null, null);

        assertThatThrownBy(() -> servico.alterar(criado.id(), mudaTipo))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessage(Mensagens.TIPO_E_SEXO_IMUTAVEIS);
    }

    @Test
    void rejectsChangingSexoOnUpdate() {
        Pet criado = servico.cadastrar(gato("Mimi Santos", "Persa", null));
        PetRequest mudaSexo = new PetRequest("Mimi Santos", TipoPet.GATO, SexoPet.MACHO,
                new EnderecoRequest("12", "Curitiba", "Rua Azul"), null, null, null);

        assertThatThrownBy(() -> servico.alterar(criado.id(), mudaSexo))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessage(Mensagens.TIPO_E_SEXO_IMUTAVEIS);
    }

    @Test
    void keepsIdTypeSexAndRegistrationDateOnUpdate() {
        Pet criado = servico.cadastrar(gato("Mimi Santos", "Persa", null));
        PetRequest semTipoNemSexo = new PetRequest("Mimi Souza", null, null,
                new EnderecoRequest("99", "Colombo", "Rua Verde"), new BigDecimal("3"), null, "Siamês");

        Pet alterado = servico.alterar(criado.id(), semTipoNemSexo);

        assertThat(alterado.id()).isEqualTo(criado.id());
        assertThat(alterado.tipo()).isEqualTo(TipoPet.GATO);
        assertThat(alterado.sexo()).isEqualTo(SexoPet.FEMEA);
        assertThat(alterado.dataCadastro()).isEqualTo(criado.dataCadastro());
        assertThat(alterado.nomeCompleto()).isEqualTo("Mimi Souza");
        assertThat(alterado.endereco().cidade()).isEqualTo("Colombo");
    }

    @Test
    void failsWhenPetDoesNotExist() {
        PetRequest dados = gato("Mimi Santos", null, null);

        assertThatThrownBy(() -> servico.buscarPorId(99)).isInstanceOf(PetNaoEncontradoException.class);
        assertThatThrownBy(() -> servico.alterar(99, dados)).isInstanceOf(PetNaoEncontradoException.class);
        assertThatThrownBy(() -> servico.excluir(99)).isInstanceOf(PetNaoEncontradoException.class);
    }

    @Test
    void deletesExistingPet() {
        Pet criado = servico.cadastrar(gato("Mimi Santos", null, null));

        servico.excluir(criado.id());

        assertThat(servico.buscar(semFiltro())).isEmpty();
    }

    @Test
    void searchesByPartOfTextIgnoringCaseAndAccents() {
        servico.cadastrar(gato("Florzinha da Silva", "Siamês", null));
        servico.cadastrar(gato("Nina Rocha", "Persa", null));

        List<Pet> porNome = servico.buscar(new FiltroPet("FLOR", null, null, null, null, null, null, null, null));
        List<Pet> porRaca = servico.buscar(new FiltroPet(null, "siames", null, null, null, null, null, null, null));

        assertThat(porNome).extracting(Pet::nomeCompleto).containsExactly("Florzinha da Silva");
        assertThat(porRaca).extracting(Pet::raca).containsExactly("Siamês");
    }

    @Test
    void excludesPetsWithNullFieldFromRangeFilters() {
        servico.cadastrar(gato("Mimi Santos", "Persa", new BigDecimal("2")));
        servico.cadastrar(gato("Nina Rocha", "Persa", null));

        List<Pet> resultado = servico.buscar(new FiltroPet(null, null, null, null, null,
                new BigDecimal("1"), null, null, null));

        assertThat(resultado).extracting(Pet::nomeCompleto).containsExactly("Mimi Santos");
    }

    @Test
    void combinesFiltersWithAnd() {
        servico.cadastrar(gato("Mimi Santos", "Persa", new BigDecimal("2")));
        servico.cadastrar(gato("Mimi Souza", "Siamês", new BigDecimal("2")));

        List<Pet> resultado = servico.buscar(new FiltroPet("mimi", "persa", null, null, null, null, null, null, null));

        assertThat(resultado).extracting(Pet::nomeCompleto).containsExactly("Mimi Santos");
    }
}
