package matridisha;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.*;

public class ProfilePanel extends JPanel {

    // ===============================
    // 1️⃣ Constructor & UI Layout
    // ===============================
    public ProfilePanel(DashboardFrame parent, String user) {
        setLayout(new BorderLayout());
        setBackground(new Color(255, 240, 245));

        // Profile Card Container
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(40, 40, 40, 40));

        // Header
        JLabel title = new JLabel("YOUR PROFILE");
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setForeground(new Color(199, 21, 133));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Data Table Layout
        JPanel table = new JPanel(new GridLayout(7, 2, 0, 15));
        table.setBackground(Color.WHITE);

        // Value Labels Initialization
        JLabel n = new JLabel("..."); 
        JLabel a = new JLabel("-"); 
        JLabel w = new JLabel("-");
        JLabel wk = new JLabel("-"); 
        JLabel c = new JLabel("-"); 
        JLabel dv = new JLabel("-"); 
        JLabel ds = new JLabel("-");

        // Adding Rows to Table
        addR(table, "Full Name:", n);
        addR(table, "Age:", a);
        addR(table, "Weight:", w);
        addR(table, "Week:", wk);
        addR(table, "Previous Child:", c);
        addR(table, "Delivery:", dv);
        addR(table, "Condition:", ds);

        // ===============================
        // 2️⃣ Database: Fetch User Profile
        // ===============================
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement pst = conn.prepareStatement("SELECT * FROM users WHERE username=?");
            pst.setString(1, user);
            ResultSet rs = pst.executeQuery();
            
            if(rs.next()){
                n.setText(rs.getString("name"));
                a.setText(rs.getString("age"));
                w.setText(rs.getString("weight"));
                wk.setText(rs.getString("current_weeks"));
                c.setText(rs.getString("prev_child"));
                dv.setText(rs.getString("delivery_type"));
                ds.setText(rs.getString("current_disease"));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        // ===============================
        // 3️⃣ Navigation & Assembly
        // ===============================
        JButton back = new JButton("← Back to Dashboard");
        back.setBackground(new Color(199, 21, 133));
        back.setForeground(Color.WHITE);
        back.setFont(new Font("Arial", Font.BOLD, 14));
        back.setAlignmentX(Component.CENTER_ALIGNMENT);
        back.setFocusable(false);
        back.addActionListener(e -> parent.showDashboard());

        // Assembly
        card.add(title); 
        card.add(Box.createRigidArea(new Dimension(0, 30)));
        card.add(table); 
        card.add(Box.createRigidArea(new Dimension(0, 30)));
        card.add(back);

        // Centering the card on the panel
        JPanel centerer = new JPanel(new GridBagLayout());
        centerer.setOpaque(false);
        centerer.add(card);
        add(centerer, BorderLayout.CENTER);
    }

    // ===============================
    // 4️⃣ UI Helper: Row Creator
    // ===============================
    private void addR(JPanel p, String l, JLabel v) {
        JLabel lbl = new JLabel(l); 
        lbl.setFont(new Font("Arial", Font.BOLD, 14));
        v.setHorizontalAlignment(SwingConstants.RIGHT);
        v.setFont(new Font("Arial", Font.PLAIN, 14));
        p.add(lbl); 
        p.add(v);
    }
}