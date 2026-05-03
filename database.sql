-- FoodieExpress Complete Database Schema (MySQL for Beginners)
-- ======================================================
-- Step 1: Run this file: mysql -u root -p < database.sql
-- Password: your_mysql_password

-- Create Database
CREATE DATABASE IF NOT EXISTS food_ordering_db;
USE food_ordering_db;

-- 1. Users table - for registration and login
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL, -- Plain text for demo (use BCrypt in production)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Menu Items table - food menu
CREATE TABLE menu_items (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    category VARCHAR(50) NOT NULL,
    description TEXT
);

-- 3. Orders table - main orders
CREATE TABLE orders (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'placed',
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 4. Order Items table - items in each order
CREATE TABLE order_items (
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    item_id INT NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES menu_items(id)
);

-- Sample Data - 1 Admin + 1 Customer
INSERT INTO users (name, email, password) VALUES
('Admin User', 'admin@foodieexpress.com', 'admin123'), -- Login: admin@foodieexpress.com / admin123
('Test Customer', 'customer@foodieexpress.com', 'customer123'); -- Login: customer@foodieexpress.com / customer123

-- Sample 10 Food Items
INSERT INTO menu_items (name, price, category, description) VALUES
('Classic Burger', 89.00, 'Burgers', 'Juicy beef patty with cheese and fresh veggies'),
('Cheese Pizza', 129.00, 'Pizza', 'Fresh mozzarella and tangy tomato sauce pizza'),
('Chicken Pasta', 109.00, 'Pasta', 'Creamy pasta with grilled chicken and herbs'),
('Caesar Salad', 79.00, 'Salad', 'Crisp romaine lettuce with creamy Caesar dressing'),
('Pepsi 330ml', 25.00, 'Drinks', 'Refreshing Pepsi cola can'),
('Veggie Burger', 79.00, 'Burgers', 'Healthy patty made with fresh vegetables'),
('Margherita Pizza', 119.00, 'Pizza', 'Classic Italian pizza with basil and mozzarella'),
('Garlic Bread', 59.00, 'Sides', 'Crispy garlic bread with herb butter'),
('Chocolate Cake', 69.00, 'Dessert', 'Rich moist chocolate cake slice'),
('Oreo Shake', 89.00, 'Drinks', 'Creamy Oreo milkshake with whipped cream');

-- Success message
SELECT 'FoodieExpress Database Created Successfully! Ready for Java servlets.' as message;

