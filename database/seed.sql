-- Os 10 pets de exemplo (docs/03-modelo-de-dados.md, seção 8). Ids 1 a 10.
INSERT INTO pet (nome_completo, tipo, sexo, numero_casa, cidade, rua, idade_anos, peso_kg, raca) VALUES
    ('Rex Almeida',        'CACHORRO', 'MACHO', '123', 'Curitiba',             'Rua das Flores',    2,   5.00,  'Vira-lata'),
    ('Florzinha da Silva', 'GATO',     'FEMEA', '456', 'Seilândia',            'Rua Azul',          6,   5.00,  'Siamês'),
    ('Thor Oliveira',      'CACHORRO', 'MACHO', '78',  'Curitiba',             'Avenida Brasil',    4,   32.50, 'Labrador'),
    ('Mimi Santos',        'GATO',     'FEMEA', '910', 'São José dos Pinhais', 'Rua Esmeralda',     0.5, 1.20,  'Persa'),
    ('Bob Costa',          'CACHORRO', 'MACHO', NULL,  'Pinhais',              'Rua do Sol',        0.8, 3.50,  'Poodle'),
    ('Luna Ferreira',      'CACHORRO', 'FEMEA', '205', 'Curitiba',             'Rua das Palmeiras', 7,   24.00, 'Border Collie'),
    ('Garfield Souza',     'GATO',     'MACHO', '33',  'Colombo',              'Rua Verde',         9,   6.80,  'Vira-lata'),
    ('Nina Rocha',         'GATO',     'FEMEA', '12',  'Araucária',            'Rua dos Pinheiros', 1,   3.10,  NULL),
    ('Max Pereira',        'CACHORRO', 'MACHO', '540', 'Curitiba',             'Rua das Acácias',   12,  45.00, 'Pastor Alemão'),
    ('Pipoca Lima',        'CACHORRO', 'FEMEA', '88',  'Colombo',              'Rua Central',       3,   8.50,  'Vira-lata');
