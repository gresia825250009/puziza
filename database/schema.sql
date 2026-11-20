
-- =========================================
-- DATABASE PUZIZA
-- =========================================

-- 1. TABLE PERSONS
CREATE TABLE IF NOT EXISTS persons (
    id SERIAL PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    registered_at DATE NOT NULL DEFAULT CURRENT_DATE,
    role VARCHAR(20) NOT NULL
        CHECK (role IN ('CUSTOMER', 'CASHIER')),
    gender VARCHAR(10),
    birth_date DATE
);

-- 2. TABLE CUSTOMERS
CREATE TABLE IF NOT EXISTS customers (
    person_id INTEGER PRIMARY KEY,
    email VARCHAR(120),

    CONSTRAINT fk_customers_person
        FOREIGN KEY (person_id)
        REFERENCES persons(id)
        ON DELETE CASCADE
);

-- 3. TABLE CASHIERS
CREATE TABLE IF NOT EXISTS cashiers (
    person_id INTEGER PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,

    CONSTRAINT fk_cashiers_person
        FOREIGN KEY (person_id)
        REFERENCES persons(id)
        ON DELETE CASCADE
);

-- 4. TABLE PIZZAS
CREATE TABLE IF NOT EXISTS pizzas (
    id SERIAL PRIMARY KEY,
    pizza_name VARCHAR(40) NOT NULL
        CHECK (pizza_name IN ('VolcanoMeat', 'CheeseMania')),
    size VARCHAR(10) NOT NULL
        CHECK (size IN ('Small', 'Medium', 'Large')),
    base_price NUMERIC(12,0) NOT NULL
        CHECK (base_price >= 0),

    UNIQUE (pizza_name, size)
);

-- 5. TABLE CHEESE_MANIA
CREATE TABLE IF NOT EXISTS cheese_mania (
    pizza_id INTEGER PRIMARY KEY,
    topping_description VARCHAR(150) NOT NULL
        DEFAULT 'Mozzarella and cheese',

    CONSTRAINT fk_cheese_pizza
        FOREIGN KEY (pizza_id)
        REFERENCES pizzas(id)
        ON DELETE CASCADE
);

-- 6. TABLE VOLCANO_MEAT
CREATE TABLE IF NOT EXISTS volcano_meat (
    pizza_id INTEGER PRIMARY KEY,
    topping_description VARCHAR(150) NOT NULL
        DEFAULT 'Beef, sausage, spicy sauce',

    CONSTRAINT fk_volcano_pizza
        FOREIGN KEY (pizza_id)
        REFERENCES pizzas(id)
        ON DELETE CASCADE
);

-- 7. TABLE SALES
CREATE TABLE IF NOT EXISTS sales (
    id SERIAL PRIMARY KEY,
    sale_code VARCHAR(30) NOT NULL UNIQUE,
    created_at TIMESTAMP WITHOUT TIME ZONE
        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    customer_id INTEGER,
    cashier_id INTEGER,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    listproduk INTEGER[] NOT NULL,

    CONSTRAINT fk_sales_customer
        FOREIGN KEY (customer_id)
        REFERENCES customers(person_id),

    CONSTRAINT fk_sales_cashier
        FOREIGN KEY (cashier_id)
        REFERENCES cashiers(person_id)
);

-- =========================================
-- DATABASE PERMISSIONS
-- =========================================

GRANT SELECT, INSERT, UPDATE, DELETE
ON ALL TABLES IN SCHEMA public TO gresia;

GRANT USAGE, SELECT, UPDATE
ON ALL SEQUENCES IN SCHEMA public TO gresia;