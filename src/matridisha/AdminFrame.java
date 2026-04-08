package matridisha;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.sql.*;

public class AdminFrame extends JFrame {
    JTextField nameField;
    JTextArea symArea, solArea;

    public AdminFrame() {
        setTitle("Matridisha - Admin Panel");
        setSize(450, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);

        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBorder(new EmptyBorder(30, 30, 30, 30));
        main.setBackground(Color.WHITE);

        Color deepPink = new Color(199, 21, 133);
        LineBorder pinkBorder = new LineBorder(new Color(255, 182, 193), 2);

        // Form elements
        JLabel title = new JLabel("Add New Information");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(deepPink);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        nameField = new JTextField(); nameField.setBorder(pinkBorder);
        symArea = new JTextArea(3, 10); symArea.setBorder(pinkBorder);
        solArea = new JTextArea(5, 10); solArea.setBorder(pinkBorder);

        JButton addBtn = new JButton("Save to Database");
        addBtn.setBackground(deepPink);
        addBtn.setForeground(Color.WHITE);
        addBtn.setMaximumSize(new Dimension(400, 45));
        addBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton backBtn = new JButton("Back to Dashboard");
        backBtn.setContentAreaFilled(false);
        backBtn.setForeground(deepPink);
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Adding components
        main.add(title); main.add(Box.createRigidArea(new Dimension(0, 20)));
        main.add(new JLabel("Disease Name:")); main.add(nameField);
        main.add(Box.createRigidArea(new Dimension(0, 15)));
        main.add(new JLabel("Symptoms:")); main.add(new JScrollPane(symArea));
        main.add(Box.createRigidArea(new Dimension(0, 15)));
        main.add(new JLabel("Solution:")); main.add(new JScrollPane(solArea));
        main.add(Box.createRigidArea(new Dimension(0, 25)));
        main.add(addBtn); main.add(Box.createRigidArea(new Dimension(0, 10)));
        main.add(backBtn);

        add(main);

        // Actions
        addBtn.addActionListener(e -> addData());
        backBtn.addActionListener(e -> dispose());
    }

    private void addData() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO diseases (name, symptoms, solution) VALUES (?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, nameField.getText());
            pst.setString(2, symArea.getText());
            pst.setString(3, solArea.getText());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data Saved Successfully!");
            nameField.setText(""); symArea.setText(""); solArea.setText("");
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
    }
}