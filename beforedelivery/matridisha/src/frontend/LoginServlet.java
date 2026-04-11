import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Basic validation
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            sendErrorResponse(response, "Email and password are required!");
            return;
        }

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/matridisha", "root", "")) {
            
            String sql = "SELECT id, name, password_hash FROM users WHERE email = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, email);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next() && verifyPassword(password, rs.getString("password_hash"))) {
                        HttpSession session = request.getSession();
                        session.setAttribute("userName", rs.getString("name"));
                        session.setAttribute("userId", rs.getInt("id"));
                        response.sendRedirect("dashboard.html");
                    } else {
                        sendErrorResponse(response, "Invalid email or password!");
                    }
                }
            }
        } catch (SQLException e) {
            sendErrorResponse(response, "Database error occurred. Please try again later.");
            e.printStackTrace();
        }
    }

    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<h3 style='color:red'>" + message + "</h3>");
        out.println("<a href='login.html'>Try Again</a>");
    }

    private boolean verifyPassword(String rawPassword, String hashedPassword) {
        // Use BCrypt: BCrypt.checkpw(rawPassword, BCrypt.gensalt(hashBytes))
        // For now, this is a placeholder
        return rawPassword.equals(hashedPassword); // Remove this in production!
    }
}