package matridisha;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.sql.*;

public class LoginFrame extends JFrame {
    JTextField userField;
    JPasswordField passField;
    JButton loginBtn, signupBtn, forgotBtn;

    public LoginFrame() {
        setTitle("Matridisha - Login");
        setSize(450, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(255, 240, 245)); // Soft Pink Background

        // Center Panel (Card-like structure without advanced code)
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(40, 40, 40, 40));
        card.setBackground(Color.WHITE);

        // Title
        JLabel title = new JLabel("Matridisha");
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setForeground(new Color(139, 0, 139)); // Magenta/Dark Pink
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subTitle = new JLabel("Your Pregnancy Companion");
        subTitle.setFont(new Font("Arial", Font.PLAIN, 18));
        subTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Inputs Panel
        JPanel inputPanel = new JPanel(new GridLayout(4, 1, 5, 15));
        inputPanel.setOpaque(false);
        inputPanel.setMaximumSize(new Dimension(350, 150));

        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        userLabel.setForeground(new Color(139, 0, 139));
        userField = new JTextField();
        userField.setBorder(new LineBorder(new Color(255, 182, 193), 2)); // Pink Border

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passLabel.setForeground(new Color(139, 0, 139));
        passField = new JPasswordField();
        passField.setBorder(new LineBorder(new Color(255, 182, 193), 2)); // Pink Border

        inputPanel.add(userLabel); inputPanel.add(userField);
        inputPanel.add(passLabel); inputPanel.add(passField);

        // Buttons
        loginBtn = new JButton("Login");
        loginBtn.setBackground(new Color(199, 21, 133)); // Deep Pink
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Arial", Font.BOLD, 16));
        loginBtn.setMaximumSize(new Dimension(350, 50));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.setFocusable(false);

        signupBtn = new JButton("<html>No account? <u>Register now</u></html>");
        signupBtn.setBorderPainted(false);
        signupBtn.setContentAreaFilled(false);
        signupBtn.setFocusable(false);
        signupBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        forgotBtn = new JButton("<html><u>Forgot password? Reset</u></html>");
        forgotBtn.setBorderPainted(false);
        forgotBtn.setContentAreaFilled(false);
        forgotBtn.setFocusable(false);
        forgotBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Assembly
        card.add(title); card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(subTitle); card.add(Box.createRigidArea(new Dimension(0, 40)));
        card.add(inputPanel); card.add(Box.createRigidArea(new Dimension(0, 40)));
        card.add(loginBtn); card.add(Box.createRigidArea(new Dimension(0, 20)));
        card.add(signupBtn); card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(forgotBtn);

        add(card, BorderLayout.CENTER);

        // Actions
        loginBtn.addActionListener(e -> loginAction());
        signupBtn.addActionListener(e -> { new RegisterFrame().setVisible(true); dispose(); });
    }

    private void loginAction() {
        String user = userField.getText();
        String pass = new String(passField.getPassword());
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM users WHERE username=? AND password=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, user);
            pst.setString(2, pass);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                new DashboardFrame(name).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Username or Password!");
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    public static void main(String[] args) {
        new LoginFrame().setVisible(true);
    }
}