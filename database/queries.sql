-- Consultas do Passo 2. Resultado esperado em cada comentário (docs/03, "Resultados esperados").

-- Listar todos: 10 linhas
SELECT * FROM pet ORDER BY id;

-- Nome contendo "flor", qualquer caixa: id 2
SELECT id, nome_completo FROM pet WHERE sem_acento(nome_completo) LIKE '%' || sem_acento('flor') || '%' ORDER BY id;

-- Nome contendo "silva": id 2
SELECT id, nome_completo FROM pet WHERE sem_acento(nome_completo) LIKE '%' || sem_acento('silva') || '%' ORDER BY id;

-- Raça contendo "siames" (sem acento): id 2
SELECT id, raca FROM pet WHERE sem_acento(raca) LIKE '%' || sem_acento('siames') || '%' ORDER BY id;

-- Raça contendo "pastor": id 9
SELECT id, raca FROM pet WHERE sem_acento(raca) LIKE '%' || sem_acento('pastor') || '%' ORDER BY id;

-- Raça igual a "Vira-lata": ids 1, 7, 10
SELECT id, raca FROM pet WHERE raca = 'Vira-lata' ORDER BY id;

-- Endereço contendo "flor" (cidade ou rua): id 1
SELECT id, rua, cidade FROM pet
WHERE sem_acento(cidade) LIKE '%' || sem_acento('flor') || '%'
   OR sem_acento(rua) LIKE '%' || sem_acento('flor') || '%'
ORDER BY id;

-- Cidade igual a "Colombo": ids 7, 10
SELECT id, cidade FROM pet WHERE cidade = 'Colombo' ORDER BY id;

-- Idade de 1 a 5 anos (inclusive): ids 1, 3, 8, 10
SELECT id, idade_anos FROM pet WHERE idade_anos BETWEEN 1 AND 5 ORDER BY id;

-- Peso de até 5kg (inclusive): ids 1, 2, 4, 5, 8
SELECT id, peso_kg FROM pet WHERE peso_kg <= 5 ORDER BY id;

-- Gato com pelo menos 1 ano: ids 2, 7, 8
SELECT id FROM pet WHERE tipo = 'GATO' AND idade_anos >= 1 ORDER BY id;

-- Cachorro com pelo menos 20kg: ids 3, 6, 9
SELECT id FROM pet WHERE tipo = 'CACHORRO' AND peso_kg >= 20 ORDER BY id;

-- Menos de 1 ano: ids 4, 5
SELECT id FROM pet WHERE idade_anos < 1 ORDER BY id;

-- Quantidade por tipo: CACHORRO 6, GATO 4
SELECT tipo, count(*) AS quantidade FROM pet GROUP BY tipo ORDER BY tipo;

-- Peso médio por tipo e sexo: CACHORRO/FEMEA 16.25, CACHORRO/MACHO 21.50, GATO/FEMEA 3.10, GATO/MACHO 6.80
SELECT tipo, sexo, round(avg(peso_kg), 2) AS peso_medio FROM pet GROUP BY tipo, sexo ORDER BY tipo, sexo;

-- NULL não é igual a nada: a primeira devolve 0 linhas, a segunda devolve o id 5
SELECT id FROM pet WHERE numero_casa = NULL;
SELECT id FROM pet WHERE numero_casa IS NULL;

-- Índice na prática: compare os planos (em tabela pequena o PostgreSQL pode preferir varrer tudo)
EXPLAIN SELECT id FROM pet WHERE sem_acento(nome_completo) = sem_acento('Rex Almeida');
EXPLAIN SELECT id FROM pet WHERE sem_acento(nome_completo) LIKE '%rex%';
