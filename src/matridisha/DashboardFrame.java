package matridisha;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.sql.*;

public class DashboardFrame extends JFrame {
    private String username;
    private String fullName = ""; 
    private Color darkPink = new Color(199, 21, 133);
    private Color bgPink = new Color(255, 245, 247); // Light Pink Background

    public DashboardFrame(String username) {
        this.username = username;
        fetchFullName();

        setTitle("Matridisha");
        setSize(550, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        showDashboard();
    }

    private void fetchFullName() {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement pst = conn.prepareStatement("SELECT name FROM users WHERE username = ?");
            pst.setString(1, username);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) fullName = rs.getString("name");
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void showDashboard() {
        getContentPane().removeAll();
        setLayout(new BorderLayout());
        getContentPane().setBackground(bgPink);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(40, 50, 40, 50));
        mainPanel.setBackground(bgPink);

        // Header Title
        JLabel title = new JLabel("Matridisha", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 42));
        title.setForeground(darkPink);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Welcome Message
        JLabel welcome = new JLabel("Welcome, " + (fullName.isEmpty() ? username : fullName) + "!", JLabel.CENTER);
        welcome.setFont(new Font("Arial", Font.BOLD, 22));
        welcome.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Pregnancy Heading (HTML used for perfect centering and line break)
        JLabel pregnancyHeading = new JLabel("<html><div style='text-align: center; width: 350px;'>" +
                "Embrace the Joy of Motherhood:<br>Your Healthy Pregnancy Guide</div></html>", JLabel.CENTER);
        pregnancyHeading.setFont(new Font("Serif", Font.ITALIC, 19));
        pregnancyHeading.setForeground(new Color(150, 50, 100));
        pregnancyHeading.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Grid Menu
        JPanel grid = new JPanel(new GridLayout(2, 2, 20, 20));
        grid.setOpaque(false);
        grid.setMaximumSize(new Dimension(450, 280));

        JButton b1 = createMenuBtn("My Profile");
        JButton b2 = createMenuBtn("Common Diseases");
        JButton b3 = createMenuBtn("Before Birth Guide");
        JButton b4 = createMenuBtn("After Birth Care");

        grid.add(b1); grid.add(b2); grid.add(b3); grid.add(b4);

        // Logout Button
        JButton logout = new JButton("Logout");
        logout.setBackground(darkPink);
        logout.setForeground(Color.WHITE);
        logout.setFont(new Font("Arial", Font.BOLD, 15));
        logout.setMaximumSize(new Dimension(450, 50));
        logout.setAlignmentX(Component.CENTER_ALIGNMENT);
        logout.setFocusable(false);

        // Listeners for Redirection
        b1.addActionListener(e -> setContentPanel(new ProfilePanel(this, username)));
        b2.addActionListener(e -> setContentPanel(new SearchPanel(this)));
        b3.addActionListener(e -> setContentPanel(new GuidancePanel(this, true)));
        b4.addActionListener(e -> setContentPanel(new GuidancePanel(this, false)));
        logout.addActionListener(e -> { new LoginFrame().setVisible(true); dispose(); });

        mainPanel.add(title);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(welcome);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(pregnancyHeading);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 45)));
        mainPanel.add(grid);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 45)));
        mainPanel.add(logout);

        add(mainPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void setContentPanel(JPanel panel) {
        getContentPane().removeAll();
        add(panel);
        revalidate();
        repaint();
    }

    private JButton createMenuBtn(String t) {
        JButton b = new JButton(t);
        b.setBackground(Color.WHITE);
        b.setFont(new Font("Arial", Font.BOLD, 14));
        b.setForeground(new Color(139, 0, 139));
        b.setBorder(new LineBorder(new Color(255, 182, 193), 2));
        b.setFocusable(false);
        return b;
    }
}