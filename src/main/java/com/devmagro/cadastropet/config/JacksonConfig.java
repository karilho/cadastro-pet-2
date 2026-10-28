package com.devmagro.cadastropet.config;

import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.type.LogicalType;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    /**
     * Select vazio chega como "" no JSON. Para enum, isso conta como "não informado" (null).
     * Assim o erro de tipo e sexo sai junto com os outros erros de validação, em vez de
     * derrubar a leitura do corpo inteiro.
     */
    @Bean
    Jackson2ObjectMapperBuilderCustomizer textoVazioEmEnumViraNulo() {
        return construtor -> construtor.postConfigurer(mapper ->
                mapper.coercionConfigFor(LogicalType.Enum)
                        .setCoercion(CoercionInputShape.EmptyString, CoercionAction.AsNull));
    }
}
