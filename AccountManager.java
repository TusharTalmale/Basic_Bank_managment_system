package BankManage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class AccountManager {
    private Connection con;
    private Scanner sc;

    AccountManager(Connection con, Scanner sc) {
        this.con = con;
        this.sc = sc;
    }

    public void credit_money(long acc_num) throws SQLException {
        sc.nextLine();
        System.out.println("Enter Amount :");
        double amount = sc.nextDouble();

        sc.nextLine();
        System.out.println("Enter pin:");
        String security_pin = sc.nextLine();

        try {
            con.setAutoCommit(false);

            if (isValidPin(acc_num, security_pin)) {
                String credit_query = "UPDATE Accounts SET balance = balance + ? WHERE acc_num = ?";
                PreparedStatement preparedStatement1 = con.prepareStatement(credit_query);
                preparedStatement1.setDouble(1, amount);
                preparedStatement1.setLong(2, acc_num);
                int rowAffected = preparedStatement1.executeUpdate();
                if (rowAffected > 0) {
                    System.out.println("Rs. " + amount + " credited successfully!");
                    con.commit();
                } else {
                    System.out.println("Transaction Failed");
                    con.rollback();
                }
            } else {
                System.out.println("Invalid Security Pin!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            con.rollback();
        } finally {
            con.setAutoCommit(true);
        }
    }

    public void debit_money(long acc_num) throws SQLException {
        sc.nextLine();
        System.out.println("Enter amount you want to debit: ");
        double amount = sc.nextDouble();

        sc.nextLine();
        System.out.println("Enter pin:");
        String security_pin = sc.nextLine();

        try {
            con.setAutoCommit(false);

            if (isValidPin(acc_num, security_pin)) {
                double current_balance = getBalance(acc_num);

                if (amount <= current_balance) {
                    String debit_query = "UPDATE Accounts SET balance = balance - ? WHERE acc_num = ?";
                    PreparedStatement preparedStatement1 = con.prepareStatement(debit_query);
                    preparedStatement1.setDouble(1, amount);
                    preparedStatement1.setLong(2, acc_num);
                    int rowsAffected = preparedStatement1.executeUpdate();

                    if (rowsAffected > 0) {
                        System.out.println("Rs. " + amount + " debited successfully!");
                        con.commit();
                    } else {
                        System.out.println("Transaction Failed!");
                        con.rollback();
                    }
                } else {
                    System.out.println("Insufficient Balance!");
                }
            } else {
                System.out.println("Invalid Security Pin!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            con.rollback();
        } finally {
            con.setAutoCommit(true);
        }
    }

    public double getBalance(long acc_num) throws SQLException {
        sc.nextLine();
        System.out.println("Enter Security pin:");
        String security_pin = sc.nextLine();

        try {
            if (isValidPin(acc_num, security_pin)) {
                PreparedStatement preparedStatement = con.prepareStatement(
                        "SELECT balance FROM Accounts WHERE acc_num = ? AND security_pin = ?");
                preparedStatement.setLong(1, acc_num);
                preparedStatement.setString(2, security_pin);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    double balance = resultSet.getDouble("balance");
                    System.out.println("Balance: " + balance);
                    return balance;
                }
            } else {
                System.out.println("Invalid Pin!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void transfer_money(long sender_acc_num) throws SQLException {
        sc.nextLine();
        System.out.println("Enter receiver Account number:");
        long receiver_acc_num = sc.nextLong();

        System.out.println("Enter Amount:");
        double amount = sc.nextDouble();
        sc.nextLine();

        System.out.println("Enter Security Pin:");
        String security_pin = sc.nextLine();

        try {
            con.setAutoCommit(false);

            if (isValidPin(sender_acc_num, security_pin)) {
                double sender_balance = getBalance(sender_acc_num);
                if (amount <= sender_balance) {
                    PreparedStatement debit = con.prepareStatement("UPDATE Accounts SET balance = balance - ? WHERE acc_num = ?");
                    PreparedStatement credit = con.prepareStatement("UPDATE Accounts SET balance = balance + ? WHERE acc_num = ?");
                    debit.setDouble(1, amount);
                    debit.setLong(2, sender_acc_num);
                    credit.setDouble(1, amount);
                    credit.setLong(2, receiver_acc_num);

                    int rowAffected1 = debit.executeUpdate();
                    int rowAffected2 = credit.executeUpdate();
                    if (rowAffected1 > 0 && rowAffected2 > 0) {
                        System.out.println("Rs." + amount + " transferred successfully!");
                        con.commit();
                    } else {
                        System.out.println("Transaction Failed!");
                        con.rollback();
                    }
                } else {
                    System.out.println("Insufficient Balance!");
                }
            } else {
                System.out.println("Invalid Security Pin!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            con.rollback();
        } finally {
            con.setAutoCommit(true);
        }
    }

    private boolean isValidPin(long acc_num, String security_pin) throws SQLException {
        PreparedStatement preparedStatement = con.prepareStatement(
                "SELECT * FROM Accounts WHERE acc_num = ? AND security_pin = ?");
        preparedStatement.setLong(1, acc_num);
        preparedStatement.setString(2, security_pin);
        ResultSet resultSet = preparedStatement.executeQuery();
        return resultSet.next();
    }
}
