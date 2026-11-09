package com.devmagro.cadastropet;

import com.devmagro.cadastropet.dto.EnderecoRequest;
import com.devmagro.cadastropet.dto.PetRequest;
import com.devmagro.cadastropet.model.SexoPet;
import com.devmagro.cadastropet.model.TipoPet;
import com.devmagro.cadastropet.validation.Mensagens;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Regra do nome isolada, sem Spring: só a Bean Validation.
 */
@DisplayName("Regra do nome completo")
class NomeCompletoValidatorTest {

    private static Validator validador;

    @BeforeAll
    static void criarValidador() {
        validador = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private List<String> errosDoNome(String nome) {
        PetRequest pet = new PetRequest(nome, TipoPet.GATO, SexoPet.FEMEA,
                new EnderecoRequest("1", "Curitiba", "Rua X"), null, null, null);
        return validador.validate(pet).stream()
                .filter(erro -> erro.getPropertyPath().toString().equals("nomeCompleto"))
                .map(ConstraintViolation::getMessage)
                .toList();
    }

    @ParameterizedTest(name = "accepts \"{0}\"")
    @ValueSource(strings = {"Rex Almeida", "Florzinha da Silva", "Cármen Lúcia", "  Rex Almeida  ", "Ana Maria Souza"})
    void acceptsNamesWithAtLeastTwoWordsOfLetters(String nome) {
        assertThat(errosDoNome(nome)).isEmpty();
    }

    @ParameterizedTest(name = "accepts blank value \"{0}\" (optional field)")
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void acceptsBlankNameBecauseItIsOptional(String nome) {
        assertThat(errosDoNome(nome)).isEmpty();
    }

    @ParameterizedTest(name = "asks for surname in \"{0}\"")
    @ValueSource(strings = {"Rex", "  Rex  ", "Florzinha"})
    void asksForSurnameWhenThereIsOnlyOneWord(String nome) {
        assertThat(errosDoNome(nome)).containsExactly(Mensagens.NOME_SEM_SOBRENOME);
    }

    @ParameterizedTest(name = "rejects \"{0}\"")
    @ValueSource(strings = {"Rex 2", "Rex@Silva", "Rex-Silva", "R2D2 Robo", "Rex_Silva Souza"})
    void rejectsNamesWithDigitsOrSymbols(String nome) {
        assertThat(errosDoNome(nome)).containsExactly(Mensagens.NOME_INVALIDO);
    }
}
