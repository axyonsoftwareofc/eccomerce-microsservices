-- restaurant-service/src/test/resources/schema-test.sql

CREATE TABLE IF NOT EXISTS restaurant_categories (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    icon_url VARCHAR(500),
    is_active BOOLEAN DEFAULT true,
    display_order INTEGER DEFAULT 0
);

CREATE TABLE IF NOT EXISTS restaurants (
    id UUID PRIMARY KEY,
    owner_id UUID NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    logo_url VARCHAR(500),
    banner_url VARCHAR(500),
    phone VARCHAR(20),
    email VARCHAR(100),
    address_street VARCHAR(200),
    address_number VARCHAR(20),
    address_complement VARCHAR(100),
    address_neighborhood VARCHAR(100),
    address_city VARCHAR(100),
    address_state VARCHAR(2),
    address_zip_code VARCHAR(10),
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    delivery_radius_km DECIMAL(5, 2) DEFAULT 5.00,
    min_order_value DECIMAL(10, 2) DEFAULT 0.00,
    delivery_fee DECIMAL(10, 2) DEFAULT 0.00,
    avg_preparation_time INTEGER DEFAULT 30,
    avg_delivery_time INTEGER DEFAULT 30,
    opens_at TIME,
    closes_at TIME,
    is_open_on_weekends BOOLEAN DEFAULT true,
    category_id UUID,
    status VARCHAR(20) DEFAULT 'PENDING_APPROVAL',
    is_open BOOLEAN DEFAULT false,
    is_accepting_orders BOOLEAN DEFAULT true,
    rating DECIMAL(3, 2) DEFAULT 0.00,
    total_reviews INTEGER DEFAULT 0,
    total_orders INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO restaurant_categories (id, name, icon_url, display_order) VALUES
    (RANDOM_UUID(), 'Pizzaria', '🍕', 1),
    (RANDOM_UUID(), 'Hamburgueria', '🍔', 2);