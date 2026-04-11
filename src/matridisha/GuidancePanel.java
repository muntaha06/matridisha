package matridisha;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.*;

public class GuidancePanel extends JPanel {
    DashboardFrame parent;
    JPanel contentPanel;
    Color darkPink = new Color(199, 21, 133);
    Color lightPink = new Color(255, 182, 193);

    public GuidancePanel(DashboardFrame parent, boolean isBeforeBirth) {
        this.parent = parent;
        setLayout(new BorderLayout());
        setBackground(new Color(255, 245, 247));

        String category = isBeforeBirth ? "Prenatal" : "Postnatal";
        
        // --- Header Section ---
        JLabel header = new JLabel(isBeforeBirth ? "BEFORE BIRTH (PRENATAL) GUIDE" : "AFTER BIRTH (POSTNATAL) GUIDE", JLabel.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 26)); // Increased header size
        header.setForeground(darkPink);
        header.setBorder(new EmptyBorder(25, 0, 25, 0));

        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(255, 245, 247));
        contentPanel.setBorder(new EmptyBorder(10, 35, 10, 35));

        loadData(category);

        JScrollPane scroll = new JScrollPane(contentPanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(25); // Faster scrolling

        JButton back = new JButton("← Back to Dashboard");
        back.setBackground(darkPink);
        back.setForeground(Color.WHITE);
        back.setFont(new Font("Arial", Font.BOLD, 16));
        back.setPreferredSize(new Dimension(0, 60));
        back.setFocusable(false);
        back.addActionListener(e -> parent.showDashboard());

        add(header, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(back, BorderLayout.SOUTH);
    }

    private void loadData(String category) {
        contentPanel.removeAll();
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement pst = conn.prepareStatement("SELECT title, info FROM guidance WHERE category = ?");
            pst.setString(1, category);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                contentPanel.add(createCard(rs.getString("title"), rs.getString("info")));
                contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    /**
     * Enhanced createCard with larger fonts and better spacing
     */
    private JPanel createCard(String title, String info) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(lightPink, 2),
            new EmptyBorder(10, 10, 10, 10)
        ));

        JTextPane textPane = new JTextPane();
        textPane.setContentType("text/html");
        textPane.setEditable(false);
        textPane.setFocusable(false);
        textPane.setMargin(new Insets(15, 15, 15, 15)); // Inner padding for text
        
        // --- HTML Formatting with Increased Font Size ---
        // Base font set to 13pt, Headings set to 18pt
        String htmlContent = "<html><body style='font-family: Segoe UI, Arial; font-size: 13pt; line-height: 1.6; color: #333333;'>" +
            "<h1 style='color: #C71585; font-size: 18pt; margin-bottom: 15px;'>" + title + "</h1>" +
            info.replaceAll("DIET \\(What, Why, When, How Much\\):", "<b>DIET (What, Why, When, How Much):</b>")
                .replaceAll("EXERCISE:", "<b>EXERCISE:</b>")
                .replaceAll("RECOVERY & TIPS:", "<b>RECOVERY & TIPS:</b>")
                .replaceAll("WHAT TO FEED:", "<b>WHAT TO FEED:</b>")
                .replaceAll("CARE:", "<b>CARE:</b>")
                .replaceAll("MENTAL HEALTH:", "<b>MENTAL HEALTH:</b>")
                .replaceAll("WHAT:", "<b>WHAT:</b>")
                .replaceAll("WHY:", "<b>WHY:</b>")
                .replaceAll("WHEN:", "<b>WHEN:</b>")
                .replaceAll("HOW MUCH:", "<b>HOW MUCH:</b>")
                .replaceAll("\n", "<br>") + 
            "</body></html>";

        textPane.setText(htmlContent);
        card.add(textPane, BorderLayout.CENTER);
        
        // Dynamic height handling for 500+ words
        card.setMaximumSize(new Dimension(850, 2000)); 
        return card;
    }
}