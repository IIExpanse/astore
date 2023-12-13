package ru.aston.astore.repository.impl;

import lombok.extern.slf4j.Slf4j;
import ru.aston.astore.connection.ConnectionPool;
import ru.aston.astore.entity.Client;
import ru.aston.astore.repository.ClientRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class JDBCClientRepository implements ClientRepository {
    private static final String INSERT_CLIENT = "INSERT INTO clients (id, first_name, last_name) VALUES (?, ?, ?)";
    private static final String FIND_CLIENT_BY_ID = "SELECT * FROM clients WHERE id = ?";
    private static final String FIND_CLIENTS_BY_NAME = "SELECT * FROM clients " +
            "WHERE first_name LIKE ? AND last_name LIKE ?";
    private static final String UPDATE_CLIENT = "UPDATE clients SET first_name = ?, last_name = ? " +
            "WHERE id = ?";
    private static final String DELETE_CLIENT = "DELETE FROM clients WHERE id = ?";

    @Override
    public Optional<Client> add(Client newClient) {
        try (Connection con = ConnectionPool.getConnection()) {
            PreparedStatement ps = con.prepareStatement(INSERT_CLIENT);
            ps.setObject(1, newClient.getId());
            ps.setString(2, newClient.getFirstName());
            ps.setString(3, newClient.getLastName());
            ps.executeUpdate();

            return findById(newClient.getId());

        } catch (SQLException e) {
            log.error("Error while adding a new client: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<Client> findById(UUID id) {
        try (Connection con = ConnectionPool.getConnection()) {
            PreparedStatement ps = con.prepareStatement(FIND_CLIENT_BY_ID);
            ps.setObject(1, id);
            ResultSet rs = ps.executeQuery();

            return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();

        } catch (SQLException e) {
            log.error(String.format("Error while finding a client with id=%s: %s",
                    id.toString(), e.getMessage()));
        }
        return Optional.empty();
    }

    @Override
    public Collection<Client> findByName(String firstName, String lastName) {
        try (Connection con = ConnectionPool.getConnection()) {
            PreparedStatement ps = con.prepareStatement(FIND_CLIENTS_BY_NAME);
            ps.setString(1, "%" + firstName + "%");
            ps.setString(2, "%" + lastName + "%");
            ResultSet rs = ps.executeQuery();

            List<Client> list = new ArrayList<>();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;

        } catch (SQLException e) {
            log.error(String.format("Error while finding clients with firstName=%s and lastName=%s: %s",
                    firstName, lastName, e.getMessage()));
        }
        return List.of();
    }

    @Override
    public boolean update(Client updatedClient) {
        try (Connection con = ConnectionPool.getConnection()) {
            PreparedStatement ps = con.prepareStatement(UPDATE_CLIENT);
            ps.setString(1, updatedClient.getFirstName());
            ps.setString(2, updatedClient.getLastName());
            ps.setObject(3, updatedClient.getId());
            int affectedRows = ps.executeUpdate();

            return affectedRows > 0;

        } catch (SQLException e) {
            log.error(String.format("Error while updating a client with id=%s: %s",
                    updatedClient.getId().toString(), e.getMessage()));
        }
        return false;
    }

    @Override
    public boolean remove(UUID id) {
        try (Connection con = ConnectionPool.getConnection()) {
            PreparedStatement ps = con.prepareStatement(DELETE_CLIENT);
            ps.setObject(1, id);
            int affectedRows = ps.executeUpdate();

            return affectedRows > 0;

        } catch (Exception e) {
            log.error(String.format("Error while removing a client with id=%s: %s",
                    id.toString(), e.getMessage()));
        }
        return false;
    }

    private Client mapRow(ResultSet rs) throws SQLException {
        return Client.builder()
                .id(UUID.fromString(rs.getString(1)))
                .firstName(rs.getString(2))
                .lastName(rs.getString(3))
                .build();
    }
}
