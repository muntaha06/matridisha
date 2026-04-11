package matridisha;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.*;

public class RegisterFrame extends JFrame {
    JTextField nameF, userF, ageF, weightF, weeksF;
    JPasswordField passF;
    JComboBox<String> childCB, deliveryCB, diseaseCB;
    Color darkPink = new Color(199, 21, 133);
    Color lightPink = new Color(255, 182, 193);

    // ===============================
    // 1️⃣ Constructor & UI Layout
    // ===============================
    public RegisterFrame() {
        setTitle("Matridisha - Registration");
        setSize(500, 750);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);

        // Main Panel Setup
        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBorder(new EmptyBorder(20, 40, 20, 40));
        main.setBackground(Color.WHITE);

        // Header Title
        JLabel title = new JLabel("Create Account");
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(darkPink);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- Input Fields Initialization ---
        nameF = createF("Full Name");
        userF = createF("Username");
        passF = new JPasswordField(); setupField(passF, "Password");
        ageF = createF("Age");
        weightF = createF("Weight (kg)");
        weeksF = createF("Current Pregnancy Weeks");

        // Combo Boxes Setup
        String[] diseases = {"None", "Diabetes", "Asthma (Hapani)", "Anemia", "Heart Problem", "High Pressure"};
        diseaseCB = new JComboBox<>(diseases); setupCB(diseaseCB, "Current Disease");

        childCB = new JComboBox<>(new String[]{"No", "Yes"}); setupCB(childCB, "Previous Child");
        deliveryCB = new JComboBox<>(new String[]{"Not Applicable", "Normal", "C-Section"}); setupCB(deliveryCB, "Delivery Preference/Type");

        // Register Button
        JButton regBtn = new JButton("Register Now");
        regBtn.setBackground(darkPink); 
        regBtn.setForeground(Color.WHITE);
        regBtn.setFont(new Font("Arial", Font.BOLD, 16));
        regBtn.setMaximumSize(new Dimension(400, 50));
        regBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        regBtn.setFocusable(false);

        // --- Assembly ---
        main.add(title); 
        main.add(Box.createRigidArea(new Dimension(0, 20)));
        main.add(nameF); main.add(userF); main.add(passF);
        main.add(ageF); main.add(weightF); main.add(weeksF);
        main.add(diseaseCB); main.add(childCB); main.add(deliveryCB);
        main.add(Box.createRigidArea(new Dimension(0, 20))); 
        main.add(regBtn);

        add(new JScrollPane(main));

        // Event Listeners
        regBtn.addActionListener(e -> registerUser());
    }

    // ===============================
    // 2️⃣ UI Helper Methods
    // ===============================
    private JTextField createF(String t) {
        JTextField f = new JTextField(); 
        setupField(f, t); 
        return f;
    }

    private void setupField(JComponent c, String t) {
        c.setBorder(BorderFactory.createTitledBorder(new LineBorder(lightPink, 2), t));
        c.setMaximumSize(new Dimension(400, 50));
    }

    private void setupCB(JComboBox c, String t) {
        c.setBackground(Color.WHITE);
        c.setBorder(BorderFactory.createTitledBorder(new LineBorder(lightPink, 2), t));
        c.setMaximumSize(new Dimension(400, 55));
    }

    // ===============================
    // 3️⃣ Database: Register Logic
    // ===============================
    private void registerUser() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO users (name, username, password, age, weight, current_weeks, prev_child, delivery_type, current_disease) VALUES (?,?,?,?,?,?,?,?,?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, nameF.getText());
            pst.setString(2, userF.getText());
            pst.setString(3, new String(passF.getPassword()));
            pst.setInt(4, Integer.parseInt(ageF.getText()));
            pst.setDouble(5, Double.parseDouble(weightF.getText()));
            pst.setInt(6, Integer.parseInt(weeksF.getText()));
            pst.setString(7, childCB.getSelectedItem().toString());
            pst.setString(8, deliveryCB.getSelectedItem().toString());
            pst.setString(9, diseaseCB.getSelectedItem().toString());
            
            pst.executeUpdate();
            
            showCustomMsg("Registration Successful!");
            dispose();
        } catch (Exception ex) { 
            showCustomMsg("Error: Please fill all information correctly!"); 
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
            JOptionPane.INFORMATION_MESSAGE);
    }
}