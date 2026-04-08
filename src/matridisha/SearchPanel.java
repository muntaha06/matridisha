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

    public SearchPanel(DashboardFrame parent) {
        this.parent = parent;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Top Search Bar
        JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        top.setBackground(Color.WHITE);
        
        searchField = new JTextField(15);
        searchField.setBorder(new LineBorder(lightPink, 2));
        searchField.setPreferredSize(new Dimension(200, 35));

        JButton searchBtn = new JButton("Search");
        searchBtn.setBackground(darkPink);
        searchBtn.setForeground(Color.WHITE);

        top.add(new JLabel("Search Disease:"));
        top.add(searchField);
        top.add(searchBtn);

        // Result Area
        resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.setBackground(new Color(255, 245, 247));
        
        JScrollPane scroll = new JScrollPane(resultPanel);
        scroll.setBorder(new EmptyBorder(10, 20, 10, 20));

        // Bottom Navigation
        JButton backBtn = new JButton("← Back to Dashboard");
        backBtn.setBackground(darkPink);
        backBtn.setForeground(Color.WHITE);
        backBtn.setPreferredSize(new Dimension(0, 50));

        add(top, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(backBtn, BorderLayout.SOUTH);

        // Initial Load
        loadData("");

        searchBtn.addActionListener(e -> loadData(searchField.getText().trim()));
        backBtn.addActionListener(e -> parent.showDashboard());
    }

    private void loadData(String query) {
        resultPanel.removeAll();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM diseases WHERE name LIKE ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, "%" + query + "%");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                resultPanel.add(createCard(rs.getString("name"), rs.getString("symptoms"), rs.getString("cause"), rs.getString("solution")));
                resultPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            }
        } catch (Exception e) { e.printStackTrace(); }
        resultPanel.revalidate();
        resultPanel.repaint();
    }

    private JPanel createCard(String name, String sym, String cause, String sol) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(new LineBorder(lightPink, 1), new EmptyBorder(15, 15, 15, 15)));
        card.setMaximumSize(new Dimension(500, 250));

        JLabel title = new JLabel(name.toUpperCase());
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(darkPink);

        JTextArea info = new JTextArea("Symptoms: " + sym + "\nCause: " + cause + "\nSolution: " + sol);
        info.setLineWrap(true); info.setWrapStyleWord(true); info.setEditable(false);

        card.add(title); card.add(Box.createRigidArea(new Dimension(0, 10))); card.add(info);
        return card;
    }
}