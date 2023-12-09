package ru.aston.astore.connection;

import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.stream.Collectors;

public class ConnectionManager {
    private static DataSource dataSource;
    public static final String schemaPath = "schema.sql";
    public static final String testSchemaPath = "src/main/resources/schema.sql";
    private static final String URL = "jdbc:postgresql://localhost/postgres";
    private static final String user = "postgres";
    private static final String password = "iamroot";

    private ConnectionManager() {
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
        dataSource = ds;
    }

    private static void initializeDataSource() {
        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setURL(URL);
        ds.setUser(user);
        ds.setPassword(password);
        dataSource = ds;
    }

    private static void initializeDatabase() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(ConnectionManager.class.getClassLoader().getResourceAsStream(schemaPath))))) {
            String sql = br.lines().collect(Collectors.joining());
            dataSource.getConnection().prepareStatement(sql).execute();

        } catch (IOException e) {
            throw new RuntimeException("Error while loading database schema from file: " + e.getMessage());

        } catch (SQLException e) {
            throw new RuntimeException("Error while executing initial database schema: " + e.getMessage());
        }
    }
}
