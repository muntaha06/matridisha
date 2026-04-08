package matridisha;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DietFrame extends JFrame {
    public DietFrame() {
        setTitle("Matridisha - Guidance & Diet Chart");
        setSize(550, 500);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        Color deepPink = new Color(199, 21, 133);
        Color lightPink = new Color(255, 182, 193);

        // Header
        JLabel header = new JLabel("Nutritional Guide", JLabel.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 24));
        header.setForeground(deepPink);
        header.setBorder(new EmptyBorder(20, 10, 20, 10));
        add(header, BorderLayout.NORTH);

        // Tabs styling
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Arial", Font.BOLD, 12));
        tabs.setBackground(Color.WHITE);

        tabs.addTab("1st Trimester", createPanel("<html><b>Focus: Folate & Vitamin B6</b><br><br>1. Green Leafy Veggies<br>2. Citrus Fruits<br>3. Beans and Lentils<br>4. Eggs & Ginger</html>"));
        tabs.addTab("2nd Trimester", createPanel("<html><b>Focus: Calcium & Iron</b><br><br>1. Milk and Yogurt<br>2. Lean Meat/Fish<br>3. Nuts and Almonds<br>4. Whole Grains</html>"));
        tabs.addTab("3rd Trimester", createPanel("<html><b>Focus: Fiber & Vitamin C</b><br><br>1. High Fiber Foods<br>2. Seasonal Fruits<br>3. Coconut Water<br>4. Frequent healthy meals</html>"));

        add(tabs, BorderLayout.CENTER);

        // Back Button
        JButton backBtn = new JButton("Back to Dashboard");
        backBtn.setBackground(deepPink);
        backBtn.setForeground(Color.WHITE);
        backBtn.setFont(new Font("Arial", Font.BOLD, 14));
        backBtn.setFocusable(false);
        backBtn.addActionListener(e -> dispose());
        
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        btnPanel.add(backBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private JPanel createPanel(String text) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", Font.PLAIN, 16));
        l.setBorder(new EmptyBorder(20, 20, 20, 20));
        p.add(new JScrollPane(l));
        return p;
    }
}