package atmsimulator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PINChange {

    public void changePIN(String cardNumber, String oldPIN, String newPIN) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();

            // Verify old PIN
            String verifySql = "SELECT PIN FROM Cards WHERE CardNumber = ?";
            stmt = conn.prepareStatement(verifySql);
            stmt.setString(1, cardNumber);
            rs = stmt.executeQuery();

            if (rs.next()) {
                String currentPIN = rs.getString("PIN");

                // Assuming PINs are hashed; compare hashed values
                if (currentPIN.equals(oldPIN)) { // Here you would check hashed PINs
                    // Update PIN
                    String updatePinSql = "UPDATE Cards SET PIN = ? WHERE CardNumber = ?";
                    stmt = conn.prepareStatement(updatePinSql);
                    stmt.setString(1, newPIN); // Use hashed new PIN
                    stmt.setString(2, cardNumber);
                    stmt.executeUpdate();

                    System.out.println("PIN successfully changed!");
                } else {
                    System.out.println("Old PIN is incorrect.");
                }
            } else {
                System.out.println("Card not found.");
            }

        } catch (Exception e) {
            System.err.println("Error during PIN change: " + e.getMessage());
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

