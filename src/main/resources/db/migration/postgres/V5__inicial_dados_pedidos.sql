-- Inserção de dados na tabela pedido
INSERT INTO pedido (cliente_id, status, preco_total, status_confirmacao_pagamento, data_criacao, numero_pedido)
VALUES (null, 'PENDENTE', 51.0, 'PENDENTE', '2024-09-15 09:30:00', 100000);

INSERT INTO pedido (cliente_id, status, preco_total, status_confirmacao_pagamento, data_criacao, numero_pedido)
VALUES (1, 'PENDENTE', 67.0, 'PENDENTE', '2024-09-15 15:00:00', 200000);

-- Inserção de dados na tabela item_pedido
INSERT INTO item_pedido (produto_id, preco_unitario, quantidade, observacao, preco_total, pedido_id)
VALUES (1, 25.0, 1, null, 25.0, 1);

INSERT INTO item_pedido (produto_id, preco_unitario, quantidade, observacao, preco_total, pedido_id)
VALUES (4, 12.0, 1, null, 12.0, 1);

INSERT INTO item_pedido (produto_id, preco_unitario, quantidade, observacao, preco_total, pedido_id)
VALUES (7, 7.0, 1, null, 7.0, 1);

INSERT INTO item_pedido (produto_id, preco_unitario, quantidade, observacao, preco_total, pedido_id)
VALUES (10, 7.0, 1, null, 7.0, 1);

INSERT INTO item_pedido (produto_id, preco_unitario, quantidade, observacao, preco_total, pedido_id)
VALUES (2, 30.0, 2, null, 60.0, 2);

INSERT INTO item_pedido (produto_id, preco_unitario, quantidade, observacao, preco_total, pedido_id)
VALUES (7, 7.0, 1, null, 7.0, 2);