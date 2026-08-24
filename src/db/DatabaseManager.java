package db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private final String url;
    private Connection connection;

    public DatabaseManager(String dbFileName) {
        this.url = "jdbc:sqlite:" + dbFileName;
    }

    public void connect() {
        try {
            connection = DriverManager.getConnection(url);
            System.out.println("Connected to SQLite database.");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to SQLite database: " + e.getMessage(), e);
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println("Warning: failed to close database connection cleanly.");
        }
    }

    public void resetDatabase() {
        String dropProcessedTable = "DROP TABLE IF EXISTS processed_expenses";
        String dropPendingTable = "DROP TABLE IF EXISTS pending_expenses";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(dropProcessedTable);
            stmt.execute(dropPendingTable);
            System.out.println("Database reset: pending_expenses and processed_expenses have been cleared.");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to reset database: " + e.getMessage(), e);
        }
    }

    public void initializeSchema() {
        String pendingTable = "CREATE TABLE IF NOT EXISTS pending_expenses (" +
                "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  employee_id INTEGER NOT NULL," +
                "  employee_name TEXT NOT NULL," +
                "  employee_email TEXT NOT NULL," +
                "  role TEXT NOT NULL," +
                "  expense_type TEXT NOT NULL," +
                "  amount REAL NOT NULL," +
                "  description TEXT," +
                "  status TEXT NOT NULL DEFAULT 'PENDING'," +
                "  created_at TEXT NOT NULL DEFAULT (datetime('now'))" +
                ");";

        String processedTable = "CREATE TABLE IF NOT EXISTS processed_expenses (" +
                "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  pending_id INTEGER NOT NULL," +
                "  employee_id INTEGER NOT NULL," +
                "  employee_name TEXT NOT NULL," +
                "  employee_email TEXT NOT NULL," +
                "  role TEXT NOT NULL," +
                "  expense_type TEXT NOT NULL," +
                "  amount REAL NOT NULL," +
                "  description TEXT," +
                "  final_status TEXT NOT NULL," +
                "  remarks TEXT," +
                "  transaction_id TEXT," +
                "  processed_at TEXT NOT NULL DEFAULT (datetime('now'))," +
                "  FOREIGN KEY (pending_id) REFERENCES pending_expenses(id)" +
                ");";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(pendingTable);
            stmt.execute(processedTable);
            System.out.println("Schema ready: pending_expenses, processed_expenses.");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize schema: " + e.getMessage(), e);
        }
    }

    public void seedSampleDataIfEmpty() {
        String countSql = "SELECT COUNT(*) FROM pending_expenses";
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(countSql)) {

            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("pending_expenses already has data, skipping seed.");
                return;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check pending_expenses: " + e.getMessage(), e);
        }

        String insertSql = "INSERT INTO pending_expenses " +
                "(employee_id, employee_name, employee_email, role, expense_type, amount, description) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        Object[][] sampleRows = {
                { 101, "Archit Deshpande", "architdeshpande.study@gmail.com", "EMPLOYEE", "TRAVEL", 15000.0,
                        "Client visit - Pune to Mumbai flight + cab" },
                { 102, "Priya Nair", "priya.nair@example.com", "EMPLOYEE", "FOOD", 8000.0,
                        "Team lunch during offsite" },
                { 103, "Arjun Mehta", "arjun.mehta@example.com", "MANAGER", "ACCOMMODATION", 15000.0,
                        "2-night hotel stay for vendor audit" },
                { 104, "Sneha Kulkarni", "sneha.kulkarni@example.com", "MANAGER", "FOOD", 90000.0,
                        "Quarterly client dinner (bulk booking)" },
                { 105, "Vikram Rao", "vikram.rao@example.com", "FINANCE", "OFFICE", 4000.0,
                        "Printer cartridges and stationery" },
                { 106, "Ananya Iyer", "ananya.iyer@example.com", "ADMIN", "OFFICE", 55000.0,
                        "New workstation setup for new hires" },
                { 107, "Karan Verma", "karan.verma@example.com", "ADMIN", "TRAVEL", 95000.0,
                        "International conference travel" },
                { 108, "Divya Reddy", "divya.reddy@example.com", "EMPLOYEE", "OFFICE", 2000.0, "Notebooks and pens" }
        };

        try (PreparedStatement ps = connection.prepareStatement(insertSql)) {
            for (Object[] row : sampleRows) {
                ps.setInt(1, (Integer) row[0]);
                ps.setString(2, (String) row[1]);
                ps.setString(3, (String) row[2]);
                ps.setString(4, (String) row[3]);
                ps.setString(5, (String) row[4]);
                ps.setDouble(6, (Double) row[5]);
                ps.setString(7, (String) row[6]);
                ps.addBatch();
            }
            ps.executeBatch();
            System.out.println("Seeded " + sampleRows.length + " sample pending expense claims.");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to seed sample data: " + e.getMessage(), e);
        }
    }

    public List<PendingExpense> fetchPending() {
        String sql = "SELECT id, employee_id, employee_name, employee_email, role, expense_type, amount, description " +
                "FROM pending_expenses WHERE status = 'PENDING' ORDER BY id";

        List<PendingExpense> result = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                result.add(new PendingExpense(
                        rs.getInt("id"),
                        rs.getInt("employee_id"),
                        rs.getString("employee_name"),
                        rs.getString("employee_email"),
                        rs.getString("role"),
                        rs.getString("expense_type"),
                        rs.getDouble("amount"),
                        rs.getString("description")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch pending expenses: " + e.getMessage(), e);
        }

        return result;
    }

    public void markPendingProcessed(int pendingId, String newStatus) {
        String sql = "UPDATE pending_expenses SET status = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, pendingId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update pending_expenses status: " + e.getMessage(), e);
        }
    }

    public void insertProcessed(PendingExpense pe, String finalStatus, String remarks, String transactionId) {
        String sql = "INSERT INTO processed_expenses " +
                "(pending_id, employee_id, employee_name, employee_email, role, expense_type, amount, description, " +
                " final_status, remarks, transaction_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, pe.getId());
            ps.setInt(2, pe.getEmployeeId());
            ps.setString(3, pe.getEmployeeName());
            ps.setString(4, pe.getEmployeeEmail());
            ps.setString(5, pe.getRole());
            ps.setString(6, pe.getExpenseType());
            ps.setDouble(7, pe.getAmount());
            ps.setString(8, pe.getDescription());
            ps.setString(9, finalStatus);
            ps.setString(10, remarks);
            ps.setString(11, transactionId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert into processed_expenses: " + e.getMessage(), e);
        }
    }
}
