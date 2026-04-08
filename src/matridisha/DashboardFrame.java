package matridisha;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class DashboardFrame extends JFrame {
    public DashboardFrame(String name) {
        setTitle("Matridisha - Dashboard");
        setSize(500, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
        mainPanel.setBackground(Color.WHITE);

        // --- Header Section ---
        JLabel title = new JLabel("Matridisha");
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setForeground(new Color(199, 21, 133)); // Magenta/Deep Pink
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel welcome = new JLabel("Welcome " + name + " !");
        welcome.setFont(new Font("Arial", Font.BOLD, 22));
        welcome.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel quote = new JLabel("<html><center>We are by your side on this beautiful journey of motherhood, with trust and compassion.</center></html>");
        quote.setFont(new Font("Arial", Font.PLAIN, 14));
        quote.setMaximumSize(new Dimension(400, 60));
        quote.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- Menu Grid (2x2) ---
        JPanel gridPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        gridPanel.setOpaque(false);
        gridPanel.setMaximumSize(new Dimension(400, 250));

        JButton btnProfile = createGridButton("My Profile");
        JButton btnDisease = createGridButton("<html><center>Common Diseases<br>and Solutions</center></html>");
        JButton btnGuidanceBefore = createGridButton("<html><center>Guidance<br>(before birth)</center></html>");
        JButton btnGuidanceAfter = createGridButton("<html><center>Guidance<br>(after birth)</center></html>");

        gridPanel.add(btnProfile); 
        gridPanel.add(btnDisease);
        gridPanel.add(btnGuidanceBefore); 
        gridPanel.add(btnGuidanceAfter);

        // --- Bottom Buttons ---
        JButton btnLogout = new JButton("Log out");
        btnLogout.setBackground(new Color(199, 21, 133));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFont(new Font("Arial", Font.BOLD, 16));
        btnLogout.setMaximumSize(new Dimension(400, 50));
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogout.setFocusable(false);

        // Adding to Main Panel
        mainPanel.add(title); mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(welcome); mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(quote); mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        mainPanel.add(gridPanel); mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        mainPanel.add(btnLogout);

        add(mainPanel);

        // --- Button Click Actions (Events) ---

        // Open Search Page
        btnDisease.addActionListener(e -> {
            new SearchFrame().setVisible(true);
        });

        // Open Diet/Guidance Page
        btnGuidanceBefore.addActionListener(e -> {
            new DietFrame().setVisible(true);
        });

        // Logout
        btnLogout.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose(); // Close Dashboard
        });

        // Temp Alerts for other buttons
        btnProfile.addActionListener(e -> JOptionPane.showMessageDialog(this, "Profile Section coming soon!"));
        btnGuidanceAfter.addActionListener(e -> JOptionPane.showMessageDialog(this, "After Birth Guidance coming soon!"));
    }

    private JButton createGridButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setForeground(new Color(139, 0, 139)); 
        btn.setBorder(new LineBorder(new Color(255, 182, 193), 2)); 
        btn.setFocusable(false);
        btn.setOpaque(true);
        return btn;
    }
}