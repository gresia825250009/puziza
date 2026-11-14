CREATE TABLE IF NOT EXISTS persons (
    id SERIAL PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    registered_at DATE NOT NULL DEFAULT CURRENT_DATE,
    person_type VARCHAR(20) NOT NULL CHECK (person_type IN ('CUSTOMER','CASHIER'))
);
CREATE TABLE IF NOT EXISTS customers (
    person_id INTEGER PRIMARY KEY REFERENCES persons(id) ON DELETE CASCADE,
    email VARCHAR(120)
);
CREATE TABLE IF NOT EXISTS cashiers (
    person_id INTEGER PRIMARY KEY REFERENCES persons(id) ON DELETE CASCADE,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL
);
CREATE TABLE IF NOT EXISTS pizzas (
    id SERIAL PRIMARY KEY,
    pizza_name VARCHAR(40) NOT NULL CHECK (pizza_name IN ('VolcanoMeat','CheeseMania')),
    size VARCHAR(10) NOT NULL CHECK (size IN ('Small','Medium','Large')),
    base_price NUMERIC(12,2) NOT NULL CHECK (base_price >= 0),
    UNIQUE(pizza_name,size)
);
CREATE TABLE IF NOT EXISTS volcano_meat (
    pizza_id INTEGER PRIMARY KEY REFERENCES pizzas(id) ON DELETE CASCADE,
    topping_description VARCHAR(150) NOT NULL DEFAULT 'Beef, sausage, spicy sauce'
);
CREATE TABLE IF NOT EXISTS cheese_mania (
    pizza_id INTEGER PRIMARY KEY REFERENCES pizzas(id) ON DELETE CASCADE,
    topping_description VARCHAR(150) NOT NULL DEFAULT 'Mozzarella and cheese'
);
CREATE TABLE IF NOT EXISTS sales (
    id SERIAL PRIMARY KEY,
    sale_code VARCHAR(30) UNIQUE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    customer_id INTEGER REFERENCES customers(person_id),
    cashier_id INTEGER REFERENCES cashiers(person_id),
    subtotal NUMERIC(12,2) NOT NULL,
    tax NUMERIC(12,2) NOT NULL,
    total NUMERIC(12,2) NOT NULL
);
CREATE TABLE IF NOT EXISTS sale_items (
    id SERIAL PRIMARY KEY,
    sale_id INTEGER NOT NULL REFERENCES sales(id) ON DELETE CASCADE,
    pizza_id INTEGER NOT NULL REFERENCES pizzas(id),
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    unit_price NUMERIC(12,2) NOT NULL
);

GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO gresia;
GRANT USAGE, SELECT, UPDATE ON ALL SEQUENCES IN SCHEMA public TO gresia;
