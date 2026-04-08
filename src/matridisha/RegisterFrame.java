package matridisha;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class RegisterFrame extends JFrame {
    JTextField nameField, userField, dateField;
    JPasswordField passField;
    JButton regBtn, backBtn;

    public RegisterFrame() {
        setTitle("Matridisha - Registration");
        setSize(400, 400);
        setLayout(new GridLayout(6, 2, 10, 10));
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        add(new JLabel("Full Name:")); nameField = new JTextField(); add(nameField);
        add(new JLabel("Username:")); userField = new JTextField(); add(userField);
        add(new JLabel("Password:")); passField = new JPasswordField(); add(passField);
        add(new JLabel("Due Date (YYYY-MM-DD):")); dateField = new JTextField(); add(dateField);

        regBtn = new JButton("Register");
        add(regBtn);
        
        backBtn = new JButton("Back to Login");
        add(backBtn);

        regBtn.addActionListener(e -> registerUser());
        backBtn.addActionListener(e -> { new LoginFrame().setVisible(true); dispose(); });
    }

    private void registerUser() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO users (name, username, password, due_date) VALUES (?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, nameField.getText());
            pst.setString(2, userField.getText());
            pst.setString(3, new String(passField.getPassword()));
            pst.setString(4, dateField.getText());
            
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Registration Successful!");
            new LoginFrame().setVisible(true);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}