package com.devmagro.cadastropet.repository;

import com.devmagro.cadastropet.exception.ErroDeBancoException;
import com.devmagro.cadastropet.model.Endereco;
import com.devmagro.cadastropet.model.Pet;
import com.devmagro.cadastropet.model.SexoPet;
import com.devmagro.cadastropet.model.TipoPet;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementação com JDBC puro. Todo valor vai por parâmetro (?), nunca concatenado no SQL.
 * Busca de texto usa a função sem_acento() criada no schema.sql.
 * getBigDecimal devolve null para coluna NULL (getInt e getDouble devolveriam 0).
 */
@Repository
@Profile("!memoria")
public class JdbcPetRepository implements PetRepository {

    private static final String COLUNAS =
            "id, nome_completo, tipo, sexo, numero_casa, cidade, rua, idade_anos, peso_kg, raca, data_cadastro";

    private static final String INSERIR = """
            INSERT INTO pet (nome_completo, tipo, sexo, numero_casa, cidade, rua, idade_anos, peso_kg, raca, data_cadastro)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

    // tipo, sexo, id e data de cadastro nunca são alterados
    private static final String ALTERAR = """
            UPDATE pet
               SET nome_completo = ?, numero_casa = ?, cidade = ?, rua = ?, idade_anos = ?, peso_kg = ?, raca = ?
             WHERE id = ?
            """;

    private static final String CONTEM = " LIKE '%' || sem_acento(?) || '%'";

    private final DataSource dataSource;

    public JdbcPetRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Pet salvar(Pet pet) {
        return pet.id() == null ? inserir(pet) : alterar(pet);
    }

    private Pet inserir(Pet pet) {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement comando = conexao.prepareStatement(INSERIR, Statement.RETURN_GENERATED_KEYS)) {
            comando.setString(1, pet.nomeCompleto());
            comando.setString(2, pet.tipo().name());
            comando.setString(3, pet.sexo().name());
            comando.setString(4, pet.endereco().numeroCasa());
            comando.setString(5, pet.endereco().cidade());
            comando.setString(6, pet.endereco().rua());
            comando.setBigDecimal(7, pet.idade());
            comando.setBigDecimal(8, pet.peso());
            comando.setString(9, pet.raca());
            comando.setTimestamp(10, Timestamp.valueOf(pet.dataCadastro()));
            comando.executeUpdate();

            try (ResultSet chaves = comando.getGeneratedKeys()) {
                chaves.next();
                return pet.comId(chaves.getLong("id"));
            }
        } catch (SQLException erro) {
            throw new ErroDeBancoException("cadastrar pet", erro);
        }
    }

    /** Transação explícita: ou altera tudo, ou nada. */
    private Pet alterar(Pet pet) {
        try (Connection conexao = dataSource.getConnection()) {
            conexao.setAutoCommit(false);
            try (PreparedStatement comando = conexao.prepareStatement(ALTERAR)) {
                comando.setString(1, pet.nomeCompleto());
                comando.setString(2, pet.endereco().numeroCasa());
                comando.setString(3, pet.endereco().cidade());
                comando.setString(4, pet.endereco().rua());
                comando.setBigDecimal(5, pet.idade());
                comando.setBigDecimal(6, pet.peso());
                comando.setString(7, pet.raca());
                comando.setLong(8, pet.id());
                comando.executeUpdate();
                conexao.commit();
                return pet;
            } catch (SQLException erro) {
                conexao.rollback();
                throw erro;
            } finally {
                conexao.setAutoCommit(true);
            }
        } catch (SQLException erro) {
            throw new ErroDeBancoException("alterar pet", erro);
        }
    }

    @Override
    public Optional<Pet> buscarPorId(long id) {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement comando = conexao.prepareStatement("SELECT " + COLUNAS + " FROM pet WHERE id = ?")) {
            comando.setLong(1, id);
            try (ResultSet linhas = comando.executeQuery()) {
                return linhas.next() ? Optional.of(paraPet(linhas)) : Optional.empty();
            }
        } catch (SQLException erro) {
            throw new ErroDeBancoException("buscar pet", erro);
        }
    }

    @Override
    public List<Pet> buscar(FiltroPet filtro) {
        StringBuilder sql = new StringBuilder("SELECT " + COLUNAS + " FROM pet WHERE 1 = 1");
        List<Object> valores = new ArrayList<>();

        if (filtro.tipo() != null) { sql.append(" AND tipo = ?"); valores.add(filtro.tipo().name()); }
        if (filtro.sexo() != null) { sql.append(" AND sexo = ?"); valores.add(filtro.sexo().name()); }
        if (temTexto(filtro.nome())) { sql.append(" AND sem_acento(nome_completo)").append(CONTEM); valores.add(filtro.nome()); }
        if (temTexto(filtro.raca())) { sql.append(" AND sem_acento(raca)").append(CONTEM); valores.add(filtro.raca()); }
        if (temTexto(filtro.endereco())) {
            sql.append(" AND (sem_acento(cidade)").append(CONTEM).append(" OR sem_acento(rua)").append(CONTEM).append(")");
            valores.add(filtro.endereco());
            valores.add(filtro.endereco());
        }
        // comparação com NULL nunca é verdadeira: pet sem idade não atende idadeMin, como pede a regra
        if (filtro.idadeMin() != null) { sql.append(" AND idade_anos >= ?"); valores.add(filtro.idadeMin()); }
        if (filtro.idadeMax() != null) { sql.append(" AND idade_anos <= ?"); valores.add(filtro.idadeMax()); }
        if (filtro.pesoMin() != null) { sql.append(" AND peso_kg >= ?"); valores.add(filtro.pesoMin()); }
        if (filtro.pesoMax() != null) { sql.append(" AND peso_kg <= ?"); valores.add(filtro.pesoMax()); }
        sql.append(" ORDER BY id");

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement comando = conexao.prepareStatement(sql.toString())) {
            for (int i = 0; i < valores.size(); i++) {
                comando.setObject(i + 1, valores.get(i));
            }
            try (ResultSet linhas = comando.executeQuery()) {
                List<Pet> pets = new ArrayList<>();
                while (linhas.next()) {
                    pets.add(paraPet(linhas));
                }
                return pets;
            }
        } catch (SQLException erro) {
            throw new ErroDeBancoException("buscar pets", erro);
        }
    }

    @Override
    public boolean excluir(long id) {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement comando = conexao.prepareStatement("DELETE FROM pet WHERE id = ?")) {
            comando.setLong(1, id);
            return comando.executeUpdate() > 0;
        } catch (SQLException erro) {
            throw new ErroDeBancoException("excluir pet", erro);
        }
    }

    private Pet paraPet(ResultSet linha) throws SQLException {
        return new Pet(
                linha.getLong("id"),
                linha.getString("nome_completo"),
                TipoPet.valueOf(linha.getString("tipo")),
                SexoPet.valueOf(linha.getString("sexo")),
                new Endereco(linha.getString("numero_casa"), linha.getString("cidade"), linha.getString("rua")),
                linha.getBigDecimal("idade_anos"),
                linha.getBigDecimal("peso_kg"),
                linha.getString("raca"),
                linha.getTimestamp("data_cadastro").toLocalDateTime());
    }

    private boolean temTexto(String texto) {
        return texto != null && !texto.isBlank();
    }
}
