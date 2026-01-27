package frontend;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // Collecting data from the HTML form
            String name = request.getParameter("name");
            int age = Integer.parseInt(request.getParameter("age"));
            double weight = Double.parseDouble(request.getParameter("weight"));
            int weeks = Integer.parseInt(request.getParameter("weeks"));
            String prevChild = request.getParameter("previousChild");
            String deliveryType = request.getParameter("deliveryType");
            String username = request.getParameter("username");
            String password = request.getParameter("password");

            // Calling the Register logic class
            Register reg = new Register();
            boolean success = reg.registerUser(name, age, weight, weeks, prevChild, deliveryType, username, password);

            if(success) {
                response.setStatus(200);
                response.getWriter().write("Success");
            } else {
                response.setStatus(500);
                response.getWriter().write("Registration Failed");
            }
        } catch (Exception e) {
            response.setStatus(400);
            response.getWriter().write("Error: " + e.getMessage());
        }
    }
}