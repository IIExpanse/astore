package ru.aston.astore.repository.impl;

import lombok.extern.slf4j.Slf4j;
import ru.aston.astore.connection.ConnectionPool;
import ru.aston.astore.entity.Order;
import ru.aston.astore.entity.OrderStatus;
import ru.aston.astore.repository.OrderRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class JDBCOrderRepository implements OrderRepository {
    private static final String INSERT_ORDER = "INSERT INTO orders (id, client_id, manager_id, status, created) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String INSERT_PRODUCTS_INTO_ORDER =
            "MERGE INTO orders_products op " +
            "USING (SELECT * FROM (VALUES (?, ?)) AS s(order_id, product_id)) AS src " +
            "ON src.order_id = op.order_id AND src.product_id = op.order_id " +
            "WHEN MATCHED THEN " +
                "UPDATE SET product_amount = product_amount + 1 " +
            "WHEN NOT MATCHED THEN " +
                "INSERT VALUES (?, ?, 1)";
    private static final String FIND_ORDER_BY_ID = "SELECT * FROM orders WHERE id = ?";
    private static final String FIND_PRODUCTS_FROM_ORDER = "SELECT product_id FROM orders_products WHERE order_id = ?";
    private static final String FIND_ORDERS_BY_STATUS = "SELECT * FROM orders WHERE status = ?";
    private static final String UPDATE_ORDER = "UPDATE orders SET client_id = ?, manager_id = ?, status = ?, " +
            "created = ? " +
            "WHERE id = ?";
    private static final String REMOVE_PRODUCTS_FROM_ORDER = "DELETE FROM orders_products WHERE order_id = ?";
    private static final String REMOVE_ORDER = "DELETE FROM orders WHERE id = ?";

    @Override
    public Optional<Order> add(Order newOrder) {
        try (Connection con = ConnectionPool.getConnection()) {
            PreparedStatement ps = con.prepareStatement(INSERT_ORDER);
            ps.setObject(1, newOrder.getId());
            ps.setObject(2, newOrder.getClient_id());
            ps.setString(4, newOrder.getStatus().toString());
            ps.setTimestamp(5, Timestamp.valueOf(newOrder.getCreated()));
            if (newOrder.getAssignedManager() != null) {
                ps.setObject(3, newOrder.getAssignedManager());

            } else {
                ps.setNull(3, Types.OTHER);
            }
            ps.executeUpdate();
            ps.close();
            if (!newOrder.getProducts().isEmpty()) {
                addProductsIntoOrder(newOrder.getProducts(), newOrder.getId());
            }
            return findById(newOrder.getId());

        } catch (SQLException e) {
            log.error("Error while adding a new order: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public boolean addProductsIntoOrder(Collection<UUID> productsIds, UUID orderId) {
        try (Connection con = ConnectionPool.getConnection()) {
            PreparedStatement ps = con.prepareStatement(INSERT_PRODUCTS_INTO_ORDER);

            for (UUID productsId : productsIds) {
                ps.setObject(1, orderId);
                ps.setObject(2, productsId);
                ps.setObject(3, orderId);
                ps.setObject(4, productsId);
                ps.addBatch();
            }

            int successfulCommands = ps.executeBatch().length;
            ps.close();

            return successfulCommands > 0;

        } catch (SQLException e) {
            log.error(String.format("Error while adding products into order with id=%s: %s",
                    orderId.toString(), e.getMessage()));
        }
        return false;
    }

    @Override
    public Optional<Order> findById(UUID id) {
        try (Connection con = ConnectionPool.getConnection()) {
            PreparedStatement ps = con.prepareStatement(FIND_ORDER_BY_ID);
            ps.setObject(1, id);
            ResultSet rs = ps.executeQuery();

            Optional<Order> ans = rs.next() ?
                    Optional.of(mapRow(rs)) : Optional.empty();
            ps.close();
            return ans;

        } catch (SQLException e) {
            log.error(String.format("Error while finding an order with id=%s: %s",
                    id.toString(), e.getMessage()));
        }
        return Optional.empty();
    }

    @Override
    public Collection<UUID> getProductIdsByOrder(UUID id) {
        try (Connection con = ConnectionPool.getConnection()) {
            PreparedStatement ps = con.prepareStatement(FIND_PRODUCTS_FROM_ORDER);
            ps.setObject(1, id);
            ResultSet rs = ps.executeQuery();

            List<UUID> list = new ArrayList<>();
            while (rs.next()) {
                list.add(UUID.fromString(rs.getString(1)));
            }
            ps.close();

            return list;

        } catch (SQLException e) {
            log.error(String.format("Error while retrieving product IDs from an order with id=%s: %s",
                    id.toString(), e.getMessage()));
        }
        return List.of();
    }

    @Override
    public Collection<Order> findByStatus(OrderStatus status) {
        try (Connection con = ConnectionPool.getConnection()) {
            PreparedStatement ps = con.prepareStatement(FIND_ORDERS_BY_STATUS);
            ps.setString(1, status.toString());
            ResultSet rs = ps.executeQuery();

            List<Order> list = new ArrayList<>();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            ps.close();

            return list;

        } catch (SQLException e) {
            log.error(String.format("Error while finding orders with status%s: %s",
                    status.toString(), e.getMessage()));
        }
        return List.of();
    }

    @Override
    public boolean update(Order updatedOrder) {
        try (Connection con = ConnectionPool.getConnection()) {
            PreparedStatement ps = con.prepareStatement(UPDATE_ORDER);
            ps.setObject(1, updatedOrder.getClient_id());
            ps.setString(3, updatedOrder.getStatus().toString());
            ps.setTimestamp(4, Timestamp.valueOf(updatedOrder.getCreated()));
            ps.setObject(5, updatedOrder.getId());
            if (updatedOrder.getAssignedManager() != null) {
                ps.setObject(2, updatedOrder.getAssignedManager());

            } else {
                ps.setNull(2, Types.NULL);
            }
            int affectedRows = ps.executeUpdate();
            ps.close();

            return affectedRows > 0;

        } catch (SQLException e) {
            log.error(String.format("Error while updating an order with id=%s: %s",
                    updatedOrder.getId().toString(), e.getMessage()));
        }
        return false;
    }

    @Override
    public boolean remove(UUID id) {
        try (Connection con = ConnectionPool.getConnection()) {
            PreparedStatement ps = con.prepareStatement(REMOVE_PRODUCTS_FROM_ORDER);
            ps.setObject(1, id);
            ps.executeUpdate();

            ps = con.prepareStatement(REMOVE_ORDER);
            ps.setObject(1, id);
            int affectedRows = ps.executeUpdate();
            ps.close();

            return affectedRows > 0;

        } catch (Exception e) {
            log.error(String.format("Error while removing an order with id=%s: %s",
                    id.toString(), e.getMessage()));
        }
        return false;
    }

    private Order mapRow(ResultSet rs) throws SQLException {
        UUID orderId = UUID.fromString(rs.getString(1));

        return Order.builder()
                .id(orderId)
                .client_id(UUID.fromString(rs.getString(2)))
                .assignedManager(rs.getString(3) != null ? UUID.fromString(rs.getString(3)) : null)
                .status(OrderStatus.valueOf(rs.getString(4)))
                .created(rs.getTimestamp(5).toLocalDateTime())
                .products(getProductIdsByOrder(orderId))
                .build();
    }
}
