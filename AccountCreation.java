package atmsimulator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Random;

public class AccountCreation {

    // Method to create a new account with a new card
    public void createNewAccount(String firstName, String lastName, String accountType) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // Generate AccountNumber, CardNumber, PIN, and ExpirationDate
            String accountNumber = generateAccountNumber();
            String cardNumber = generateCardNumber();
            String pin = generatePIN();
            String expirationDate = "2026-12-31"; // Example expiration date

            // Insert into Accounts with the generated AccountNumber
            String accountSql = "INSERT INTO Accounts (AccountNumber, AccountType) VALUES (?, ?)";
            stmt = conn.prepareStatement(accountSql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, accountNumber);
            stmt.setString(2, accountType);
            stmt.executeUpdate();

            // Get the newly created AccountID (if needed)
            rs = stmt.getGeneratedKeys();
            int accountId = rs.next() ? rs.getInt(1) : -1;

            // Insert into Users first to get UserID
            String userSql = "INSERT INTO Users (FirstName, LastName) VALUES (?, ?)";
            stmt = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.executeUpdate();

            // Get the newly created UserID
            rs = stmt.getGeneratedKeys();
            int userId = rs.next() ? rs.getInt(1) : -1;

            // Now insert into Cards, including the UserID
            String cardSql = "INSERT INTO Cards (CardNumber, AccountNumber, PIN, ExpirationDate, UserID) VALUES (?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(cardSql);
            stmt.setString(1, cardNumber);
            stmt.setString(2, accountNumber);
            stmt.setString(3, pin);
            stmt.setString(4, expirationDate);
            stmt.setInt(5, userId);  // Set the UserID
            stmt.executeUpdate();

            // Commit the transaction
            conn.commit();
            System.out.println("New account and card created!");
            System.out.println("Account Number: " + accountNumber);
            System.out.println("Card Number: " + cardNumber);
            System.out.println("PIN: " + pin);

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Roll back any changes if an error occurs
                    System.err.println("Transaction rolled back due to: " + e.getMessage());
                } catch (Exception rollbackEx) {
                    System.err.println("Error during rollback: " + rollbackEx.getMessage());
                }
            }
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

    // Method to add a new card to an existing account for an existing user
    public void addCardToExistingAccount(String accountNumber, String firstName, String lastName) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // Check the number of existing users for the account
            String countUsersSql = "SELECT COUNT(DISTINCT UserID) FROM Cards WHERE AccountNumber = ?";
            stmt = conn.prepareStatement(countUsersSql);
            stmt.setString(1, accountNumber);
            rs = stmt.executeQuery();

            int userCount = 0;
            if (rs.next()) {
                userCount = rs.getInt(1);
            }

            // If the account already has 2 users, reject the request
            if (userCount >= 2) {
                System.out.println("Cannot add more users. This account already has 2 users.");
                return; // Exit the method
            }

            // Check if the user exists
            String checkUserSql = "SELECT UserID FROM Users WHERE FirstName = ? AND LastName = ?";
            stmt = conn.prepareStatement(checkUserSql);
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            rs = stmt.executeQuery();

            int userId = -1;
            if (rs.next()) {
                userId = rs.getInt("UserID");
            } else {
                // If user doesn't exist, add new user
                String addUserSql = "INSERT INTO Users (FirstName, LastName) VALUES (?, ?)";
                stmt = conn.prepareStatement(addUserSql, Statement.RETURN_GENERATED_KEYS);
                stmt.setString(1, firstName);
                stmt.setString(2, lastName);
                stmt.executeUpdate();

                rs = stmt.getGeneratedKeys();
                userId = rs.next() ? rs.getInt(1) : -1;
            }

            // Generate CardNumber and PIN
            String cardNumber = generateCardNumber();
            String pin = generatePIN();
            String expirationDate = "2026-12-31"; // Example expiration date

            // Insert new card into Cards table
            String cardSql = "INSERT INTO Cards (CardNumber, AccountNumber, PIN, ExpirationDate, UserID) VALUES (?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(cardSql);
            stmt.setString(1, cardNumber);
            stmt.setString(2, accountNumber);
            stmt.setString(3, pin);
            stmt.setString(4, expirationDate);
            stmt.setInt(5, userId);
            stmt.executeUpdate();

            // Commit the transaction
            conn.commit();
            System.out.println("New card added to existing account!");
            System.out.println("Account Number: " + accountNumber);
            System.out.println("Card Number: " + cardNumber);
            System.out.println("PIN: " + pin);

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Roll back any changes if an error occurs
                    System.err.println("Transaction rolled back due to: " + e.getMessage());
                } catch (Exception rollbackEx) {
                    System.err.println("Error during rollback: " + rollbackEx.getMessage());
                }
            }
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

    // Utility method to generate a random 16-digit account number
    private String generateAccountNumber() {
        String prefix = "169241"; // Fixed prefix for the first 6 digits
        Random random = new Random();
        StringBuilder accountNumber = new StringBuilder(prefix);

        for (int i = 0; i < 10; i++) {
            accountNumber.append(random.nextInt(10)); // Generate the last 10 random digits
        }
        return accountNumber.toString();
    }

    // Utility method to generate a random 10-digit card number
    private String generateCardNumber() {
        Random random = new Random();
        StringBuilder cardNumber = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            cardNumber.append(random.nextInt(10));
        }
        return cardNumber.toString();
    }

    // Utility method to generate a random 4-digit PIN
    private String generatePIN() {
        Random random = new Random();
        int pin = 1000 + random.nextInt(9000); // Generates a 4-digit PIN
        return String.valueOf(pin);
    }
}
