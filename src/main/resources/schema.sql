CREATE DATABASE IF NOT EXISTS eco_waste_billing;
USE eco_waste_billing;

CREATE TABLE IF NOT EXISTS admins (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS store_profiles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    store_name VARCHAR(120) NOT NULL,
    owner_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL,
    address VARCHAR(255) NOT NULL,
    login_username VARCHAR(60) NOT NULL UNIQUE,
    login_password VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS customers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    store_id BIGINT NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL,
    otp_code VARCHAR(10),
    otp_verified BOOLEAN NOT NULL DEFAULT FALSE,
    next_discount_eligible BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_customers_store
        FOREIGN KEY (store_id) REFERENCES store_profiles(id)
);

CREATE TABLE IF NOT EXISTS products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    store_id BIGINT NOT NULL,
    name VARCHAR(120) NOT NULL,
    price INT NOT NULL,
    image VARCHAR(255) NOT NULL,
    stock_quantity INT NOT NULL DEFAULT 0,
    waste_category ENUM('RECYCLABLE', 'REUSABLE', 'ECO_DISPOSAL') NOT NULL,
    CONSTRAINT fk_products_store
        FOREIGN KEY (store_id) REFERENCES store_profiles(id)
);

CREATE TABLE IF NOT EXISTS bills (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    store_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    customer_name VARCHAR(100) NOT NULL,
    customer_phone VARCHAR(20) NOT NULL,
    customer_email VARCHAR(100) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    next_visit_discount_percent DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    total_amount DECIMAL(10,2) NOT NULL,
    created_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_bills_store
        FOREIGN KEY (store_id) REFERENCES store_profiles(id),
    CONSTRAINT fk_bills_customer
        FOREIGN KEY (customer_id) REFERENCES customers(id)
);

CREATE TABLE IF NOT EXISTS bill_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    bill_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(120) NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    line_total DECIMAL(10,2) NOT NULL,
    waste_category ENUM('RECYCLABLE', 'REUSABLE', 'ECO_DISPOSAL') NOT NULL,
    CONSTRAINT fk_bill_items_bill
        FOREIGN KEY (bill_id) REFERENCES bills(id),
    CONSTRAINT fk_bill_items_product
        FOREIGN KEY (product_id) REFERENCES products(id)
);

INSERT INTO admins (username, password) VALUES
('admin', 'admin123');

INSERT INTO store_profiles (store_name, owner_name, phone_number, email, address, login_username, login_password, active) VALUES
('MegaMart Central', 'Platform Owner', '9999999999', 'owner@megamart.com', 'Main Business Office', 'store_demo', '$2a$10$wIHV1k6Yl9F1V9s7kQ4b2ul89P4sP9N8M2Y2f7x5K5kWQjG0e8K8q', TRUE);

INSERT INTO products (store_id, name, price, image, stock_quantity, waste_category) VALUES
(1, 'Recycled Paper Notebook', 95, 'images/recycled-paper-notebook.jpg', 30, 'RECYCLABLE'),
(1, 'Glass Water Bottle', 220, 'images/glass-water-bottle.jpg', 18, 'REUSABLE'),
(1, 'Eco Disposal Bag Pack', 80, 'images/eco-disposal-bag-pack.jpg', 40, 'ECO_DISPOSAL'),
(1, 'Reusable Steel Straw Set', 150, 'images/reusable-steel-straw-set.jpg', 22, 'REUSABLE'),
(1, 'Compost Bin Starter', 450, 'images/compost-bin-starter.jpg', 8, 'ECO_DISPOSAL'),
(1, 'Recycled Cardboard Storage Box', 180, 'images/recycled-storage-box.jpg', 15, 'RECYCLABLE'),
(1, 'Bamboo Toothbrush Pack', 120, 'images/bamboo-toothbrush-pack.jpg', 26, 'REUSABLE'),
(1, 'Eco Cleaning Sponge', 60, 'images/eco-cleaning-sponge.jpg', 35, 'ECO_DISPOSAL'),
(1, 'Reusable Shopping Tote', 140, 'images/reusable-shopping-tote.jpg', 20, 'REUSABLE'),
(1, 'Recycled Plastic Pen Set', 75, 'images/recycled-plastic-pen-set.jpg', 28, 'RECYCLABLE'),
(1, 'Metal Food Container', 260, 'images/metal-food-container.jpg', 12, 'REUSABLE'),
(1, 'Battery Disposal Kit', 199, 'images/battery-disposal-kit.jpg', 10, 'ECO_DISPOSAL'),
(1, 'Recycled Gift Wrap Roll', 110, 'images/recycled-gift-wrap-roll.jpg', 17, 'RECYCLABLE'),
(1, 'Reusable Coffee Cup', 175, 'images/reusable-coffee-cup.jpg', 14, 'REUSABLE'),
(1, 'E-Waste Collection Box', 320, 'images/e-waste-collection-box.jpg', 9, 'ECO_DISPOSAL');
