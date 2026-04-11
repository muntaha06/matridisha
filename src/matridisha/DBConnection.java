package matridisha;

import java.sql.*;

public class DBConnection {

    // ===============================
    // 1️⃣ Database Connection Method
    // ===============================
    public static Connection getConnection() {
        Connection conn = null;
        try {
            // Configuration for MySQL (XAMPP Default)
            String url = "jdbc:mysql://localhost:3306/matridisha_db";
            String user = "root"; // Default XAMPP user
            String pass = "";     // Default XAMPP password
            
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Establish Connection
            conn = DriverManager.getConnection(url, user, pass);
            
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Database Connection Failed: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
        
        return conn;
    }
}