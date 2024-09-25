package atmsimulator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CashWithdrawal {

    public void withdraw(String cardNumber, double amount) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();

            // Check balance
            String checkBalanceSql = "SELECT Balance FROM Accounts WHERE AccountNumber= (SELECT AccountNumber FROM Cards WHERE CardNumber = ?)";
            stmt = conn.prepareStatement(checkBalanceSql);
            stmt.setString(1, cardNumber);
            rs = stmt.executeQuery();

            if (rs.next()) {
                double balance = rs.getDouble("Balance");

                // Check if enough balance exists
                if (balance >= amount) {
                    // Withdraw and update balance
                    String withdrawSql = "UPDATE Accounts SET Balance = Balance - ? WHERE AccountNumber = (SELECT AccountNumber FROM Cards WHERE CardNumber = ?)";
                    stmt = conn.prepareStatement(withdrawSql);
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
                    Logging.logTransaction(cardNumber, "Withdrawal", amount, userId);

                    System.out.println("Withdrawal successful! Amount withdrawn: " + amount);
                } else {
                    System.out.println("Insufficient balance.");
                }
            } else {
                System.out.println("Account not found.");
            }

        } catch (Exception e) {
            System.err.println("Error during withdrawal: " + e.getMessage());
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
