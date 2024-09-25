package atmsimulator;

import java.util.Scanner;

public class ATMSimulator {
    public static boolean loggedIn;

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        loggedIn = false;
        String cardNumber = "";

        // Display the welcome message only once
        System.out.println("Welcome to the ATM Management System");

        // Main application loop
        while (true) {
            try {
                if (!loggedIn) {
                    System.out.println("\n1. Login");
                    System.out.println("2. Create Account");
                    System.out.println("3. Exit");
                    System.out.print("Please select an option: ");
                    int option = scanner.nextInt();

                    switch (option) {
                        case 1:
                            // Login functionality
                            System.out.print("Enter card number: ");
                            cardNumber = scanner.next();
                            System.out.print("Enter PIN: ");
                            String pin = scanner.next();
                            UserAuthentication auth = new UserAuthentication();
                            loggedIn = auth.authenticateUser(cardNumber, pin);

                            if (loggedIn) {
                                System.out.println("Login successful!");
                                mainMenu(cardNumber);  // After login, direct to the main menu
                            } else {
                                System.out.println("Invalid card number or PIN. Try again.");
                            }
                            break;

                        case 2:
                            // Account creation functionality
                            System.out.println("Account Creation");
                            System.out.println("1. Create new bank account");
                            System.out.println("2. Add card to existing bank account");
                            System.out.print("Please select an option: ");
                            int createOption = scanner.nextInt();

                            if (createOption == 1) {
                                System.out.print("Enter first name: ");
                                String firstName = scanner.next();
                                System.out.print("Enter last name: ");
                                String lastName = scanner.next();
                                System.out.print("Enter account type (Savings/Checking): ");
                                String accountType = scanner.next();

                                AccountCreation accountCreation = new AccountCreation();
                                accountCreation.createNewAccount(firstName, lastName, accountType);

                            } else if (createOption == 2) {
                                System.out.print("Enter first name: ");
                                String firstName = scanner.next();
                                System.out.print("Enter last name: ");
                                String lastName = scanner.next();
                                scanner.nextLine();  // Consume the leftover newline
                                System.out.print("Enter existing account: ");
                                String accountNumber = scanner.nextLine();  // Use nextLine to read the account ID

                                AccountCreation accountCreation = new AccountCreation();
                                accountCreation.addCardToExistingAccount(accountNumber, firstName, lastName);

                            } else {
                                System.out.println("Invalid option.");
                            }
                            break;

                        case 3:
                            System.out.println("Thank you for using the ATM. Goodbye!");
                            System.exit(0);

                        default:
                            System.out.println("Invalid option. Please try again.");
                    }
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    // Main menu after successful login
    public static void mainMenu(String cardNumber) throws Exception {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("\nATM Main Menu:");
            System.out.println("1. Balance Check");
            System.out.println("2. Cash Withdrawal");
            System.out.println("3. Cash Deposit");
            System.out.println("4. PIN Change");
            System.out.println("5. Mini Statement");
            System.out.println("6. Logout");
            System.out.print("Please select an option: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    BalanceChecking balanceChecking = new BalanceChecking();
                    balanceChecking.checkBalance(cardNumber);
                    break;

                case 2:
                    System.out.print("Enter amount to withdraw: ");
                    double withdrawalAmount = scanner.nextDouble();
                    CashWithdrawal cashWithdrawal = new CashWithdrawal();
                    cashWithdrawal.withdraw(cardNumber, withdrawalAmount);
                    break;

                case 3:
                    System.out.print("Enter amount to deposit: ");
                    double depositAmount = scanner.nextDouble();
                    CashDeposit cashDeposit = new CashDeposit();
                    cashDeposit.deposit(cardNumber, depositAmount);
                    break;

                case 4:
                    System.out.print("Enter old PIN: ");
                    String oldPIN = scanner.next();
                    System.out.print("Enter new PIN: ");
                    String newPIN = scanner.next();
                    PINChange pinChange = new PINChange();
                    pinChange.changePIN(cardNumber, oldPIN, newPIN);
                    break;

                case 5:
                    MiniStatement miniStatement = new MiniStatement();
                    miniStatement.generateMiniStatement(cardNumber);
                    break;

                case 6:
                    System.out.println("Logging out...");
                    exit = true;  // Return to login screen
                    break;

                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
        loggedIn = false;
    }
}
