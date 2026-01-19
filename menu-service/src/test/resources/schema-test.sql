-- menu-service/src/test/resources/schema-test.sql
DROP TABLE IF EXISTS menu_item_addons;
DROP TABLE IF EXISTS menu_item_variants;
DROP TABLE IF EXISTS menu_items;
DROP TABLE IF EXISTS menu_categories;

CREATE TABLE menu_categories (
    id UUID PRIMARY KEY,
    restaurant_id UUID NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    image_url VARCHAR(500),
    display_order INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE menu_items (
    id UUID PRIMARY KEY,
    category_id UUID NOT NULL,
    restaurant_id UUID NOT NULL,
    name VARCHAR(200) NOT NULL,
    description VARCHAR(1000),
    price DECIMAL(10,2) NOT NULL,
    discount_percentage DECIMAL(5,2) DEFAULT 0,
    final_price DECIMAL(10,2),
    image_url VARCHAR(500),
    preparation_time INT,
    calories INT,
    is_vegetarian BOOLEAN DEFAULT FALSE,
    is_vegan BOOLEAN DEFAULT FALSE,
    is_gluten_free BOOLEAN DEFAULT FALSE,
    spice_level INT DEFAULT 0,
    is_available BOOLEAN DEFAULT TRUE,
    is_featured BOOLEAN DEFAULT FALSE,
    display_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES menu_categories(id) ON DELETE CASCADE
);

CREATE TABLE menu_item_variants (
    id UUID PRIMARY KEY,
    menu_item_id UUID NOT NULL,
    name VARCHAR(100) NOT NULL,
    price_adjustment DECIMAL(10,2) DEFAULT 0,
    is_available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (menu_item_id) REFERENCES menu_items(id) ON DELETE CASCADE
);

CREATE TABLE menu_item_addons (
    id UUID PRIMARY KEY,
    menu_item_id UUID NOT NULL,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    is_available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (menu_item_id) REFERENCES menu_items(id) ON DELETE CASCADE
);

CREATE INDEX idx_categories_restaurant ON menu_categories(restaurant_id);
CREATE INDEX idx_items_category ON menu_items(category_id);
CREATE INDEX idx_items_restaurant ON menu_items(restaurant_id);