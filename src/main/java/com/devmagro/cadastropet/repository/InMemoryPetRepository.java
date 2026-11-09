package com.devmagro.cadastropet.repository;

import com.devmagro.cadastropet.model.Pet;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Guarda os pets em memória. Os dados somem quando a aplicação reinicia.
 * Era a implementação do Passo 1. Continua disponível com o profile "memoria"
 * (rode com --spring.profiles.active=memoria e sem banco).
 */
@Repository
@Profile("memoria")
public class InMemoryPetRepository implements PetRepository {

    private final Map<Long, Pet> pets = new ConcurrentHashMap<>();
    private final AtomicLong proximoId = new AtomicLong(1);

    @Override
    public Pet salvar(Pet pet) {
        Pet salvo = pet.id() == null ? pet.comId(proximoId.getAndIncrement()) : pet;
        pets.put(salvo.id(), salvo);
        return salvo;
    }

    @Override
    public Optional<Pet> buscarPorId(long id) {
        return Optional.ofNullable(pets.get(id));
    }

    @Override
    public List<Pet> buscar(FiltroPet filtro) {
        return pets.values().stream()
                .filter(pet -> atende(pet, filtro))
                .sorted(Comparator.comparing(Pet::id))
                .toList();
    }

    @Override
    public boolean excluir(long id) {
        return pets.remove(id) != null;
    }

    private boolean atende(Pet pet, FiltroPet filtro) {
        return (filtro.tipo() == null || pet.tipo() == filtro.tipo())
                && (filtro.sexo() == null || pet.sexo() == filtro.sexo())
                && contem(pet.nomeCompleto(), filtro.nome())
                && contem(pet.raca(), filtro.raca())
                && (vazio(filtro.endereco())
                        || contem(pet.endereco().cidade(), filtro.endereco())
                        || contem(pet.endereco().rua(), filtro.endereco()))
                && noIntervalo(pet.idade(), filtro.idadeMin(), filtro.idadeMax())
                && noIntervalo(pet.peso(), filtro.pesoMin(), filtro.pesoMax());
    }

    /** Pet com campo nulo nunca atende a um filtro daquele campo. */
    private boolean contem(String valor, String trecho) {
        if (vazio(trecho)) {
            return true;
        }
        return valor != null && padronizar(valor).contains(padronizar(trecho));
    }

    private boolean noIntervalo(BigDecimal valor, BigDecimal minimo, BigDecimal maximo) {
        if (minimo == null && maximo == null) {
            return true;
        }
        if (valor == null) {
            return false;
        }
        return (minimo == null || valor.compareTo(minimo) >= 0)
                && (maximo == null || valor.compareTo(maximo) <= 0);
    }

    private boolean vazio(String texto) {
        return texto == null || texto.isBlank();
    }

    /** Ignora maiúsculas, minúsculas e acentos. */
    private String padronizar(String texto) {
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
    }
}
