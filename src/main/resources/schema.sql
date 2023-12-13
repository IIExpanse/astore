CREATE TABLE IF NOT EXISTS clients
(
    id         UUID PRIMARY KEY,
    first_name VARCHAR NOT NULL,
    last_name  VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS products
(
    id           UUID PRIMARY KEY,
    title        VARCHAR NOT NULL,
    price        FLOAT   NOT NULL,
    discount     FLOAT,
    product_type VARCHAR NOT NULL,
    CONSTRAINT valid_discount CHECK (discount IS NULL OR discount > 0 AND discount < 1),
    CONSTRAINT product_type_enum CHECK (product_type IN ('CLOTHING', 'ELECTRONICS', 'HOUSEHOLD', 'FURNITURE'))
);

CREATE TABLE IF NOT EXISTS employees
(
    id         UUID PRIMARY KEY,
    first_name VARCHAR NOT NULL,
    last_name  VARCHAR NOT NULL,
    role       VARCHAR NOT NULL,
    CONSTRAINT role_enum CHECK (role IN ('MANAGER'))
);

CREATE TABLE IF NOT EXISTS orders
(
    id         UUID PRIMARY KEY,
    client_id  UUID      NOT NULL,
    manager_id UUID,
    status     VARCHAR   NOT NULL,
    created    TIMESTAMP NOT NULL,
    CONSTRAINT fk_client_id FOREIGN KEY (client_id) REFERENCES clients (id) ON UPDATE CASCADE,
    CONSTRAINT fk_manager_id FOREIGN KEY (manager_id) REFERENCES employees (id) ON UPDATE CASCADE,
    CONSTRAINT order_status_enum
        CHECK (status IN ('PENDING', 'PROCESSING', 'DELIVERING', 'COMPLETED', 'CANCELLED')),
    CONSTRAINT order_created_not_in_future CHECK (created <= LOCALTIMESTAMP)
);

CREATE TABLE IF NOT EXISTS orders_products
(
    order_id       UUID,
    product_id     UUID,
    product_amount INT NOT NULL,
    CONSTRAINT pk_order_product_ids PRIMARY KEY (order_id, product_id),
    CONSTRAINT fk_order_id FOREIGN KEY (order_id) REFERENCES orders (id) ON UPDATE CASCADE,
    CONSTRAINT fk_product_id FOREIGN KEY (product_id) REFERENCES products (id) ON UPDATE CASCADE
);