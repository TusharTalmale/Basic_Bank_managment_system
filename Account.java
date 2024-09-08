package BankManage;

import java.sql.*;
//import java.util.Random;
import java.util.Scanner;

public class Account {
    private Connection con;
    private Scanner sc;

    public Account(Connection con, Scanner sc) {
        this.con = con;
        this.sc = sc;
    }


    // Method to open a new account
    public long open_account(String email) {
        if (account_exist(email)) {
            System.out.println("Account already exists for this email.");
            return -1;
        }

        String open_account_query = "INSERT INTO Accounts(acc_num, full_name, email, balance, security_pin) VALUES(?, ?, ?, ?, ?)";
        sc.nextLine();  // Consume leftover newline

        System.out.println("Enter Full Name: ");
        String full_name = sc.nextLine();

        System.out.println("Enter Initial Amount: ");
        double balance = sc.nextDouble();
        sc.nextLine();  // Consume the newline left by nextDouble()

        System.out.println("Enter Security Pin: ");
        String security_pin = sc.nextLine();

        try {
            long acc_num = generateAccountNumber();  // Generate a new account number
            PreparedStatement preparedStatement = con.prepareStatement(open_account_query);
            preparedStatement.setLong(1, acc_num);
            preparedStatement.setString(2, full_name);
            preparedStatement.setString(3, email);
            preparedStatement.setDouble(4, balance);
            preparedStatement.setString(5, security_pin);

            // Execute the SQL query
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Account successfully opened with Account Number: " + acc_num);
                return acc_num;
            } else {
                System.out.println("Failed to open account. Please try again.");
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    // Method to get account number by email
    public long getAccount_number(String email) {
        String query = "SELECT acc_num FROM Accounts WHERE email = ?";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();

            // Check if a result was returned
            if (resultSet.next()) {
                return resultSet.getLong("acc_num");  // Return the account number
            } else {
                System.out.println("No account found for this email. Please create an account first.");
//                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
//            return -1;
        }
        throw new RuntimeException("Account not Present");
    }

    // Helper method to generate a random 10-digit account number
    private long generateAccountNumber() {
        try {
            Statement statement = con.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT acc_num from Accounts ORDER BY acc_num DESC LIMIT 1");
            if (resultSet.next()) {
                long last_account_number = resultSet.getLong("acc_num");
                return last_account_number+1;
            } else {
                return 10000100;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return 10000100;
    }



    // Method to check if an account exists for a given email
    public boolean account_exist(String email) {
        String query = "SELECT acc_num FROM Accounts WHERE email = ?";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();  // Return true if account exists, false otherwise
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
