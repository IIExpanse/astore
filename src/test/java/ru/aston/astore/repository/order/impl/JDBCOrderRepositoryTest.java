package ru.aston.astore.repository.order.impl;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.aston.astore.connection.ConnectionManager;
import ru.aston.astore.entity.client.Client;
import ru.aston.astore.entity.order.Order;
import ru.aston.astore.entity.order.OrderStatus;
import ru.aston.astore.entity.product.Product;
import ru.aston.astore.entity.product.ProductType;
import ru.aston.astore.properties.TestProperties;
import ru.aston.astore.repository.client.impl.JDBCClientRepository;
import ru.aston.astore.repository.product.impl.JDBCProductRepository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
        ConnectionManager.setDataSource(dataSource);

        try (BufferedReader br = new BufferedReader(new FileReader(ConnectionManager.testSchemaPath))) {
            schema = br.lines().collect(Collectors.joining());
        }
    }

    @AfterAll
    static void clearDataSource() {
        ConnectionManager.setDataSource(null);
    }

    @BeforeEach
    void refresh() throws SQLException {
        ConnectionManager.getConnection().prepareStatement("DROP ALL OBJECTS").execute();
        ConnectionManager.getConnection().prepareStatement(schema).execute();
        orderRepository = new JDBCOrderRepository();
        productRepository = new JDBCProductRepository();
        clientRepository = new JDBCClientRepository();
    }


    @Test
    void addAndFindOrder() {
        Client client = getDefaultClient();
        Order order = getDefaultOrder(client.getId());
        clientRepository.addClient(client);
        Order returnedOrder = orderRepository.addOrder(order).orElseThrow();
        assertEquals(order, returnedOrder);
    }

    @Test
    void addProductsIntoOrder() {
        Client client = getDefaultClient();
        clientRepository.addClient(client);

        Product product1 = getDefaultProduct();
        Product product2 = getDefaultProduct();
        productRepository.addProduct(product1);
        productRepository.addProduct(product2);

        Order order = getDefaultOrder(client.getId());
        orderRepository.addOrder(order);

        assertTrue(orderRepository.addProductsIntoOrder(
                List.of(product1.getId(), product2.getId()), order.getId()));

        Collection<UUID> collection = orderRepository.getProductIdsByOrder(order.getId());
        assertTrue(collection.contains(product1.getId()));
        assertTrue(collection.contains(product2.getId()));
    }

    @Test
    void findByStatus() {
        Client client = getDefaultClient();
        Order order = getDefaultOrder(client.getId());
        clientRepository.addClient(client);
        orderRepository.addOrder(order);

        assertTrue(orderRepository.findByStatus(OrderStatus.PENDING).contains(order));
    }

    @Test
    void updateOrder() {
        Client client = getDefaultClient();
        Order order = getDefaultOrder(client.getId());
        clientRepository.addClient(client);
        orderRepository.addOrder(order);

        Order order2 = Order.builder()
                .id(order.getId())
                .client_id(client.getId())
                .status(OrderStatus.PROCESSING)
                .created(LocalDateTime.now())
                .products(List.of())
                .build();
        assertTrue(orderRepository.updateOrder(order2));
        assertEquals(orderRepository.findById(order.getId()).orElseThrow().getStatus(), order2.getStatus());
    }

    @Test
    void removeOrder() {
        Client client = getDefaultClient();
        Order order = getDefaultOrder(client.getId());
        clientRepository.addClient(client);
        orderRepository.addOrder(order);

        assertTrue(orderRepository.removeOrder(order.getId()));
        assertTrue(orderRepository.findById(order.getId()).isEmpty());
    }

    private Order getDefaultOrder(UUID clientId) {
        return Order.builder()
                .id(UUID.randomUUID())
                .client_id(clientId)
                .status(OrderStatus.PENDING)
                .created(LocalDateTime.now())
                .products(List.of())
                .build();
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

    private Client getDefaultClient() {
        return Client.builder()
                .id(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .build();
    }
}