package com.devmagro.cadastropet.controller;

import com.devmagro.cadastropet.dto.PetRequest;
import com.devmagro.cadastropet.dto.PetResponse;
import com.devmagro.cadastropet.model.Pet;
import com.devmagro.cadastropet.model.SexoPet;
import com.devmagro.cadastropet.model.TipoPet;
import com.devmagro.cadastropet.repository.FiltroPet;
import com.devmagro.cadastropet.service.PetService;
import com.devmagro.cadastropet.validation.Cadastro;
import jakarta.validation.groups.Default;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

/**
 * Só traduz HTTP: recebe a requisição, chama o service e devolve o status certo.
 * Nenhuma regra de negócio e nenhum acesso a dados mora aqui.
 */
@RestController
@RequestMapping("/pets")
public class PetController {

    private final PetService servico;

    public PetController(PetService servico) {
        this.servico = servico;
    }

    @PostMapping
    public ResponseEntity<PetResponse> cadastrar(
            @RequestBody @Validated({Default.class, Cadastro.class}) PetRequest dados) {
        Pet criado = servico.cadastrar(dados);
        URI local = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(criado.id())
                .toUri();
        return ResponseEntity.created(local).body(PetResponse.de(criado));
    }

    @GetMapping
    public List<PetResponse> buscar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String raca,
            @RequestParam(required = false) String endereco,
            @RequestParam(required = false) TipoPet tipo,
            @RequestParam(required = false) SexoPet sexo,
            @RequestParam(required = false) BigDecimal idadeMin,
            @RequestParam(required = false) BigDecimal idadeMax,
            @RequestParam(required = false) BigDecimal pesoMin,
            @RequestParam(required = false) BigDecimal pesoMax) {
        FiltroPet filtro = new FiltroPet(nome, raca, endereco, tipo, sexo, idadeMin, idadeMax, pesoMin, pesoMax);
        return servico.buscar(filtro).stream().map(PetResponse::de).toList();
    }

    @GetMapping("/{id}")
    public PetResponse buscarPorId(@PathVariable long id) {
        return PetResponse.de(servico.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public PetResponse alterar(@PathVariable long id, @RequestBody @Validated PetRequest dados) {
        return PetResponse.de(servico.alterar(id, dados));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable long id) {
        servico.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
