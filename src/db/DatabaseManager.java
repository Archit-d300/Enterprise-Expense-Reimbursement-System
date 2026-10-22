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
                "  department TEXT NOT NULL DEFAULT 'GENERAL'," +
                "  receipt_reference TEXT NOT NULL DEFAULT ''," +
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
                "  department TEXT NOT NULL DEFAULT 'GENERAL'," +
                "  receipt_reference TEXT NOT NULL DEFAULT ''," +
                "  final_status TEXT NOT NULL," +
                "  remarks TEXT," +
                "  transaction_id TEXT," +
                "  processed_at TEXT NOT NULL DEFAULT (datetime('now'))," +
                "  FOREIGN KEY (pending_id) REFERENCES pending_expenses(id)" +
                ");";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(pendingTable);
            stmt.execute(processedTable);
            ensureColumnExists("pending_expenses", "department", "TEXT NOT NULL DEFAULT 'GENERAL'");
            ensureColumnExists("pending_expenses", "receipt_reference", "TEXT NOT NULL DEFAULT ''");
            ensureColumnExists("processed_expenses", "department", "TEXT NOT NULL DEFAULT 'GENERAL'");
            ensureColumnExists("processed_expenses", "receipt_reference", "TEXT NOT NULL DEFAULT ''");
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
                "(employee_id, employee_name, employee_email, role, expense_type, amount, description, department, receipt_reference) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Object[][] sampleRows = {
                { 101, "Archit Deshpande", "architdeshpande.study@gmail.com", "EMPLOYEE", "TRAVEL", 15000.0,
                        "Client visit - Pune to Mumbai flight + cab", "Sales", "receipts/101-travel.pdf" },
                { 102, "Priya Nair", "priya.nair@example.com", "EMPLOYEE", "FOOD", 8000.0,
                        "Team lunch during offsite", "Engineering", "receipts/102-food.pdf" },
                { 103, "Arjun Mehta", "arjun.mehta@example.com", "MANAGER", "ACCOMMODATION", 15000.0,
                        "2-night hotel stay for vendor audit", "Operations", "receipts/103-hotel.pdf" },
                { 104, "Sneha Kulkarni", "sneha.kulkarni@example.com", "MANAGER", "FOOD", 90000.0,
                        "Quarterly client dinner (bulk booking)", "Sales", "receipts/104-dinner.pdf" },
                { 105, "Vikram Rao", "vikram.rao@example.com", "FINANCE", "OFFICE", 4000.0,
                        "Printer cartridges and stationery", "Finance", "receipts/105-office.pdf" },
                { 106, "Ananya Iyer", "ananya.iyer@example.com", "ADMIN", "OFFICE", 55000.0,
                        "New workstation setup for new hires", "Administration", "receipts/106-workstation.pdf" },
                { 107, "Karan Verma", "karan.verma@example.com", "ADMIN", "TRAVEL", 95000.0,
                        "International conference travel", "Administration", "receipts/107-travel.pdf" },
                { 108, "Divya Reddy", "divya.reddy@example.com", "EMPLOYEE", "OFFICE", 2000.0,
                        "Notebooks and pens", "Engineering", "receipts/108-stationery.pdf" }
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
                ps.setString(8, (String) row[7]);
                ps.setString(9, (String) row[8]);
                ps.addBatch();
            }
            ps.executeBatch();
            System.out.println("Seeded " + sampleRows.length + " sample pending expense claims.");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to seed sample data: " + e.getMessage(), e);
        }
    }

    public List<PendingExpense> fetchPending() {
        String sql = "SELECT id, employee_id, employee_name, employee_email, role, expense_type, amount, " +
                "description, department, receipt_reference " +
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
                        rs.getString("description"),
                        rs.getString("department"),
                        rs.getString("receipt_reference")));
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
                " department, receipt_reference, final_status, remarks, transaction_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, pe.getId());
            ps.setInt(2, pe.getEmployeeId());
            ps.setString(3, pe.getEmployeeName());
            ps.setString(4, pe.getEmployeeEmail());
            ps.setString(5, pe.getRole());
            ps.setString(6, pe.getExpenseType());
            ps.setDouble(7, pe.getAmount());
            ps.setString(8, pe.getDescription());
            ps.setString(9, pe.getDepartment());
            ps.setString(10, pe.getReceiptReference() == null ? "" : pe.getReceiptReference());
            ps.setString(11, finalStatus);
            ps.setString(12, remarks);
            ps.setString(13, transactionId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert into processed_expenses: " + e.getMessage(), e);
        }
    }

    public int insertPending(PendingExpense expense) {
        String sql = "INSERT INTO pending_expenses " +
                "(employee_id, employee_name, employee_email, role, expense_type, amount, description, department, receipt_reference) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, expense.getEmployeeId());
            ps.setString(2, expense.getEmployeeName());
            ps.setString(3, expense.getEmployeeEmail());
            ps.setString(4, expense.getRole());
            ps.setString(5, expense.getExpenseType());
            ps.setDouble(6, expense.getAmount());
            ps.setString(7, expense.getDescription());
            ps.setString(8, expense.getDepartment());
            ps.setString(9, expense.getReceiptReference() == null ? "" : expense.getReceiptReference());
            ps.executeUpdate();
            try (Statement statement = connection.createStatement();
                    ResultSet result = statement.executeQuery("SELECT last_insert_rowid()")) {
                if (result.next()) {
                    return result.getInt(1);
                }
            }
            throw new SQLException("SQLite did not return the inserted expense ID.");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert pending expense: " + e.getMessage(), e);
        }
    }

    public List<ProcessedExpense> fetchProcessedExpenses() {
        String sql = "SELECT pending_id, employee_id, employee_name, role, department, expense_type, amount, " +
                "description, final_status, remarks, transaction_id, processed_at " +
                "FROM processed_expenses ORDER BY processed_at, id";
        List<ProcessedExpense> result = new ArrayList<>();
        try (Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                result.add(new ProcessedExpense(
                        rs.getInt("pending_id"),
                        rs.getInt("employee_id"),
                        rs.getString("employee_name"),
                        rs.getString("role"),
                        rs.getString("department"),
                        rs.getString("expense_type"),
                        rs.getDouble("amount"),
                        rs.getString("description"),
                        rs.getString("final_status"),
                        rs.getString("remarks"),
                        rs.getString("transaction_id"),
                        rs.getString("processed_at")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch processed expenses: " + e.getMessage(), e);
        }
        return result;
    }

    private void ensureColumnExists(String table, String column, String declaration) throws SQLException {
        try (Statement statement = connection.createStatement();
                ResultSet columns = statement.executeQuery("PRAGMA table_info(" + table + ")")) {
            while (columns.next()) {
                if (column.equalsIgnoreCase(columns.getString("name"))) {
                    return;
                }
            }
        }
        try (Statement statement = connection.createStatement()) {
            statement.execute("ALTER TABLE " + table + " ADD COLUMN " + column + " " + declaration);
        }
    }
}
