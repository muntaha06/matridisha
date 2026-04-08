package matridisha;

import java.sql.*;

public class DBConnection {
    public static Connection getConnection() {
        Connection conn = null;
        try {
            // Database name: matridisha_db
            String url = "jdbc:mysql://localhost:3306/matridisha_db";
            String user = "root"; // Default XAMPP user
            String pass = "";     // Default XAMPP password
            
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, user, pass);
        } catch (Exception e) {
            System.out.println("Database Connection Failed: " + e.getMessage());
        }
        return conn;
    }
}