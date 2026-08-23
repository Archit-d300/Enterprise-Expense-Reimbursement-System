package db;

import model.ExpenseClaim;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ExpenseClaimDAO {

    private Connection getConnection() {
        return DatabaseConnection.getInstance().getConnection();
    }

    public void saveClaim(ExpenseClaim claim) {
        String sql = "INSERT INTO expense_claims(id, employee_id, amount, email, description, status) VALUES(?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, claim.getId());
            stmt.setInt(2, claim.getEmployeeId());
            stmt.setDouble(3, claim.getAmount());
            stmt.setString(4, claim.getEmail());
            stmt.setString(5, claim.getDescription());
            stmt.setString(6, claim.getStatus());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving claim: " + e.getMessage());
        }
    }

    public void updateClaimStatus(int claimId, String newStatus) {
        String sql = "UPDATE expense_claims SET status = ? WHERE id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, newStatus);
            stmt.setInt(2, claimId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating claim status: " + e.getMessage());
        }
    }

    public void saveOrUpdate(ExpenseClaim claim) {
        String checkSql = "SELECT COUNT(*) FROM expense_claims WHERE id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(checkSql)) {
            stmt.setInt(1, claim.getId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                updateClaimStatus(claim.getId(), claim.getStatus());
            } else {
                saveClaim(claim);
            }
        } catch (SQLException e) {
            System.err.println("Error in saveOrUpdate: " + e.getMessage());
        }
    }
}
