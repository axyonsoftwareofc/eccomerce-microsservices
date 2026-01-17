-- schema.sql

-- Categorias de restaurantes
CREATE TABLE IF NOT EXISTS restaurant_categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    icon_url VARCHAR(500),
    is_active BOOLEAN DEFAULT true,
    display_order INTEGER DEFAULT 0
);

-- Restaurantes
CREATE TABLE IF NOT EXISTS restaurants (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    owner_id UUID NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    logo_url VARCHAR(500),
    banner_url VARCHAR(500),
    phone VARCHAR(20),
    email VARCHAR(100),

    -- Endereço
    address_street VARCHAR(200),
    address_number VARCHAR(20),
    address_complement VARCHAR(100),
    address_neighborhood VARCHAR(100),
    address_city VARCHAR(100),
    address_state VARCHAR(2),
    address_zip_code VARCHAR(10),
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),

    -- Configurações de entrega
    delivery_radius_km DECIMAL(5, 2) DEFAULT 5.00,
    min_order_value DECIMAL(10, 2) DEFAULT 0.00,
    delivery_fee DECIMAL(10, 2) DEFAULT 0.00,
    avg_preparation_time INTEGER DEFAULT 30,
    avg_delivery_time INTEGER DEFAULT 30,

    -- Horário
    opens_at TIME,
    closes_at TIME,
    is_open_on_weekends BOOLEAN DEFAULT true,

    -- Categoria
    category_id UUID REFERENCES restaurant_categories(id),

    -- Status
    status VARCHAR(20) DEFAULT 'PENDING_APPROVAL',
    is_open BOOLEAN DEFAULT false,
    is_accepting_orders BOOLEAN DEFAULT true,

    -- Avaliação
    rating DECIMAL(3, 2) DEFAULT 0.00,
    total_reviews INTEGER DEFAULT 0,
    total_orders INTEGER DEFAULT 0,

    -- Timestamps
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices
CREATE INDEX IF NOT EXISTS idx_restaurants_owner ON restaurants(owner_id);
CREATE INDEX IF NOT EXISTS idx_restaurants_category ON restaurants(category_id);
CREATE INDEX IF NOT EXISTS idx_restaurants_status ON restaurants(status);
CREATE INDEX IF NOT EXISTS idx_restaurants_open ON restaurants(is_open);
CREATE INDEX IF NOT EXISTS idx_restaurants_location ON restaurants(latitude, longitude);

-- Inserir categorias padrão
INSERT INTO restaurant_categories (id, name, icon_url, display_order) VALUES
    (gen_random_uuid(), 'Pizzaria', '🍕', 1),
    (gen_random_uuid(), 'Hamburgueria', '🍔', 2),
    (gen_random_uuid(), 'Japonês', '🍣', 3),
    (gen_random_uuid(), 'Italiana', '🍝', 4),
    (gen_random_uuid(), 'Brasileira', '🍛', 5),
    (gen_random_uuid(), 'Chinesa', '🥡', 6),
    (gen_random_uuid(), 'Mexicana', '🌮', 7),
    (gen_random_uuid(), 'Doces', '🍰', 8),
    (gen_random_uuid(), 'Saudável', '🥗', 9),
    (gen_random_uuid(), 'Açaí', '🍇', 10)
ON CONFLICT DO NOTHING;