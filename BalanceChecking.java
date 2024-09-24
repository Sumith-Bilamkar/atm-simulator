package atmsimulator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BalanceChecking {

    public void checkBalance(String cardNumber) throws Exception {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT a.Balance FROM Accounts a JOIN Cards c ON a.AccountNumber = c.AccountNumber WHERE c.CardNumber = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, cardNumber);

        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            System.out.println("Your current balance is: " + rs.getDouble("Balance"));
        }
    }
}

