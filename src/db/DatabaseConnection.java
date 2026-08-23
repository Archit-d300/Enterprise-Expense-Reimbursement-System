package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    private static final String URL = "jdbc:sqlite:expense_app.db";

    private DatabaseConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection(URL);
            initTables();
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite Driver Error: JDBC jar file missing from classpath.");
        } catch (SQLException e) {
            System.err.println("Database Connection Failed: " + e.getMessage());
        }
    }

    public static synchronized DatabaseConnection getInstance() {
        try {
            if (instance == null || instance.connection == null || instance.connection.isClosed()) {
                instance = new DatabaseConnection();
            }
        } catch (SQLException e) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    private void initTables() {
        if (connection == null) {
            return;
        }

        String createClaimsTable = """
            CREATE TABLE IF NOT EXISTS expense_claims (
                id INTEGER PRIMARY KEY,
                employee_id INTEGER NOT NULL,
                amount REAL NOT NULL,
                email TEXT,
                description TEXT,
                status TEXT NOT NULL
            );
        """;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createClaimsTable);
        } catch (SQLException e) {
            System.err.println("Table Creation Failed: " + e.getMessage());
        }
    }
}