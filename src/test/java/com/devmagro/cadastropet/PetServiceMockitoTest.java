package com.devmagro.cadastropet;

import com.devmagro.cadastropet.dto.EnderecoRequest;
import com.devmagro.cadastropet.dto.PetRequest;
import com.devmagro.cadastropet.exception.PetNaoEncontradoException;
import com.devmagro.cadastropet.exception.RegraDeNegocioException;
import com.devmagro.cadastropet.model.Endereco;
import com.devmagro.cadastropet.model.Pet;
import com.devmagro.cadastropet.model.SexoPet;
import com.devmagro.cadastropet.model.TipoPet;
import com.devmagro.cadastropet.repository.PetRepository;
import com.devmagro.cadastropet.service.PetService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Service isolado do armazenamento: o repositório é um mock.
 * Prova o que o service manda gravar, não como o repositório grava.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PetService com repositório simulado")
class PetServiceMockitoTest {

    @Mock
    private PetRepository repositorio;

    @InjectMocks
    private PetService servico;

    private final Pet florzinha = new Pet(2L, "Florzinha da Silva", TipoPet.GATO, SexoPet.FEMEA,
            new Endereco("456", "Seilândia", "Rua Azul"), new BigDecimal("6"), new BigDecimal("5"), "Siamês",
            LocalDateTime.of(2026, 1, 10, 9, 0));

    private PetRequest dados(TipoPet tipo, SexoPet sexo) {
        return new PetRequest(" Florzinha Souza ", tipo, sexo, new EnderecoRequest(" ", " Colombo ", " Rua Verde "),
                new BigDecimal("7"), null, "");
    }

    @Test
    void sendsNormalizedPetWithoutIdToRepositoryOnCreate() {
        when(repositorio.salvar(any())).thenAnswer(chamada -> ((Pet) chamada.getArgument(0)).comId(1L));

        servico.cadastrar(dados(TipoPet.GATO, SexoPet.FEMEA));

        ArgumentCaptor<Pet> enviado = ArgumentCaptor.forClass(Pet.class);
        verify(repositorio).salvar(enviado.capture());
        assertThat(enviado.getValue().id()).isNull();
        assertThat(enviado.getValue().nomeCompleto()).isEqualTo("Florzinha Souza");
        assertThat(enviado.getValue().endereco()).isEqualTo(new Endereco(null, "Colombo", "Rua Verde"));
        assertThat(enviado.getValue().raca()).isNull();
        assertThat(enviado.getValue().dataCadastro()).isNotNull();
    }

    @Test
    void keepsOriginalTypeSexAndDateWhenUpdating() {
        when(repositorio.buscarPorId(2L)).thenReturn(Optional.of(florzinha));
        when(repositorio.salvar(any())).thenAnswer(chamada -> chamada.getArgument(0));

        Pet alterado = servico.alterar(2L, dados(null, null));

        assertThat(alterado.id()).isEqualTo(2L);
        assertThat(alterado.tipo()).isEqualTo(TipoPet.GATO);
        assertThat(alterado.sexo()).isEqualTo(SexoPet.FEMEA);
        assertThat(alterado.dataCadastro()).isEqualTo(florzinha.dataCadastro());
    }

    @Test
    void neverSavesWhenTryingToChangeType() {
        when(repositorio.buscarPorId(2L)).thenReturn(Optional.of(florzinha));

        assertThatThrownBy(() -> servico.alterar(2L, dados(TipoPet.CACHORRO, SexoPet.FEMEA)))
                .isInstanceOf(RegraDeNegocioException.class);
        verify(repositorio, never()).salvar(any());
    }

    @Test
    void neverSavesWhenPetDoesNotExist() {
        when(repositorio.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servico.alterar(99L, dados(null, null)))
                .isInstanceOf(PetNaoEncontradoException.class);
        verify(repositorio, never()).salvar(any());
    }

    @Test
    void reportsNotFoundWhenRepositoryRemovesNothing() {
        when(repositorio.excluir(99L)).thenReturn(false);

        assertThatThrownBy(() -> servico.excluir(99L)).isInstanceOf(PetNaoEncontradoException.class);
    }
}
