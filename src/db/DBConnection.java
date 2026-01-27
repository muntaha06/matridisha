package db;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    public static Connection getConnection() {
        Connection con = null;
        try {
            // Loading MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/matridisha",
                "root",
                "" // Default XAMPP password is empty
            );
        } catch (Exception e) {
            System.out.println("Database connection failed");
            e.printStackTrace();
        }
        return con;
    }
}