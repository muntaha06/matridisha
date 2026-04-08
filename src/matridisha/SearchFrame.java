package matridisha;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.sql.*;

public class SearchFrame extends JFrame {
    JTextField searchField;
    JTextArea resultArea;
    JButton searchBtn, backBtn;

    public SearchFrame() {
        setTitle("Matridisha - Search Disease");
        setSize(500, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); // Eita dile shudhu ei window bondho hobe
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        mainPanel.setBackground(Color.WHITE);

        Color deepPink = new Color(199, 21, 133);
        Color lightPink = new Color(255, 182, 193);

        // --- Search Bar ---
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchBar.setOpaque(false);
        JLabel label = new JLabel("Symptoms: ");
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(deepPink);

        searchField = new JTextField(15);
        searchField.setBorder(new LineBorder(lightPink, 2));
        searchField.setPreferredSize(new Dimension(150, 30));

        searchBtn = new JButton("Search");
        searchBtn.setBackground(deepPink);
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusable(false);

        searchBar.add(label); searchBar.add(searchField); searchBar.add(searchBtn);

        // --- Result Text Area ---
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Arial", Font.PLAIN, 15));
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        
        JScrollPane scroll = new JScrollPane(resultArea);
        scroll.setBorder(new LineBorder(lightPink, 2));
        scroll.setPreferredSize(new Dimension(400, 300));

        // --- Back Button ---
        backBtn = new JButton("Back to Dashboard");
        backBtn.setBackground(new Color(255, 105, 180)); // Hot Pink
        backBtn.setForeground(Color.WHITE);
        backBtn.setFont(new Font("Arial", Font.BOLD, 14));
        backBtn.setMaximumSize(new Dimension(400, 45));
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.setFocusable(false);

        // Assembly
        mainPanel.add(searchBar);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(scroll);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        mainPanel.add(backBtn);

        add(mainPanel, BorderLayout.CENTER);

        // --- Logic ---
        searchBtn.addActionListener(e -> performSearch());
        backBtn.addActionListener(e -> dispose()); // Current page close korbe
    }

    private void performSearch() {
        String query = searchField.getText().trim();
        if(query.isEmpty()) return;

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM diseases WHERE name LIKE ? OR symptoms LIKE ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, "%" + query + "%");
            pst.setString(2, "%" + query + "%");
            
            ResultSet rs = pst.executeQuery();
            StringBuilder sb = new StringBuilder();
            boolean found = false;
            while (rs.next()) {
                found = true;
                sb.append("🌸 Disease: ").append(rs.getString("name")).append("\n");
                sb.append("📍 Symptoms: ").append(rs.getString("symptoms")).append("\n");
                sb.append("💡 Solution: ").append(rs.getString("solution")).append("\n");
                sb.append("--------------------------------------------------\n\n");
            }
            if (found) resultArea.setText(sb.toString());
            else resultArea.setText("No results found for '" + query + "'.");
        } catch (Exception ex) {
            resultArea.setText("Error: " + ex.getMessage());
        }
    }
}