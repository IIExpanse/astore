package ru.aston.astore.repository.product.impl;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.aston.astore.connection.ConnectionPool;
import ru.aston.astore.entity.product.Product;
import ru.aston.astore.entity.product.ProductType;
import ru.aston.astore.properties.TestProperties;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JDBCProductRepositoryTest {
    private JDBCProductRepository repository;
    private static String schema;

    @BeforeAll
    static void setTestDataSource() throws IOException {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL(TestProperties.testDatabaseURL);
        ConnectionPool.setDataSource(dataSource);

        try (BufferedReader br = new BufferedReader(new FileReader(ConnectionPool.testSchemaPath))) {
            schema = br.lines().collect(Collectors.joining());
        }
    }

    @AfterAll
    static void clearDataSource() {
        ConnectionPool.clearDataSource();
    }

    @BeforeEach
    void refresh() throws SQLException {
        try (Connection con = ConnectionPool.getConnection()) {
            con.prepareStatement("DROP ALL OBJECTS").execute();
            con.prepareStatement(schema).execute();
        }
        repository = new JDBCProductRepository();
    }

    @Test
    void addAndFindProduct() {
        Product product = getDefaultProduct();
        Product returnedProduct = repository.addProduct(product).orElseThrow();
        assertEquals(product, returnedProduct);
    }

    @Test
    void findByTitle() {
        Product product = getDefaultProduct();
        repository.addProduct(product);
        assertTrue(repository.findByTitle(product.getTitle())
                .contains(product));
        assertTrue(repository.findByTitle(product.getTitle())
                .contains(product));
    }

    @Test
    void updateProduct() {
        Product product = getDefaultProduct();
        repository.addProduct(product);
        product = Product.builder()
                .id(product.getId())
                .title("SmartPhone")
                .price(12.3f)
                .type(ProductType.ELECTRONICS)
                .build();
        assertTrue(repository.updateProduct(product));

        Product returnedProduct = repository.findById(product.getId()).orElseThrow();
        assertEquals(product.getTitle(), returnedProduct.getTitle());
        assertEquals(product.getPrice(), returnedProduct.getPrice());
        assertEquals(product.getType(), returnedProduct.getType());
    }

    @Test
    void removeProduct() {
        Product product = getDefaultProduct();
        repository.addProduct(product);
        assertTrue(repository.removeProduct(product.getId()));
        assertTrue(repository.findById(product.getId()).isEmpty());
    }

    private Product getDefaultProduct() {
        return Product.builder()
                .id(UUID.randomUUID())
                .title("Wooden chair")
                .price(9.5f)
                .discount(null)
                .type(ProductType.FURNITURE)
                .build();
    }
}