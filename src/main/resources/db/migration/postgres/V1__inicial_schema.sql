CREATE TABLE IF NOT EXISTS produto (
    id BIGSERIAL,
    descricao VARCHAR(255) NOT NULL,
    preco DECIMAL(19,2) NOT NULL,
    categoria VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS cliente (
    id BIGSERIAL,
    nome VARCHAR(255) NOT NULL,
    documento VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS pedido (
    id BIGSERIAL,
    cliente_id BIGINT,
    status VARCHAR(255) NOT NULL,
    preco_total DECIMAL(19,2) NOT NULL,
    transacao_pagamento_id VARCHAR(255),
    status_confirmacao_pagamento VARCHAR(255) NOT NULL,
    data_criacao TIMESTAMP NOT NULL,
    numero_pedido DECIMAL(19,1) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (cliente_id) REFERENCES cliente(id)
);

CREATE TABLE IF NOT EXISTS item_pedido (
    id BIGSERIAL,
    produto_id BIGINT NOT NULL,
    preco_unitario DECIMAL(19,2) NOT NULL,
    quantidade INT NOT NULL,
    observacao TEXT DEFAULT NULL,
    preco_total DECIMAL(19,2) NOT NULL,
    pedido_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (produto_id) REFERENCES produto(id),
    FOREIGN KEY (pedido_id) REFERENCES pedido(id)
);