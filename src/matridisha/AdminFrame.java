package matridisha;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.sql.*;

public class AdminFrame extends JFrame {
    JTextField nameField, causeField, imagePathField;
    JTextArea symArea, solArea;

    // ===============================
    // 1️⃣ Constructor & UI Layout
    // ===============================
    public AdminFrame() {
        setTitle("Matridisha - Admin Control Panel");
        setSize(550, 700);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);

        Color deepPink = new Color(199, 21, 133);
        Color lightPink = new Color(255, 182, 193);
        Font boldFont = new Font("Arial", Font.BOLD, 14);

        // --- Main Panel Setup ---
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        mainPanel.setBackground(Color.WHITE);

        // Header
        JLabel header = new JLabel("Disease Database Management");
        header.setFont(new Font("Arial", Font.BOLD, 22));
        header.setForeground(deepPink);
        header.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- Input Fields Initialization ---
        nameField = createStyledField("Disease Name", lightPink);
        causeField = createStyledField("Cause (Why it happens)", lightPink);
        imagePathField = createStyledField("Image URL / Path", lightPink);
        
        symArea = new JTextArea(4, 20);
        solArea = new JTextArea(4, 20);
        setupTextArea(symArea, "Symptoms", lightPink);
        setupTextArea(solArea, "Solution / Remedy", lightPink);

        // --- Buttons Setup ---
        JButton addBtn = createPinkButton("Add Disease Record", deepPink);
        JButton deleteBtn = new JButton("Delete Disease by Name");
        deleteBtn.setBackground(Color.BLACK);
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setFont(boldFont);
        deleteBtn.setMaximumSize(new Dimension(450, 45));
        deleteBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        deleteBtn.setFocusable(false);

        JButton backBtn = new JButton("Back to Dashboard");
        backBtn.setForeground(deepPink);
        backBtn.setContentAreaFilled(false);
        backBtn.setBorderPainted(false);
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- Assembly of Components ---
        mainPanel.add(header);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        mainPanel.add(nameField);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(causeField);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(new JScrollPane(symArea));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(new JScrollPane(solArea));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(imagePathField);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        mainPanel.add(addBtn);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(deleteBtn);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(backBtn);

        add(new JScrollPane(mainPanel));

        // --- Action Listeners ---
        addBtn.addActionListener(e -> addData());
        deleteBtn.addActionListener(e -> deleteData());
        backBtn.addActionListener(e -> dispose());
    }

    // ===============================
    // 2️⃣ UI Helper Methods
    // ===============================
    private JTextField createStyledField(String title, Color borderCol) {
        JTextField f = new JTextField();
        f.setBorder(BorderFactory.createTitledBorder(new LineBorder(borderCol, 1), title));
        f.setMaximumSize(new Dimension(450, 50));
        return f;
    }

    private void setupTextArea(JTextArea area, String title, Color borderCol) {
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createTitledBorder(new LineBorder(borderCol, 1), title));
    }

    private JButton createPinkButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Arial", Font.BOLD, 15));
        b.setMaximumSize(new Dimension(450, 50));
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setFocusable(false);
        return b;
    }

    // ===============================
    // 3️⃣ Database: Add Data Method
    // ===============================
    private void addData() {
        String name = nameField.getText().trim();
        String symptoms = symArea.getText().trim();
        String cause = causeField.getText().trim();
        String solution = solArea.getText().trim();
        String img = imagePathField.getText().trim();

        if (name.isEmpty() || symptoms.isEmpty() || solution.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill at least Name, Symptoms, and Solution!");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO diseases (name, symptoms, cause, solution, image_path) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, name);
            pst.setString(2, symptoms);
            pst.setString(3, cause);
            pst.setString(4, solution);
            pst.setString(5, img);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Disease Added Successfully!");
            
            // Clear fields after success
            nameField.setText(""); symArea.setText(""); causeField.setText(""); 
            solArea.setText(""); imagePathField.setText("");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }

    // ===============================
    // 4️⃣ Database: Delete Data Method
    // ===============================
    private void deleteData() {
        String name = JOptionPane.showInputDialog(this, "Enter the exact Disease Name to delete:");
        if (name != null && !name.trim().isEmpty()) {
            try (Connection conn = DBConnection.getConnection()) {
                String sql = "DELETE FROM diseases WHERE name = ?";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, name.trim());
                int res = pst.executeUpdate();
                
                if (res > 0) {
                    JOptionPane.showMessageDialog(this, "Deleted Successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "No disease found with that name.");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }
}