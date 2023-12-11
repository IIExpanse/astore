package ru.aston.astore.repository.impl;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.aston.astore.connection.ConnectionPool;
import ru.aston.astore.entity.Client;
import ru.aston.astore.properties.TestProperties;
import ru.aston.astore.util.ObjectsFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JDBCClientRepositoryTest {
    private JDBCClientRepository repository;
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
        repository = new JDBCClientRepository();
    }

    @Test
    void addAndFindClient() {
        Client client = ObjectsFactory.getDefaultClient();
        Client returnedClient = repository.add(client).orElseThrow();
        assertEquals(client, returnedClient);
    }

    @Test
    void findByName() {
        Client client = ObjectsFactory.getDefaultClient();
        repository.add(client);
        assertTrue(repository.findByName(client.getFirstName(), client.getLastName())
                .contains(client));
        assertTrue(repository.findByName(client.getFirstName(), "")
                .contains(client));
        assertTrue(repository.findByName("", client.getLastName())
                .contains(client));
        assertTrue(repository.findByName("", client.getLastName().substring(0, 1))
                .contains(client));
    }

    @Test
    void updateClient() {
        Client client = ObjectsFactory.getDefaultClient();
        repository.add(client);
        client = Client.builder()
                .id(client.getId())
                .firstName("Bob")
                .lastName("Smith")
                .build();
        assertTrue(repository.update(client));

        Client returnedClient = repository.findById(client.getId()).orElseThrow();
        assertEquals(client.getFirstName(), returnedClient.getFirstName());
        assertEquals(client.getLastName(), returnedClient.getLastName());
    }

    @Test
    void removeClient() {
        Client client = ObjectsFactory.getDefaultClient();
        repository.add(client);
        assertTrue(repository.remove(client.getId()));
        assertTrue(repository.findById(client.getId()).isEmpty());
    }
}