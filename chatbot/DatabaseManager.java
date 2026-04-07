import java.sql.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import org.json.JSONArray;
import org.json.JSONObject;

public class DatabaseManager {
    // Connection String for Local SQL Server
    // Ensure "Anika Database" exists in your SQL Server
    private static final String CONNECTION_URL = "jdbc:sqlserver://DESKTOP-UI6PRJS\\SQLEXPRESS;databaseName=Anika Database;integratedSecurity=true;encrypt=true;trustServerCertificate=true;";

    public static void insertMessage(String sessionId, String userName, String question, String reply, String time,
            String image) {
        String sql = "INSERT INTO ConversationHistory (SessionID, UserName, UserQuestion, BotReply, Timestamp, ImageBase64) VALUES (?, ?, ?, ?, ?, ?)";

        // Load driver explicitly just in case (Assuming mssql-jdbc jar is in classpath)
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (Exception e) {
        }

        try (Connection conn = DriverManager.getConnection(CONNECTION_URL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, sessionId);
            pstmt.setString(2, userName);
            pstmt.setString(3, question);
            pstmt.setString(4, reply);
            pstmt.setString(5, time);
            pstmt.setString(6, image); // Can be null

            pstmt.executeUpdate();
            System.out.println("DB: Message saved for session " + sessionId);

        } catch (SQLException e) {
            System.err.println("DB Error: " + e.getMessage());
            // Don't crash the app if DB fails, just log it
        }
    }

    // Method to upload existing history.json
    public static void main(String[] args) {
        try {
            System.out.println("Starting migration of history.json to Database...");
            File f = new File("history.json");
            if (!f.exists()) {
                System.out.println("history.json not found.");
                return;
            }
            String content = new String(Files.readAllBytes(f.toPath()), StandardCharsets.UTF_8);
            JSONArray history = new JSONArray(content);

            int count = 0;
            for (int i = 0; i < history.length(); i++) {
                JSONObject session = history.getJSONObject(i);
                String sId = session.getString("id");
                String uName = session.optString("user", "Anika"); // Default to Anika if missing
                JSONArray msgs = session.getJSONArray("messages");

                for (int j = 0; j < msgs.length(); j++) {
                    JSONObject m = msgs.getJSONObject(j);
                    String q = m.optString("user", "");
                    String a = m.optString("bot", "");
                    String t = m.optString("time", "");
                    String img = m.optString("image", null);

                    insertMessage(sId, uName, q, a, t, img);
                    count++;
                }
            }
            System.out.println("Successfully uploaded " + count + " messages to the database.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
