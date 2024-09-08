package BankManage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class BankingApp {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Connection con = null;
        Scanner sc = null;
        try {
            // Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish the database connection
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/bank_sys", // Replace with your database name
                    "root",  // Replace with your MySQL username
                    "Tushar" // Replace with your MySQL password
            );

            sc = new Scanner(System.in);
            User user = new User(con, sc);
            AccountManager accountManager = new AccountManager(con, sc);

            String email;
            long account_number = 0;

            while (true) {
                System.out.println("*** Welcome To Bank ***"
                        + "\n"
                        + "1. Register \n"
                        + "2. Login \n"
                        + "3. Exit \n"
                );
                System.out.println("------------------------");
                System.out.println("Enter your choice: ");
                int choice = sc.nextInt();

                switch (choice) {
                    case 1:
                        user.register();
                        break;

                    case 2:
                        email = user.login();
                        if (email != null) {
                            System.out.println("Login successful! Enter your account number: ");
                            account_number = sc.nextLong();
                            System.out.println("Your Password is your Pin. ");
                            while (true) {
                                System.out.println("\nAccount Menu:"
                                        + "\n1. Check Balance"
                                        + "\n2. Credit Money"
                                        + "\n3. Debit Money"
                                        + "\n4. Transfer Money"
                                        + "\n5. Logout\n");
                                System.out.println("Enter your choice: ");
                                int accChoice = sc.nextInt();
                                switch (accChoice) {
                                    case 1:
                                        accountManager.getBalance(account_number);
                                        break;
                                    case 2:
                                        accountManager.credit_money(account_number);
                                        break;
                                    case 3:
                                        accountManager.debit_money(account_number);
                                        break;
                                    case 4:
                                        accountManager.transfer_money(account_number);
                                        break;
                                    case 5:
                                        System.out.println("Logged out successfully!");
                                        break;
                                    default:
                                        System.out.println("Invalid choice. Please try again.");
                                }
                                if (accChoice == 5) {
                                    break;
                                }
                            }
                        } else {
                            System.out.println("Login failed. Invalid credentials.");
                        }
                        break;

                    case 3:
                        System.out.println("Exiting the system. Goodbye!");
                        return;

                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }

        } catch (ClassNotFoundException e) {
            System.out.println("JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (sc != null) {
                sc.close();
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    System.out.println("Failed to close the connection: " + e.getMessage());
                }
            }
        }
    }
}
