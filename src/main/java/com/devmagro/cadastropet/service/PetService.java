package com.devmagro.cadastropet.service;

import com.devmagro.cadastropet.dto.PetRequest;
import com.devmagro.cadastropet.exception.PetNaoEncontradoException;
import com.devmagro.cadastropet.exception.RegraDeNegocioException;
import com.devmagro.cadastropet.model.Endereco;
import com.devmagro.cadastropet.model.Pet;
import com.devmagro.cadastropet.repository.FiltroPet;
import com.devmagro.cadastropet.repository.PetRepository;
import com.devmagro.cadastropet.validation.Mensagens;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class PetService {

    private final PetRepository repositorio;

    public PetService(PetRepository repositorio) {
        this.repositorio = repositorio;
    }

    public Pet cadastrar(PetRequest dados) {
        Pet novo = new Pet(
                null,
                textoOuNulo(dados.nomeCompleto()),
                dados.tipo(),
                dados.sexo(),
                enderecoDe(dados),
                dados.idade(),
                dados.peso(),
                textoOuNulo(dados.raca()),
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        return repositorio.salvar(novo);
    }

    public List<Pet> buscar(FiltroPet filtro) {
        return repositorio.buscar(filtro);
    }

    public Pet buscarPorId(long id) {
        return repositorio.buscarPorId(id).orElseThrow(PetNaoEncontradoException::new);
    }

    /** Tipo e sexo nunca mudam. Id e data de cadastro também não. */
    public Pet alterar(long id, PetRequest dados) {
        Pet atual = buscarPorId(id);

        boolean tentouMudarTipo = dados.tipo() != null && dados.tipo() != atual.tipo();
        boolean tentouMudarSexo = dados.sexo() != null && dados.sexo() != atual.sexo();
        if (tentouMudarTipo) {
            throw new RegraDeNegocioException("tipo", Mensagens.TIPO_E_SEXO_IMUTAVEIS);
        }
        if (tentouMudarSexo) {
            throw new RegraDeNegocioException("sexo", Mensagens.TIPO_E_SEXO_IMUTAVEIS);
        }

        Pet alterado = new Pet(
                atual.id(),
                textoOuNulo(dados.nomeCompleto()),
                atual.tipo(),
                atual.sexo(),
                enderecoDe(dados),
                dados.idade(),
                dados.peso(),
                textoOuNulo(dados.raca()),
                atual.dataCadastro());
        return repositorio.salvar(alterado);
    }

    public void excluir(long id) {
        if (!repositorio.excluir(id)) {
            throw new PetNaoEncontradoException();
        }
    }

    private Endereco enderecoDe(PetRequest dados) {
        return new Endereco(
                textoOuNulo(dados.endereco().numeroCasa()),
                dados.endereco().cidade().strip(),
                dados.endereco().rua().strip());
    }

    /** Texto vazio ou só com espaços vira null: o campo é opcional e ficou sem valor. */
    private String textoOuNulo(String texto) {
        return texto == null || texto.isBlank() ? null : texto.strip();
    }
}
