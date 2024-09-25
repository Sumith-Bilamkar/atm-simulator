package atmsimulator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CashDeposit {

    public void deposit(String cardNumber, double amount) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();

            // Deposit amount
            String depositSql = "UPDATE Accounts SET Balance = Balance + ? WHERE AccountNumber = (SELECT AccountNumber FROM Cards WHERE CardNumber = ?)";
            stmt = conn.prepareStatement(depositSql);
            stmt.setDouble(1, amount);
            stmt.setString(2, cardNumber);
            stmt.executeUpdate();

            // Get userID
            String userIdSql = "SELECT UserID FROM Cards WHERE CardNumber = ?";
            stmt = conn.prepareStatement(userIdSql);
            stmt.setString(1, cardNumber);
            rs = stmt.executeQuery();

            int userId = -1;
            if (rs.next()) {
                userId = rs.getInt("UserID");
            }

            // Log transaction
            Logging.logTransaction(cardNumber, "Deposit", amount, userId);

            System.out.println("Deposit successful! Amount deposited: " + amount);

        } catch (Exception e) {
            System.err.println("Error during deposit: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }
}
