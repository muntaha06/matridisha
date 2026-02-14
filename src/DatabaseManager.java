import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DatabaseManager {

    // ===== SQL Server Configuration =====
    private static final String URL =
            "jdbc:sqlserver://localhost\\SQLEXPRESS;databaseName=AnikaDatabase;encrypt=true;trustServerCertificate=true;";

    private static final String USER = "projectuser";
    private static final String PASSWORD = "Project@123";

    // Load SQL Server Driver
    public DatabaseManager() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // ===============================
    // 1️⃣ User Registration Method
    // ===============================
    public boolean registerUser(String fullName, int age, double weight, int week,
                                String hasChild, String deliveryType,
                                String medicalIssue, String username, String password) {

        String sql = "INSERT INTO MatriUsers " +
                "(FullName, Age, Weight, PregnancyWeek, HasPreviousChild, DeliveryType, MedicalIssue, Username, Password) " +
                "VALUES (?,?,?,?,?,?,?,?,?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, fullName);
            pstmt.setInt(2, age);
            pstmt.setDouble(3, weight);
            pstmt.setInt(4, week);
            pstmt.setString(5, hasChild);
            pstmt.setString(6, deliveryType);
            pstmt.setString(7, medicalIssue);
            pstmt.setString(8, username);
            pstmt.setString(9, password);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===============================
    // 2️⃣ Login Validation Method
    // ===============================
    public boolean validateLogin(String username, String password) {

        String sql = "SELECT * FROM MatriUsers WHERE Username = ? AND Password = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===============================
    // 3️⃣ Password Reset Method
    // ===============================
    public boolean resetPassword(String username, String newPassword) {

        String sql = "UPDATE MatriUsers SET Password = ? WHERE Username = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newPassword);
            pstmt.setString(2, username);

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===============================
    // 4️⃣ Fetch User Profile Method
    // ===============================
    public Map<String, String> getUserProfile(String username) {

        Map<String, String> profile = new HashMap<>();

        String sql = "SELECT * FROM MatriUsers WHERE Username = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                profile.put("fullName", rs.getString("FullName"));
                profile.put("age", String.valueOf(rs.getInt("Age")));
                profile.put("weight", String.valueOf(rs.getDouble("Weight")));
                profile.put("week", String.valueOf(rs.getInt("PregnancyWeek")));
                profile.put("prevChild", rs.getString("HasPreviousChild"));
                profile.put("deliveryType", rs.getString("DeliveryType"));
                profile.put("medicalIssue", rs.getString("MedicalIssue"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return profile;
    }
}
