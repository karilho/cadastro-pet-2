package com.devmagro.cadastropet.repository.jpa;

import com.devmagro.cadastropet.repository.FiltroPet;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Transforma o FiltroPet em uma consulta, adicionando só os filtros preenchidos.
 * Texto usa a função sem_acento() do schema.sql, igual ao repositório JDBC.
 */
final class FiltroComoSpecification {

    private FiltroComoSpecification() {
    }

    static Specification<PetEntity> de(FiltroPet filtro) {
        return (raiz, consulta, cb) -> {
            List<Predicate> condicoes = new ArrayList<>();

            if (filtro.tipo() != null) condicoes.add(cb.equal(raiz.get("tipo"), filtro.tipo()));
            if (filtro.sexo() != null) condicoes.add(cb.equal(raiz.get("sexo"), filtro.sexo()));
            if (temTexto(filtro.nome())) condicoes.add(contem(cb, raiz.get("nomeCompleto"), filtro.nome()));
            if (temTexto(filtro.raca())) condicoes.add(contem(cb, raiz.get("raca"), filtro.raca()));
            if (temTexto(filtro.endereco())) {
                condicoes.add(cb.or(
                        contem(cb, raiz.get("cidade"), filtro.endereco()),
                        contem(cb, raiz.get("rua"), filtro.endereco())));
            }
            adicionarFaixa(cb, condicoes, raiz.get("idade"), filtro.idadeMin(), filtro.idadeMax());
            adicionarFaixa(cb, condicoes, raiz.get("peso"), filtro.pesoMin(), filtro.pesoMax());

            consulta.orderBy(cb.asc(raiz.get("id")));
            return cb.and(condicoes.toArray(Predicate[]::new));
        };
    }

    private static Predicate contem(CriteriaBuilder cb, Expression<String> coluna, String trecho) {
        Expression<String> padrao = cb.concat(cb.concat("%", cb.function("sem_acento", String.class, cb.literal(trecho))), "%");
        return cb.like(cb.function("sem_acento", String.class, coluna), padrao);
    }

    private static void adicionarFaixa(CriteriaBuilder cb, List<Predicate> condicoes, Expression<BigDecimal> coluna,
                                       BigDecimal minimo, BigDecimal maximo) {
        if (minimo != null) condicoes.add(cb.greaterThanOrEqualTo(coluna, minimo));
        if (maximo != null) condicoes.add(cb.lessThanOrEqualTo(coluna, maximo));
    }

    private static boolean temTexto(String texto) {
        return texto != null && !texto.isBlank();
    }
}
