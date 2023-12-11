package ru.aston.astore.repository.impl;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.aston.astore.connection.ConnectionPool;
import ru.aston.astore.entity.Client;
import ru.aston.astore.entity.Order;
import ru.aston.astore.entity.OrderStatus;
import ru.aston.astore.entity.Product;
import ru.aston.astore.properties.TestProperties;
import ru.aston.astore.util.ObjectsFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JDBCOrderRepositoryTest {

    private JDBCOrderRepository orderRepository;
    private JDBCProductRepository productRepository;
    private JDBCClientRepository clientRepository;
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
        orderRepository = new JDBCOrderRepository();
        productRepository = new JDBCProductRepository();
        clientRepository = new JDBCClientRepository();
    }


    @Test
    void addAndFindOrder() {
        Client client = ObjectsFactory.getDefaultClient();
        Order order = ObjectsFactory.getDefaultOrder(client.getId());
        clientRepository.add(client);
        Order returnedOrder = orderRepository.add(order).orElseThrow();
        assertEquals(order, returnedOrder);
    }

    @Test
    void addProductsIntoOrder() {
        Client client = ObjectsFactory.getDefaultClient();
        clientRepository.add(client);

        Product product1 = ObjectsFactory.getDefaultProduct();
        Product product2 = ObjectsFactory.getDefaultProduct();
        productRepository.add(product1);
        productRepository.add(product2);

        Order order = ObjectsFactory.getDefaultOrder(client.getId());
        orderRepository.add(order);

        assertTrue(orderRepository.addProductsIntoOrder(
                List.of(product1.getId(), product2.getId()), order.getId()));

        Collection<UUID> collection = orderRepository.getProductIdsByOrder(order.getId());
        assertTrue(collection.contains(product1.getId()));
        assertTrue(collection.contains(product2.getId()));
    }

    @Test
    void findByStatus() {
        Client client = ObjectsFactory.getDefaultClient();
        Order order = ObjectsFactory.getDefaultOrder(client.getId());
        clientRepository.add(client);
        orderRepository.add(order);

        assertTrue(orderRepository.findByStatus(OrderStatus.PENDING).contains(order));
    }

    @Test
    void updateOrder() {
        Client client = ObjectsFactory.getDefaultClient();
        Order order = ObjectsFactory.getDefaultOrder(client.getId());
        clientRepository.add(client);
        orderRepository.add(order);

        Order order2 = Order.builder()
                .id(order.getId())
                .client_id(client.getId())
                .status(OrderStatus.PROCESSING)
                .created(LocalDateTime.now())
                .products(List.of())
                .build();
        assertTrue(orderRepository.update(order2));
        assertEquals(orderRepository.findById(order.getId()).orElseThrow().getStatus(), order2.getStatus());
    }

    @Test
    void removeOrder() {
        Client client = ObjectsFactory.getDefaultClient();
        Order order = ObjectsFactory.getDefaultOrder(client.getId());
        clientRepository.add(client);
        orderRepository.add(order);

        assertTrue(orderRepository.remove(order.getId()));
        assertTrue(orderRepository.findById(order.getId()).isEmpty());
    }
}