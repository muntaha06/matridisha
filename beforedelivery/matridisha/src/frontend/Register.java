package frontend;

import db.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class Register {
    public boolean registerUser(String name, int age, double weight, int weeks, 
                               String prevChild, String deliveryType, 
                               String username, String password) {
        try {
            Connection con = DBConnection.getConnection();
            String sql = "INSERT INTO users (name, age, weight, weeks, prev_child, delivery_type, username, password) VALUES (?,?,?,?,?,?,?,?)";
            PreparedStatement ps = con.prepareStatement(sql);

            // Mapping parameters to SQL query
            ps.setString(1, name);
            ps.setInt(2, age);
            ps.setDouble(3, weight);
            ps.setInt(4, weeks);
            ps.setString(5, prevChild);
            ps.setString(6, deliveryType);
            ps.setString(7, username);
            ps.setString(8, password);

            int result = ps.executeUpdate();
            return result > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}