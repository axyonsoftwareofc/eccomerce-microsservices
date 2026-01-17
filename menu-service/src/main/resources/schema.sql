-- src/main/resources/schema.sql

CREATE TABLE IF NOT EXISTS menu (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    price DECIMAL(12, 2) NOT NULL,
    sku VARCHAR(50) NOT NULL UNIQUE,
    category_id UUID,
    is_active BOOLEAN DEFAULT true,
    image_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices
CREATE INDEX IF NOT EXISTS idx_menu_sku ON menu(sku);
CREATE INDEX IF NOT EXISTS idx_menu_active ON menu(is_active);
CREATE INDEX IF NOT EXISTS idx_menu_category ON menu(category_id);