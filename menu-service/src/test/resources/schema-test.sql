-- menu-service/src/test/resources/schema-test.sql

CREATE TABLE IF NOT EXISTS menu_categories (
    id UUID PRIMARY KEY,
    restaurant_id UUID NOT NULL,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(200),
    image_url VARCHAR(500),
    is_active BOOLEAN DEFAULT true,
    display_order INTEGER DEFAULT 0,
    available_from TIME,
    available_until TIME,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS menu_items (
    id UUID PRIMARY KEY,
    restaurant_id UUID NOT NULL,
    category_id UUID,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    image_url VARCHAR(500),
    price DECIMAL(10, 2) NOT NULL,
    original_price DECIMAL(10, 2),
    discount_percentage DECIMAL(5, 2),
    preparation_time INTEGER,
    serves INTEGER,
    calories INTEGER,
    is_vegetarian BOOLEAN DEFAULT false,
    is_vegan BOOLEAN DEFAULT false,
    is_gluten_free BOOLEAN DEFAULT false,
    is_spicy BOOLEAN DEFAULT false,
    spicy_level INTEGER,
    is_available BOOLEAN DEFAULT true,
    is_featured BOOLEAN DEFAULT false,
    is_best_seller BOOLEAN DEFAULT false,
    available_from TIME,
    available_until TIME,
    stock_quantity INTEGER,
    max_quantity_per_order INTEGER,
    display_order INTEGER DEFAULT 0,
    total_orders INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS menu_item_addons (
    id UUID PRIMARY KEY,
    menu_item_id UUID NOT NULL,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(200),
    price DECIMAL(10, 2) NOT NULL,
    is_available BOOLEAN DEFAULT true,
    max_quantity INTEGER DEFAULT 1,
    is_required BOOLEAN DEFAULT false,
    display_order INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS menu_item_variants (
    id UUID PRIMARY KEY,
    menu_item_id UUID NOT NULL,
    name VARCHAR(30) NOT NULL,
    variant_type VARCHAR(20) NOT NULL,
    price DECIMAL(10, 2),
    price_modifier DECIMAL(10, 2),
    serves INTEGER,
    is_default BOOLEAN DEFAULT false,
    is_available BOOLEAN DEFAULT true,
    display_order INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);