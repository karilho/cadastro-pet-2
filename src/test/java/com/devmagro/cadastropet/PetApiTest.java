package com.devmagro.cadastropet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testa a API de ponta a ponta contra o contrato (docs/04-contrato-da-api.md).
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("API de pets")
class PetApiTest extends ComBancoDeTeste {

    private static final String ORIGEM_DO_FRONT = "http://localhost:5500";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper json;

    // ---------- apoio ----------

    private Map<String, Object> pet(String nome, String tipo, String sexo, String numero, String cidade,
                                    String rua, Object idade, Object peso, String raca) {
        Map<String, Object> endereco = new LinkedHashMap<>();
        endereco.put("numeroCasa", numero);
        endereco.put("cidade", cidade);
        endereco.put("rua", rua);

        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("nomeCompleto", nome);
        corpo.put("tipo", tipo);
        corpo.put("sexo", sexo);
        corpo.put("endereco", endereco);
        corpo.put("idade", idade);
        corpo.put("peso", peso);
        corpo.put("raca", raca);
        return corpo;
    }

    private Map<String, Object> petValido() {
        return pet("Florzinha da Silva", "GATO", "FEMEA", "456", "Seilândia", "Rua Azul", 6, 5.0, "Siamês");
    }

    private ResultActions enviar(Map<String, Object> corpo) throws Exception {
        return mvc.perform(post("/pets").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(corpo)));
    }

    /** Os 10 pets do seed da documentação, na mesma ordem (ids 1 a 10). */
    private void cadastrarSeed() throws Exception {
        List<Map<String, Object>> seed = List.of(
                pet("Rex Almeida", "CACHORRO", "MACHO", "123", "Curitiba", "Rua das Flores", 2, 5.0, "Vira-lata"),
                pet("Florzinha da Silva", "GATO", "FEMEA", "456", "Seilândia", "Rua Azul", 6, 5.0, "Siamês"),
                pet("Thor Oliveira", "CACHORRO", "MACHO", "78", "Curitiba", "Avenida Brasil", 4, 32.5, "Labrador"),
                pet("Mimi Santos", "GATO", "FEMEA", "910", "São José dos Pinhais", "Rua Esmeralda", 0.5, 1.2, "Persa"),
                pet("Bob Costa", "CACHORRO", "MACHO", null, "Pinhais", "Rua do Sol", 0.8, 3.5, "Poodle"),
                pet("Luna Ferreira", "CACHORRO", "FEMEA", "205", "Curitiba", "Rua das Palmeiras", 7, 24.0, "Border Collie"),
                pet("Garfield Souza", "GATO", "MACHO", "33", "Colombo", "Rua Verde", 9, 6.8, "Vira-lata"),
                pet("Nina Rocha", "GATO", "FEMEA", "12", "Araucária", "Rua dos Pinheiros", 1, 3.1, null),
                pet("Max Pereira", "CACHORRO", "MACHO", "540", "Curitiba", "Rua das Acácias", 12, 45.0, "Pastor Alemão"),
                pet("Pipoca Lima", "CACHORRO", "FEMEA", "88", "Colombo", "Rua Central", 3, 8.5, "Vira-lata"));
        for (Map<String, Object> corpo : seed) {
            enviar(corpo).andExpect(status().isCreated());
        }
    }

    // ---------- POST /pets ----------

    @Test
    void createsPetAndReturns201WithLocationHeader() throws Exception {
        enviar(petValido())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/pets/1")))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nomeCompleto").value("Florzinha da Silva"))
                .andExpect(jsonPath("$.tipo").value("GATO"))
                .andExpect(jsonPath("$.endereco.cidade").value("Seilândia"))
                .andExpect(jsonPath("$.dataCadastro").value(notNullValue()));
    }

    @Test
    void returnsNullForOptionalFieldsThatWereNotInformed() throws Exception {
        enviar(pet(null, "CACHORRO", "MACHO", null, "Curitiba", "Rua X", null, null, null))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nomeCompleto").value(nullValue()))
                .andExpect(jsonPath("$.endereco.numeroCasa").value(nullValue()))
                .andExpect(jsonPath("$.idade").value(nullValue()))
                .andExpect(jsonPath("$.peso").value(nullValue()))
                .andExpect(jsonPath("$.raca").value(nullValue()));
    }

    @Test
    void acceptsBlankNameAndStoresItAsNull() throws Exception {
        enviar(pet("   ", "GATO", "MACHO", "1", "Curitiba", "Rua X", null, null, null))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nomeCompleto").value(nullValue()));
    }

    @Test
    void acceptsAccentedName() throws Exception {
        enviar(pet("Cármen Lúcia", "GATO", "FEMEA", "1", "Curitiba", "Rua X", null, null, null))
                .andExpect(status().isCreated());
    }

    @ParameterizedTest(name = "rejects name \"{0}\" with \"{1}\"")
    @CsvSource(delimiter = '|', value = {
            "Rex          | Informe nome e sobrenome do pet.",
            "Rex 2        | O nome deve conter apenas letras.",
            "Rex@Silva    | O nome deve conter apenas letras.",
            "Rex Silva3   | O nome deve conter apenas letras."})
    void rejectsInvalidNames(String nome, String mensagem) throws Exception {
        Map<String, Object> corpo = petValido();
        corpo.put("nomeCompleto", nome.strip());

        enviar(corpo)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos[0].campo").value("nomeCompleto"))
                .andExpect(jsonPath("$.campos[0].mensagem").value(mensagem.strip()));
    }

    @ParameterizedTest(name = "accepts weight {0}")
    @CsvSource({"0.5", "5", "7.5", "60"})
    void acceptsWeightInsideTheLimits(String peso) throws Exception {
        Map<String, Object> corpo = petValido();
        corpo.put("peso", new java.math.BigDecimal(peso));

        enviar(corpo).andExpect(status().isCreated());
    }

    @ParameterizedTest(name = "rejects weight {0}")
    @CsvSource({"0.4", "60.1", "0", "-3", "100"})
    void rejectsWeightOutsideTheLimits(String peso) throws Exception {
        Map<String, Object> corpo = petValido();
        corpo.put("peso", new java.math.BigDecimal(peso));

        enviar(corpo)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos[0].campo").value("peso"))
                .andExpect(jsonPath("$.campos[0].mensagem").value("O peso deve ser um número entre 0,5kg e 60kg."));
    }

    @ParameterizedTest(name = "accepts age {0}")
    @CsvSource({"0.1", "0.5", "3", "3.5", "20"})
    void acceptsAgeInsideTheLimits(String idade) throws Exception {
        Map<String, Object> corpo = petValido();
        corpo.put("idade", new java.math.BigDecimal(idade));

        enviar(corpo).andExpect(status().isCreated());
    }

    @ParameterizedTest(name = "rejects age {0}")
    @CsvSource({"20.1", "21", "0", "-2"})
    void rejectsAgeOutsideTheLimits(String idade) throws Exception {
        Map<String, Object> corpo = petValido();
        corpo.put("idade", new java.math.BigDecimal(idade));

        enviar(corpo)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos[0].campo").value("idade"))
                .andExpect(jsonPath("$.campos[0].mensagem")
                        .value("A idade deve ser um número maior que 0 e no máximo 20 anos."));
    }

    @Test
    void rejectsAgeSentAsTextWithComma() throws Exception {
        Map<String, Object> corpo = petValido();
        corpo.put("idade", "3,5");

        enviar(corpo)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos[0].campo").value("idade"))
                .andExpect(jsonPath("$.campos[0].mensagem")
                        .value("A idade deve ser um número maior que 0 e no máximo 20 anos."));
    }

    @ParameterizedTest(name = "rejects breed \"{0}\"")
    @CsvSource({"Poodle2", "Poodle!", "Vira_lata"})
    void rejectsInvalidBreeds(String raca) throws Exception {
        Map<String, Object> corpo = petValido();
        corpo.put("raca", raca);

        enviar(corpo)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos[0].campo").value("raca"))
                .andExpect(jsonPath("$.campos[0].mensagem").value("A raça deve conter apenas letras, espaços e hífen."));
    }

    @Test
    void acceptsBreedWithHyphenAndAccent() throws Exception {
        Map<String, Object> corpo = petValido();
        corpo.put("raca", "Vira-lata Pastor Alemão");

        enviar(corpo).andExpect(status().isCreated());
    }

    @Test
    void rejectsHouseNumberWithLetters() throws Exception {
        enviar(pet("Rex Almeida", "CACHORRO", "MACHO", "12a", "Curitiba", "Rua X", null, null, null))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos[0].campo").value("endereco.numeroCasa"))
                .andExpect(jsonPath("$.campos[0].mensagem").value("O número da casa deve conter apenas dígitos."));
    }

    @Test
    void listsAllInvalidFieldsAtOnce() throws Exception {
        Map<String, Object> corpo = pet("Rex", null, null, "12a", null, "", 0, 61, "Poodle2");

        enviar(corpo)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.erro").value("Dados inválidos"))
                .andExpect(jsonPath("$.caminho").value("/pets"))
                .andExpect(jsonPath("$.timestamp").value(notNullValue()))
                .andExpect(jsonPath("$.campos[*].campo", containsInAnyOrder(
                        "nomeCompleto", "tipo", "sexo", "endereco.numeroCasa", "endereco.cidade",
                        "endereco.rua", "idade", "peso", "raca")));
    }

    @Test
    void rejectsMissingAddress() throws Exception {
        Map<String, Object> corpo = petValido();
        corpo.remove("endereco");

        enviar(corpo)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos[0].campo").value("endereco"));
    }

    @Test
    void rejectsUnknownTipoValue() throws Exception {
        Map<String, Object> corpo = petValido();
        corpo.put("tipo", "ELEFANTE");

        enviar(corpo)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos[0].campo").value("tipo"))
                .andExpect(jsonPath("$.campos[0].mensagem").value("Tipo inválido. Use Cachorro ou Gato."));
    }

    @Test
    void rejectsLowercaseTipo() throws Exception {
        Map<String, Object> corpo = petValido();
        corpo.put("tipo", "gato");

        enviar(corpo)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos[0].campo").value("tipo"));
    }

    @Test
    void rejectsUnknownSexoValue() throws Exception {
        Map<String, Object> corpo = petValido();
        corpo.put("sexo", "OUTRO");

        enviar(corpo)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos[0].campo").value("sexo"))
                .andExpect(jsonPath("$.campos[0].mensagem").value("Sexo inválido. Use Macho ou Femea."));
    }

    @Test
    void treatsEmptyTipoAndSexoAsMissingAndStillListsTheOtherErrors() throws Exception {
        Map<String, Object> corpo = pet("Rex Almeida", "", "", "12", "Curitiba", "Rua X", 2, 61, null);

        enviar(corpo)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos[*].campo", containsInAnyOrder("tipo", "sexo", "peso")))
                .andExpect(jsonPath("$.campos[?(@.campo=='tipo')].mensagem").value("Tipo inválido. Use Cachorro ou Gato."));
    }

    @Test
    void rejectsMalformedJson() throws Exception {
        mvc.perform(post("/pets").contentType(MediaType.APPLICATION_JSON).content("{ isto nao e json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.campos").value(empty()));
    }

    // ---------- GET /pets ----------

    @Test
    void returnsEmptyListInsteadOf404WhenThereAreNoPets() throws Exception {
        mvc.perform(get("/pets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(empty()));
    }

    @Test
    void listsAllPetsInIdOrder() throws Exception {
        cadastrarSeed();

        mvc.perform(get("/pets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(10)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[9].id").value(10));
    }

    @ParameterizedTest(name = "GET /pets?{0} returns ids {1}")
    @CsvSource(delimiter = '|', value = {
            "nome=flor                       | 2",
            "nome=silva                      | 2",
            "nome=FLOR                       | 2",
            "raca=siames                     | 2",
            "raca=pastor                     | 9",
            "raca=Vira-lata                  | 1,7,10",
            "endereco=flor                   | 1",
            "endereco=colombo                | 7,10",
            "tipo=GATO&idadeMin=1            | 2,7,8",
            "tipo=CACHORRO&pesoMin=20        | 3,6,9",
            "pesoMax=5                       | 1,2,4,5,8",
            "idadeMin=1&idadeMax=5           | 1,3,8,10",
            "idadeMax=0.9                    | 4,5",
            "tipo=GATO&sexo=FEMEA&raca=siames| 2",
            "nome=zzzz                       | "})
    void filtersPetsAsDescribedInTheContract(String consulta, String idsEsperados) throws Exception {
        cadastrarSeed();
        String[] ids = idsEsperados == null || idsEsperados.isBlank() ? new String[0] : idsEsperados.strip().split(",");

        ResultActions resposta = mvc.perform(get("/pets?" + consulta.strip())).andExpect(status().isOk());
        resposta.andExpect(jsonPath("$", hasSize(ids.length)));
        for (int i = 0; i < ids.length; i++) {
            resposta.andExpect(jsonPath("$[" + i + "].id").value(Integer.parseInt(ids[i].strip())));
        }
    }

    @Test
    void rejectsUnknownTipoInFilter() throws Exception {
        mvc.perform(get("/pets?tipo=XYZ"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos[0].campo").value("tipo"))
                .andExpect(jsonPath("$.campos[0].mensagem").value("Tipo inválido. Use Cachorro ou Gato."));
    }

    @Test
    void rejectsNonNumericRangeFilter() throws Exception {
        mvc.perform(get("/pets?idadeMin=abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos[0].campo").value("idadeMin"));
    }

    // ---------- GET /pets/{id} ----------

    @Test
    void returnsPetById() throws Exception {
        cadastrarSeed();

        mvc.perform(get("/pets/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeCompleto").value("Florzinha da Silva"));
    }

    @Test
    void returns404InTheStandardFormatWhenPetDoesNotExist() throws Exception {
        mvc.perform(get("/pets/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.erro").value("Não encontrado"))
                .andExpect(jsonPath("$.mensagem").value("Pet não encontrado."))
                .andExpect(jsonPath("$.campos").value(empty()))
                .andExpect(jsonPath("$.caminho").value("/pets/999"));
    }

    @Test
    void rejectsNonNumericId() throws Exception {
        mvc.perform(get("/pets/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos[0].campo").value("id"));
    }

    // ---------- PUT /pets/{id} ----------

    @Test
    void updatesPetAndKeepsIdAndRegistrationDate() throws Exception {
        cadastrarSeed();
        Map<String, Object> corpo = pet("Florzinha Souza", "GATO", "FEMEA", "99", "Colombo", "Rua Verde", 7, 5.5, "Siamês Azul");

        mvc.perform(put("/pets/2").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(corpo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.nomeCompleto").value("Florzinha Souza"))
                .andExpect(jsonPath("$.endereco.cidade").value("Colombo"))
                .andExpect(jsonPath("$.raca").value("Siamês Azul"));

        mvc.perform(get("/pets/2"))
                .andExpect(jsonPath("$.nomeCompleto").value("Florzinha Souza"));
    }

    @Test
    void acceptsUpdateWithoutTipoAndSexo() throws Exception {
        cadastrarSeed();
        Map<String, Object> corpo = pet("Florzinha Souza", null, null, "99", "Colombo", "Rua Verde", 7, 5.5, null);
        corpo.remove("tipo");
        corpo.remove("sexo");

        mvc.perform(put("/pets/2").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(corpo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("GATO"))
                .andExpect(jsonPath("$.sexo").value("FEMEA"));
    }

    @Test
    void rejectsChangingTipoOrSexoOnUpdate() throws Exception {
        cadastrarSeed();
        Map<String, Object> mudaTipo = pet("Florzinha da Silva", "CACHORRO", "FEMEA", "456", "Seilândia", "Rua Azul", 6, 5.0, "Siamês");
        Map<String, Object> mudaSexo = pet("Florzinha da Silva", "GATO", "MACHO", "456", "Seilândia", "Rua Azul", 6, 5.0, "Siamês");

        mvc.perform(put("/pets/2").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(mudaTipo)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos[0].campo").value("tipo"))
                .andExpect(jsonPath("$.campos[0].mensagem").value("Tipo e sexo não podem ser alterados."));

        mvc.perform(put("/pets/2").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(mudaSexo)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos[0].campo").value("sexo"));
    }

    @Test
    void appliesTheSameValidationOnUpdate() throws Exception {
        cadastrarSeed();
        Map<String, Object> corpo = pet("Rex", "GATO", "FEMEA", "456", "Seilândia", "Rua Azul", 6, 61, "Siamês");

        mvc.perform(put("/pets/2").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(corpo)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos[*].campo", containsInAnyOrder("nomeCompleto", "peso")));
    }

    @Test
    void returns404WhenUpdatingMissingPet() throws Exception {
        mvc.perform(put("/pets/999").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(petValido())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("Pet não encontrado."));
    }

    // ---------- DELETE /pets/{id} ----------

    @Test
    void deletesPetAndReturns204() throws Exception {
        cadastrarSeed();

        mvc.perform(delete("/pets/3")).andExpect(status().isNoContent());

        mvc.perform(get("/pets/3")).andExpect(status().isNotFound());
        mvc.perform(get("/pets")).andExpect(jsonPath("$", hasSize(9)));
    }

    @Test
    void returns404WhenDeletingMissingPet() throws Exception {
        mvc.perform(delete("/pets/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("Pet não encontrado."));
    }

    // ---------- comportamento geral ----------

    @Test
    void returns404InTheStandardFormatForUnknownRoutes() throws Exception {
        mvc.perform(get("/nao-existe"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.campos").value(empty()));
    }

    @Test
    void allowsTheFrontOriginInCorsPreflight() throws Exception {
        mvc.perform(options("/pets/1")
                        .header("Origin", ORIGEM_DO_FRONT)
                        .header("Access-Control-Request-Method", "PUT")
                        .header("Access-Control-Request-Headers", "Content-Type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", ORIGEM_DO_FRONT));
    }

    @Test
    void blocksOtherOriginsInCorsPreflight() throws Exception {
        mvc.perform(options("/pets")
                        .header("Origin", "http://site-malicioso.com")
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isForbidden());
    }

    @Test
    void sendsCorsHeaderOnRealResponsesToo() throws Exception {
        mvc.perform(get("/pets").header("Origin", ORIGEM_DO_FRONT))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", ORIGEM_DO_FRONT));
    }
}
