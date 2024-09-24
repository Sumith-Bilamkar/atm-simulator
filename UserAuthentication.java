package atmsimulator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserAuthentication {

    public boolean authenticateUser(String cardNumber, String pin) throws Exception {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT * FROM Cards WHERE CardNumber = ? AND PIN = ? AND IsActive = TRUE";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, cardNumber);
        stmt.setString(2, pin);  // Assume pin is already hashed

        ResultSet rs = stmt.executeQuery();
        boolean isAuthenticated = rs.next();
        logLoginAttempt(cardNumber, isAuthenticated);
        return isAuthenticated;
    }

    private void logLoginAttempt(String cardNumber, boolean success) throws Exception {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "INSERT INTO LoginAttempts (CardNumber, AttemptTimestamp, Successful) VALUES (?, NOW(), ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, cardNumber);
        stmt.setBoolean(2, success);
        stmt.executeUpdate();
    }
}

