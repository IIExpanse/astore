package ru.aston.astore.repository.product.impl;

import lombok.extern.slf4j.Slf4j;
import ru.aston.astore.connection.ConnectionPool;
import ru.aston.astore.entity.product.Product;
import ru.aston.astore.entity.product.ProductType;
import ru.aston.astore.repository.product.ProductRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class JDBCProductRepository implements ProductRepository {
    private static final String INSERT_PRODUCT = "INSERT INTO products (id, title, price, discount, product_type) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String FIND_PRODUCT_BY_ID = "SELECT * FROM products WHERE id = ?";
    private static final String FIND_PRODUCTS_BY_TITLE = "SELECT * FROM products WHERE products.title LIKE ?";
    private static final String UPDATE_PRODUCT = "UPDATE products SET title = ?, price = ?, discount = ?, " +
            "product_type = ? " +
            "WHERE id = ?";
    private static final String DELETE_PRODUCT = "DELETE FROM products WHERE id = ?";

    @Override
    public Optional<Product> addProduct(Product newProduct) {
        try (Connection con = ConnectionPool.getConnection()) {
            PreparedStatement ps = con.prepareStatement(INSERT_PRODUCT);
            ps.setObject(1, newProduct.getId());
            ps.setString(2, newProduct.getTitle());
            ps.setFloat(3, newProduct.getPrice());
            ps.setString(5, newProduct.getType().toString());
            if (newProduct.getDiscount() != null) {
                ps.setFloat(4, newProduct.getDiscount());

            } else {
                ps.setNull(4, Types.FLOAT);
            }
            ps.executeUpdate();
            ps.close();

            return findById(newProduct.getId());

        } catch (SQLException e) {
            log.debug("Error while adding a new product: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<Product> findById(UUID id) {
        try (Connection con = ConnectionPool.getConnection()) {
            PreparedStatement ps = con.prepareStatement(FIND_PRODUCT_BY_ID);
            ps.setObject(1, id);
            ResultSet rs = ps.executeQuery();

            Optional<Product> ans = rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            ps.close();
            return ans;

        } catch (SQLException e) {
            log.debug(String.format("Error while finding a product with id=%s: %s",
                    id.toString(), e.getMessage()));
        }
        return Optional.empty();
    }

    @Override
    public Collection<Product> findByTitle(String title) {
        try (Connection con = ConnectionPool.getConnection()) {
            PreparedStatement ps = con.prepareStatement(FIND_PRODUCTS_BY_TITLE);
            ps.setString(1, "%" + title + "%");
            ResultSet rs = ps.executeQuery();

            List<Product> list = new ArrayList<>();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            ps.close();
            return list;

        } catch (SQLException e) {
            log.debug(String.format("Error while finding products with title=%s: %s",
                    title, e.getMessage()));
        }
        return List.of();
    }

    @Override
    public boolean updateProduct(Product updatedProduct) {
        try (Connection con = ConnectionPool.getConnection()) {
            PreparedStatement ps = con.prepareStatement(UPDATE_PRODUCT);
            ps.setString(1, updatedProduct.getTitle());
            ps.setFloat(2, updatedProduct.getPrice());
            ps.setString(4, updatedProduct.getType().toString());
            ps.setObject(5, updatedProduct.getId());
            if (updatedProduct.getDiscount() != null) {
                ps.setFloat(3, updatedProduct.getDiscount());

            } else {
                ps.setNull(3, Types.FLOAT);
            }

            int affectedRows = ps.executeUpdate();
            ps.close();

            return affectedRows > 0;

        } catch (SQLException e) {
            log.debug(String.format("Error while updating an employee with id=%s: %s",
                    updatedProduct.getId().toString(), e.getMessage()));
        }
        return false;
    }

    @Override
    public boolean removeProduct(UUID id) {
        try (Connection con = ConnectionPool.getConnection()) {
            PreparedStatement ps = con.prepareStatement(DELETE_PRODUCT);
            ps.setObject(1, id);
            int affectedRows = ps.executeUpdate();
            ps.close();

            return affectedRows > 0;

        } catch (SQLException e) {
            log.debug(String.format("Error while removing an employee with id=%s: %s",
                    id.toString(), e.getMessage()));
        }
        return false;
    }

    private Product mapRow(ResultSet rs) throws SQLException {
        return Product.builder()
                .id(UUID.fromString(rs.getString(1)))
                .title(rs.getString(2))
                .price(rs.getFloat(3))
                .discount(rs.getFloat(4))
                .type(ProductType.valueOf(rs.getString(5)))
                .build();
    }
}
