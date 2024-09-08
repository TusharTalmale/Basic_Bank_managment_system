package BankManage;

import java.sql.*;
import java.util.Scanner;

public class User {
    private Connection con;
    private Scanner sc ;

    public User(Connection con,Scanner sc){

        this.con = con;
        this.sc = sc;
    }
    public void register(){
        long acc_num = generateAccountNumber();  // Generate a new account number

        sc.nextLine();
        System.out.println("Full Name: ");
        String full_name = sc.nextLine();
        System.out.println("Email :");
        String email = sc.nextLine();
        System.out.println("Password : ");
        String password = sc.nextLine();
        if(user_exist(email)){
            System.out.println("User is Alredy Present plz Log in !!");
            return;
        }
        String register_query = "Insert into accounts(full_name,email,security_pin,acc_num) values (?,?,?,?)";
        try{
            PreparedStatement preparedStatement = con.prepareStatement(register_query);
            preparedStatement.setString(1,full_name);
            preparedStatement.setString(2,email);
            preparedStatement.setString(3,password);
            preparedStatement.setLong(4,  acc_num); // Set the account number
            int affectedRows = preparedStatement.executeUpdate();
            if(affectedRows > 0){
                System.out.println("Registration Successful , with you ACCOUNT Number is "+acc_num );
                           }
            else{
                System.out.println("Registration Failed!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String login(){
        sc.nextLine();
        System.out.println("Email :");
        String email = sc.nextLine();
        System.out.println("Password : ");
        String password = sc.nextLine();
        String login_query = "select * FROM Accounts Where email =? AND security_pin = ?";
        try{
            PreparedStatement preparedStatement = con.prepareStatement(login_query);
            preparedStatement.setString(1,email);
            preparedStatement.setString(2,password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                return email;
            }else{
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public boolean user_exist(String email){
        String query = "SELECT * FROM Accounts Where email = ?";
        try{
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1,email);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

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

}
