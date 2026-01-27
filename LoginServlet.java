package frontend;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        Login log = new Login();
        String[] userData = log.loginUser(username, password);

        if (userData != null) {
            response.setStatus(200);
            // Sending Name and Weeks separated by comma
            response.getWriter().write(userData[0] + "," + userData[1]);
        } else {
            response.setStatus(401);
            response.getWriter().write("Invalid Credentials");
        }
    }
}