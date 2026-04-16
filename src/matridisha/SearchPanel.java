package matridisha;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.*;

public class SearchPanel extends JPanel {
    JTextField searchField;
    JPanel resultPanel;
    DashboardFrame parent;
    Color darkPink = new Color(199, 21, 133);
    Color lightPink = new Color(255, 182, 193);

    // ===============================
    // 1️⃣ Constructor & Layout Setup
    // ===============================
    public SearchPanel(DashboardFrame parent) {
        this.parent = parent;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // --- Top Search Bar ---
        JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        top.setBackground(Color.WHITE);
        
        searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setBorder(new LineBorder(lightPink, 2));
        searchField.setPreferredSize(new Dimension(250, 40));

        JButton searchBtn = new JButton("Search");
        styleButton(searchBtn, darkPink, Color.WHITE);
        searchBtn.setPreferredSize(new Dimension(100, 40));

        top.add(new JLabel("Search Disease:"));
        top.add(searchField);
        top.add(searchBtn);

        // --- Result Area with Smooth Scroll ---
        resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.setBackground(new Color(255, 245, 247));
        
        JScrollPane scroll = new JScrollPane(resultPanel);
        scroll.setBorder(new EmptyBorder(10, 20, 10, 20));
        // Crucial for long text: make the scrollbar move faster
        scroll.getVerticalScrollBar().setUnitIncrement(20);

        // --- Bottom Navigation ---
        JButton backBtn = new JButton("← Back to Dashboard");
        styleButton(backBtn, darkPink, Color.WHITE);
        backBtn.setPreferredSize(new Dimension(0, 55));

        add(top, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(backBtn, BorderLayout.SOUTH);

        // Load all data on start
        loadData("");

        // Listeners
        searchBtn.addActionListener(e -> loadData(searchField.getText().trim()));
        backBtn.addActionListener(e -> parent.showDashboard());
    }

    // ===============================
    // 2️⃣ Database Logic
    // ===============================
    private void loadData(String query) {
        resultPanel.removeAll();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM diseases WHERE name LIKE ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, "%" + query + "%");
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                resultPanel.add(createCard(
                    rs.getString("name"), 
                    rs.getString("symptoms"), 
                    rs.getString("cause"), 
                    rs.getString("solution")
                ));
                resultPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        resultPanel.revalidate();
        resultPanel.repaint();
    }

    // ===============================
    // 3️⃣ UI Component: Enhanced Card
    // ===============================
    private JPanel createCard(String name, String sym, String cause, String sol) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(lightPink, 2), 
            new EmptyBorder(20, 20, 20, 20)
        ));
        // Increased height to accommodate the 500-word content
        card.setMaximumSize(new Dimension(700, 450));

        JLabel title = new JLabel(name.toUpperCase());
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(darkPink);
        title.setBorder(new EmptyBorder(0, 0, 10, 0));

        // Use a styled JTextPane or TextArea for rich text
        JTextArea infoArea = new JTextArea();
        infoArea.setText(
            "SYMPTOMS:\n" + sym + "\n\n" +
            "CAUSES:\n" + cause + "\n\n" +
            "DIETARY SOLUTION (What, Why, When, How Much):\n" + sol
        );
        infoArea.setFont(new Font("Arial", Font.PLAIN, 14));
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);
        infoArea.setEditable(false);
        infoArea.setMargin(new Insets(10, 10, 10, 10));

        // Putting the text inside its own scroll pane if it's too long
        JScrollPane internalScroll = new JScrollPane(infoArea);
        internalScroll.setBorder(null);

        card.add(title, BorderLayout.NORTH);
        card.add(internalScroll, BorderLayout.CENTER);
        
        return card;
    }

    private void styleButton(JButton b, Color bg, Color fg) {
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFont(new Font("Arial", Font.BOLD, 14));
        b.setFocusable(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}