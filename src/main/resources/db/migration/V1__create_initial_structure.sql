-- 1. Tabela de Hoteis
CREATE TABLE hotels (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    version BIGINT -- Optimistic Lock (se necessário no futuro)
);

-- 2. Tabela de Quartos
CREATE TABLE rooms (
    id BIGSERIAL PRIMARY KEY,
    hotel_id BIGINT NOT NULL,
    room_number VARCHAR(20) NOT NULL,
    type VARCHAR(50) NOT NULL, -- STANDARD, DELUXE, SUITE
    base_price DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_rooms_hotel FOREIGN KEY (hotel_id) REFERENCES hotels(id)
);

-- 3. Disponibilidade (O CORAÇÃO DO LOCK PESSIMISTA)
-- Aqui controlamos quantos quartos de um tipo existem por dia.
-- É nesta tabela que faremos o "SELECT ... FOR UPDATE"
CREATE TABLE room_availability (
    id BIGSERIAL PRIMARY KEY,
    hotel_id BIGINT NOT NULL,
    room_type VARCHAR(50) NOT NULL,
    date DATE NOT NULL,
    available_count INT NOT NULL,
    CONSTRAINT fk_availability_hotel FOREIGN KEY (hotel_id) REFERENCES hotels(id),
    CONSTRAINT uq_hotel_type_date UNIQUE (hotel_id, room_type, date) -- Evita duplicidade de inventário
);

-- 4. Reservas
CREATE TABLE reservations (
    id BIGSERIAL PRIMARY KEY,
    hotel_id BIGINT NOT NULL,
    user_email VARCHAR(255) NOT NULL,
    room_type VARCHAR(50) NOT NULL,
    check_in DATE NOT NULL,
    check_out DATE NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL, -- PENDING, CONFIRMED, CANCELLED
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reservations_hotel FOREIGN KEY (hotel_id) REFERENCES hotels(id)
);

-- 5. Índices de Performance (Essencial para não travar o banco)
CREATE INDEX idx_availability_lookup ON room_availability (hotel_id, date, room_type);
CREATE INDEX idx_reservations_user ON reservations (user_email);