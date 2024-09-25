package atmsimulator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MiniStatement {

    public void generateMiniStatement(String cardNumber) throws Exception {
        Connection conn = DatabaseConnection.getConnection();

        // Step 1: Fetch the account number associated with the given card number
        String fetchAccountSQL = "SELECT AccountNumber FROM Cards WHERE CardNumber = ?";
        PreparedStatement fetchAccountStmt = conn.prepareStatement(fetchAccountSQL);
        fetchAccountStmt.setString(1, cardNumber);

        ResultSet rsAccount = fetchAccountStmt.executeQuery();

        if (rsAccount.next()) {
            String accountNumber = rsAccount.getString("AccountNumber");

            // Step 2: Fetch all transactions for the account number
            String sql = "SELECT t.TransactionType, u.FirstName, t.Amount, t.Timestamp " +
                    "FROM Transactions t " +
                    "JOIN Users u ON t.UserID = u.UserID " +
                    "WHERE t.AccountNumber = ? " +
                    "ORDER BY t.Timestamp DESC " +
                    "LIMIT 5;";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, accountNumber);

            ResultSet rs = stmt.executeQuery();
            System.out.println("Mini Statement for Account Number: " + accountNumber);

            if (!rs.isBeforeFirst()) { // Check if the result set is empty
                System.out.println("There are no transactions available for this account.");
                System.out.println("Please ensure you have made transactions previously.");
            } else {
                while (rs.next()) {
                    System.out.println(rs.getString("FirstName") + ": " + rs.getString("TransactionType") +
                            " - " + rs.getDouble("Amount") + " on " + rs.getTimestamp("Timestamp"));
                }
            }
        } else {
            System.out.println("No account found for the provided card number.");
        }
    }
}
