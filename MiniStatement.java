package atmsimulator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MiniStatement {

    public void generateMiniStatement(String cardNumber) throws Exception {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT t.TransactionType, u.FirstName, t.Amount, t.Timestamp " +
                "FROM Transactions t " +
                "JOIN Accounts a ON t.AccountNumber = a.AccountNumber " +
                "JOIN Cards c ON a.AccountNumber = c.AccountNumber " +
                "JOIN Users u ON u.UserID = c.UserID " +
                "WHERE c.CardNumber = ? " +
                "ORDER BY t.Timestamp DESC " +
                "LIMIT 5;";

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, cardNumber);

        ResultSet rs = stmt.executeQuery();
        System.out.println("Mini Statement:");
        while (rs.next()) {
            System.out.println(rs.getString("FirstName") + ": " + rs.getString("TransactionType") +
                    " - " + rs.getDouble("Amount") + " on " + rs.getTimestamp("Timestamp"));
        }
    }
}

