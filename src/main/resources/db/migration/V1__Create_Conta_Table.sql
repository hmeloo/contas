CREATE TABLE contas (
    id SERIAL PRIMARY KEY,
    data_vencimento DATE NOT NULL,
    data_pagamento DATE,
    valor DECIMAL(10, 2) NOT NULL,
    descricao TEXT,
    situacao VARCHAR(50) NOT NULL
);
