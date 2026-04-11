package matridisha;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.*;

/**
 * SearchPanel: Displays detailed disease information with search functionality.
 * Designed to handle 500+ word descriptions with high readability.
 */
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

        // --- Top Search Bar Section ---
        JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        top.setBackground(Color.WHITE);
        
        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        searchField.setBorder(new LineBorder(lightPink, 2));
        searchField.setPreferredSize(new Dimension(280, 45));

        JButton searchBtn = new JButton("Search");
        styleButton(searchBtn, darkPink, Color.WHITE);
        searchBtn.setPreferredSize(new Dimension(120, 45));

        JLabel searchLabel = new JLabel("Search Disease:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        top.add(searchLabel);
        top.add(searchField);
        top.add(searchBtn);

        // --- Result Display Area with Fast Scrolling ---
        resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.setBackground(new Color(255, 245, 247));
        
        JScrollPane scroll = new JScrollPane(resultPanel);
        scroll.setBorder(new EmptyBorder(10, 30, 10, 30));
        scroll.getVerticalScrollBar().setUnitIncrement(25); // Faster scrolling for long text content

        // --- Bottom Navigation ---
        JButton backBtn = new JButton("← Back to Dashboard");
        styleButton(backBtn, darkPink, Color.WHITE);
        backBtn.setPreferredSize(new Dimension(0, 60));

        add(top, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(backBtn, BorderLayout.SOUTH);

        // Initial load of all diseases
        loadData("");

        // Listeners
        searchBtn.addActionListener(e -> loadData(searchField.getText().trim()));
        backBtn.addActionListener(e -> parent.showDashboard());
    }

    /**
     * Fetches data from MySQL and populates the result panel
     */
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
                resultPanel.add(Box.createRigidArea(new Dimension(0, 30))); // Gap between cards
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        resultPanel.revalidate();
        resultPanel.repaint();
    }

    /**
     * Creates a card using HTML to style long text with bold sub-headers
     */
    private JPanel createCard(String name, String sym, String cause, String sol) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(lightPink, 2), 
            new EmptyBorder(10, 10, 10, 10)
        ));

        // JTextPane allows for 13pt font and Bold styling via HTML
        JTextPane infoPane = new JTextPane();
        infoPane.setContentType("text/html");
        infoPane.setEditable(false);
        infoPane.setFocusable(false);
        infoPane.setMargin(new Insets(15, 15, 15, 15));

        // --- HTML String for Disease Details ---
        String htmlContent = "<html><body style='font-family: Segoe UI, Arial; font-size: 13pt; line-height: 1.6; color: #333333;'>" +
            "<h2 style='color: #C71585; font-size: 19pt; margin-bottom: 12px;'>" + name.toUpperCase() + "</h2>" +
            "<p><b>SYMPTOMS:</b><br>" + sym.replaceAll("\n", "<br>") + "</p>" +
            "<p><b>CAUSES:</b><br>" + cause.replaceAll("\n", "<br>") + "</p>" +
            "<p><b>DIETARY SOLUTION (What, Why, When, How Much):</b><br>" + sol.replaceAll("\n", "<br>") + "</p>" +
            "</body></html>";

        infoPane.setText(htmlContent);
        card.add(infoPane, BorderLayout.CENTER);
        
        // Ensure card expands to fit 500+ words
        card.setMaximumSize(new Dimension(850, 2000)); 
        
        return card;
    }

    /**
     * Common styling for buttons
     */
    private void styleButton(JButton b, Color bg, Color fg) {
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFont(new Font("Segoe UI", Font.BOLD, 16));
        b.setFocusable(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder());
    }
}