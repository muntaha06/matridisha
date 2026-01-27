package frontend;

import db.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Login {
    // Method to validate user credentials and return user details
    public String[] loginUser(String username, String password) {
        String[] userData = null;
        try {
            Connection con = DBConnection.getConnection();
            String sql = "SELECT name, weeks FROM users WHERE username=? AND password=?";
            PreparedStatement ps = con.prepareStatement(sql);
            
            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Fetching name and weeks to show on dashboard
                userData = new String[2];
                userData[0] = rs.getString("name");
                userData[1] = String.valueOf(rs.getInt("weeks"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userData;
    }
}