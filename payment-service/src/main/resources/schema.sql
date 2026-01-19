-- payment-service/src/main/resources/schema.sql

CREATE TABLE IF NOT EXISTS payments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL,
    customer_id UUID NOT NULL,

    -- Valores
    amount DECIMAL(10, 2) NOT NULL,

    -- Método de pagamento
    payment_method VARCHAR(30) NOT NULL,

    -- Status
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',

    -- Dados do pagamento
    transaction_id VARCHAR(100),
    gateway_response TEXT,

    -- PIX
    pix_code TEXT,
    pix_qr_code TEXT,
    pix_expiration TIMESTAMP,

    -- Cartão (dados mascarados)
    card_last_digits VARCHAR(4),
    card_brand VARCHAR(20),

    -- Timestamps
    paid_at TIMESTAMP,
    refunded_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices
CREATE INDEX IF NOT EXISTS idx_payments_order ON payments(order_id);
CREATE INDEX IF NOT EXISTS idx_payments_customer ON payments(customer_id);
CREATE INDEX IF NOT EXISTS idx_payments_status ON payments(status);