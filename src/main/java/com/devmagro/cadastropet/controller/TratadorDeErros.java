package com.devmagro.cadastropet.controller;

import com.devmagro.cadastropet.dto.CampoComErro;
import com.devmagro.cadastropet.dto.ErroResponse;
import com.devmagro.cadastropet.exception.PetNaoEncontradoException;
import com.devmagro.cadastropet.exception.RegraDeNegocioException;
import com.devmagro.cadastropet.validation.Mensagens;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Único lugar que transforma erro em resposta HTTP. Todo erro sai no mesmo formato.
 */
@RestControllerAdvice
public class TratadorDeErros {

    private static final Logger LOG = LoggerFactory.getLogger(TratadorDeErros.class);

    private static final String DADOS_INVALIDOS = "Dados inválidos";
    private static final String NAO_ENCONTRADO = "Não encontrado";

    private static final Map<String, String> MENSAGEM_POR_CAMPO = Map.of(
            "tipo", Mensagens.TIPO_INVALIDO,
            "sexo", Mensagens.SEXO_INVALIDO,
            "idade", Mensagens.IDADE_INVALIDA,
            "peso", Mensagens.PESO_INVALIDO);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> validacao(MethodArgumentNotValidException ex, HttpServletRequest requisicao) {
        Map<String, String> mensagemPorCampo = new TreeMap<>();
        for (FieldError erro : ex.getBindingResult().getFieldErrors()) {
            mensagemPorCampo.putIfAbsent(erro.getField(), erro.getDefaultMessage());
        }
        List<CampoComErro> campos = mensagemPorCampo.entrySet().stream()
                .map(entrada -> new CampoComErro(entrada.getKey(), entrada.getValue()))
                .toList();
        return resposta(HttpStatus.BAD_REQUEST, DADOS_INVALIDOS, "Um ou mais campos são inválidos.", campos, requisicao);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResponse> corpoIlegivel(HttpMessageNotReadableException ex, HttpServletRequest requisicao) {
        Throwable causa = ex.getCause();
        if (causa instanceof MismatchedInputException erroDeFormato) {
            String campo = erroDeFormato.getPath().stream()
                    .map(referencia -> referencia.getFieldName())
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining("."));
            if (!campo.isEmpty()) {
                String mensagem = MENSAGEM_POR_CAMPO.getOrDefault(campo, "Valor inválido para o campo '" + campo + "'.");
                return resposta(HttpStatus.BAD_REQUEST, DADOS_INVALIDOS, "Um ou mais campos são inválidos.",
                        List.of(new CampoComErro(campo, mensagem)), requisicao);
            }
        }
        return resposta(HttpStatus.BAD_REQUEST, DADOS_INVALIDOS, "JSON malformado ou com formato inválido.", List.of(), requisicao);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroResponse> parametroInvalido(MethodArgumentTypeMismatchException ex, HttpServletRequest requisicao) {
        String nome = ex.getName();
        String mensagem = "id".equals(nome)
                ? "O id deve ser um número."
                : MENSAGEM_POR_CAMPO.getOrDefault(nome, "Valor inválido para o parâmetro '" + nome + "'.");
        return resposta(HttpStatus.BAD_REQUEST, DADOS_INVALIDOS, mensagem,
                List.of(new CampoComErro(nome, mensagem)), requisicao);
    }

    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<ErroResponse> regraDeNegocio(RegraDeNegocioException ex, HttpServletRequest requisicao) {
        return resposta(HttpStatus.BAD_REQUEST, DADOS_INVALIDOS, ex.getMessage(),
                List.of(new CampoComErro(ex.getCampo(), ex.getMessage())), requisicao);
    }

    @ExceptionHandler(PetNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> naoEncontrado(PetNaoEncontradoException ex, HttpServletRequest requisicao) {
        return resposta(HttpStatus.NOT_FOUND, NAO_ENCONTRADO, ex.getMessage(), List.of(), requisicao);
    }

    @ExceptionHandler({
            NoResourceFoundException.class,
            HttpRequestMethodNotSupportedException.class,
            HttpMediaTypeNotSupportedException.class})
    public ResponseEntity<ErroResponse> erroDeProtocolo(Exception ex, HttpServletRequest requisicao) {
        HttpStatusCode status = ((ErrorResponse) ex).getStatusCode();
        String mensagem = switch (status.value()) {
            case 404 -> "Rota inexistente.";
            case 405 -> "Método não permitido para esta rota.";
            default -> "Use o Content-Type application/json.";
        };
        String erro = status.value() == 404 ? NAO_ENCONTRADO : "Requisição inválida";
        return resposta(HttpStatus.valueOf(status.value()), erro, mensagem, List.of(), requisicao);
    }

    /** Qualquer coisa não prevista. O detalhe vai para o log, nunca para o cliente. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> inesperado(Exception ex, HttpServletRequest requisicao) {
        LOG.error("Erro inesperado em {} {}", requisicao.getMethod(), requisicao.getRequestURI(), ex);
        return resposta(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno",
                "Erro interno. Tente novamente mais tarde.", List.of(), requisicao);
    }

    private ResponseEntity<ErroResponse> resposta(
            HttpStatus status, String erro, String mensagem, List<CampoComErro> campos, HttpServletRequest requisicao) {
        ErroResponse corpo = ErroResponse.de(status.value(), erro, mensagem, campos, requisicao.getRequestURI());
        return ResponseEntity.status(status).body(corpo);
    }
}
