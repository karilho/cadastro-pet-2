package com.devmagro.cadastropet;

import com.devmagro.cadastropet.controller.PetController;
import com.devmagro.cadastropet.exception.ErroDeBancoException;
import com.devmagro.cadastropet.repository.FiltroPet;
import com.devmagro.cadastropet.service.PetService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.SQLException;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Só a camada web (@WebMvcTest): o service é simulado para forçar um erro que não deveria acontecer.
 */
@WebMvcTest(PetController.class)
@DisplayName("Erro inesperado na API")
class PetControllerErroInternoTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private PetService servico;

    @Test
    void returnsGeneric500WithoutLeakingInternalDetails() throws Exception {
        when(servico.buscar(any(FiltroPet.class))).thenThrow(
                new ErroDeBancoException("buscar pets", new SQLException("relation \"pet\" does not exist")));

        mvc.perform(get("/pets"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.erro").value("Erro interno"))
                .andExpect(jsonPath("$.mensagem").value("Erro interno. Tente novamente mais tarde."))
                .andExpect(content().string(not(containsString("relation"))))
                .andExpect(content().string(not(containsString("SQLException"))))
                .andExpect(content().string(not(containsString("at com.devmagro"))));
    }
}
