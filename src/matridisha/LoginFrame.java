package matridisha;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.sql.*;

public class LoginFrame extends JFrame {
    private JTextField userField;
    private JPasswordField passField;
    private Color darkPink = new Color(199, 21, 133);
    private Color lightPink = new Color(255, 182, 193);

    // ===============================
    // 1️⃣ Constructor & UI Layout
    // ===============================
    public LoginFrame() {
        setTitle("Matridisha - Login");
        setSize(450, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);

        // Main Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(40, 50, 40, 50));
        mainPanel.setBackground(Color.WHITE);

        // Header Section
        JLabel title = new JLabel("Matridisha");
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(darkPink);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Login to your Matridisha account");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitle.setForeground(Color.GRAY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- Input Fields Initialization ---
        userField = new JTextField();
        setupField(userField, "Username");

        passField = new JPasswordField();
        setupField(passField, "Password");

        // --- Buttons Initialization ---
        JButton loginBtn = new JButton("Login");
        styleButton(loginBtn, darkPink, Color.WHITE);

        JButton regBtn = new JButton("Don't have an account? Register");
        regBtn.setContentAreaFilled(false);
        regBtn.setBorderPainted(false);
        regBtn.setForeground(darkPink);
        regBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        regBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Assembly of Components
        mainPanel.add(title);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(subtitle);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 40)));
        mainPanel.add(userField);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(passField);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        mainPanel.add(loginBtn);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(regBtn);

        add(mainPanel);

        // --- Event Listeners ---
        loginBtn.addActionListener(e -> handleLogin());
        regBtn.addActionListener(e -> {
            new RegisterFrame().setVisible(true);
            // Optional: dispose(); // Close login if you want
        });
    }

    // ===============================
    // 2️⃣ UI Helper Methods
    // ===============================
    private void setupField(JComponent c, String title) {
        c.setBorder(BorderFactory.createTitledBorder(new LineBorder(lightPink, 2), title));
        c.setMaximumSize(new Dimension(400, 55));
        c.setFont(new Font("Arial", Font.PLAIN, 14));
    }

    private void styleButton(JButton b, Color bg, Color fg) {
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFont(new Font("Arial", Font.BOLD, 16));
        b.setMaximumSize(new Dimension(400, 50));
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setFocusable(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // ===============================
    // 3️⃣ Database: Login Logic
    // ===============================
    private void handleLogin() {
        String user = userField.getText().trim();
        String pass = new String(passField.getPassword()).trim();

        if (user.isEmpty() || pass.isEmpty()) {
            showCustomMsg("Please enter both username and password!");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, user);
            pst.setString(2, pass);

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                showCustomMsg("Login Successful!");
                // Open Dashboard and pass current user
                new DashboardFrame(user).setVisible(true);
                dispose();
            } else {
                showCustomMsg("Invalid Username or Password!");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showCustomMsg("Database Error!");
        }
    }

    // ===============================
    // 4️⃣ Custom Message Dialog
    // ===============================
    private void showCustomMsg(String m) {
        UIManager.put("OptionPane.background", Color.WHITE);
        UIManager.put("Panel.background", Color.WHITE);
        JOptionPane.showMessageDialog(this, 
            "<html><font color='#C71585'><b>" + m + "</b></font></html>", 
            "Matridisha", 
            JOptionPane.PLAIN_MESSAGE);
    }

    // ===============================
    // 5️⃣ Main Method
    // ===============================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}