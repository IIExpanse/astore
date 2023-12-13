package ru.aston.astore.connection;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.stream.Collectors;

public class ConnectionPool {
    private static HikariDataSource dataSource;
    public static final String SCHEMA_PATH = "schema.sql";
    public static final String TEST_SCHEMA_PATH = "src/main/resources/schema.sql";
    private static final String DRIVER_CLASS_NAME = "org.postgresql.Driver";
    private static final String URL = "jdbc:postgresql://localhost/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "iamroot";

    private ConnectionPool() {
    }

    public static Connection getConnection() {
        if (dataSource == null) {
            initializeDataSource();
            initializeDatabase();
        }
        try {
            return dataSource.getConnection();

        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving database connection: " + e.getMessage());
        }
    }

    public static void setDataSource(DataSource ds) {
        HikariConfig config = new HikariConfig();
        config.setDataSource(ds);
        dataSource = new HikariDataSource(config);
    }

    public static void clearDataSource() {
        dataSource.close();
        dataSource = null;
    }

    private static void initializeDataSource() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(DRIVER_CLASS_NAME);
        config.setJdbcUrl(URL);
        config.setUsername(USER);
        config.setPassword(PASSWORD);
        dataSource = new HikariDataSource(config);
    }

    private static void initializeDatabase() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(ConnectionPool.class.getClassLoader().getResourceAsStream(SCHEMA_PATH))))) {
            String sql = br.lines().collect(Collectors.joining());
            dataSource.getConnection().prepareStatement(sql).execute();

        } catch (IOException e) {
            throw new RuntimeException("Error while loading database schema from file: " + e.getMessage());

        } catch (SQLException e) {
            throw new RuntimeException("Error while executing initial database schema: " + e.getMessage());
        }
    }
}
