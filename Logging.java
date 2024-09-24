package atmsimulator;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class Logging {

    public static void logTransaction(String cardNumber, String type, double amount) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DatabaseConnection.getConnection();

            String sql = "INSERT INTO Transactions (AccountNumber, CardNumber, TransactionType, Amount, Timestamp) VALUES ((SELECT AccountNumber FROM Cards WHERE CardNumber = ?), ?, ?, ?, NOW())";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, cardNumber);
            stmt.setString(2, cardNumber);

            stmt.setString(3, type);
            stmt.setDouble(4, amount);
            stmt.executeUpdate();

        } catch (Exception e) {
            System.err.println("Error logging transaction: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }
}

