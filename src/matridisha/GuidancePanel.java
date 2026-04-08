package matridisha;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class GuidancePanel extends JPanel {
    DashboardFrame parent;
    Color darkPink = new Color(199, 21, 133);
    Color lightPink = new Color(255, 182, 193);
    Color bgPink = new Color(255, 245, 247);

    public GuidancePanel(DashboardFrame parent, boolean isBeforeBirth) {
        this.parent = parent;
        setLayout(new BorderLayout());
        setBackground(bgPink);

        JLabel header = new JLabel(isBeforeBirth ? "Prenatal (Before Birth) Guide" : "Postnatal (After Birth) Care", JLabel.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 24));
        header.setForeground(darkPink);
        header.setBorder(new EmptyBorder(25, 0, 25, 0));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(bgPink);
        contentPanel.setBorder(new EmptyBorder(10, 35, 10, 35));

        if (isBeforeBirth) {
            contentPanel.add(createCard("1st Trimester (0-12 Weeks)", "Essential Tips: Start taking Folic Acid daily to prevent neural tube defects. Focus on eating light, frequent meals to combat morning sickness. Hydration is key—drink at least 2-3 liters of water. Avoid any form of raw meat or unpasteurized dairy. Light walking for 15-20 minutes is recommended."));
            contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            contentPanel.add(createCard("2nd Trimester (13-26 Weeks)", "Growth Phase: This is often called the 'honeymoon period' of pregnancy. Your baby is developing bones, so increase Calcium intake (Milk, Cheese, Spinach). You may start feeling baby movements. Practice prenatal yoga and maintain a side-sleeping position to ensure optimal blood flow to the baby."));
            contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            contentPanel.add(createCard("3rd Trimester (27-40 Weeks)", "Final Preparation: Your baby's brain is growing rapidly. Eat foods rich in Omega-3 fatty acids. Focus on pelvic floor exercises (Kegels) to prepare for labor. Prepare your hospital bag and monitor baby's kick counts. If you notice any sudden swelling or vision changes, contact your doctor immediately."));
        } else {
            // Detailed After Birth Care (200+ words)
            contentPanel.add(createCard("Physical Recovery & Healing", 
                "The postpartum period (the first 6 weeks after delivery) is crucial for a mother's recovery. " +
                "Whether you had a normal delivery or a C-section, your body needs significant rest. " +
                "For C-section mothers, it is vital to keep the incision area dry and avoid lifting heavy objects for at least 6 weeks. " +
                "Perineal care is essential for normal delivery recovery to prevent infection. " +
                "Nutrition plays a major role; increase your intake of protein (eggs, lentils, chicken) to help your tissues heal. " +
                "Avoid spicy or oily foods initially to prevent digestive issues. Stay hydrated to support milk production and prevent constipation."));

            contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));

            contentPanel.add(createCard("Mental Health & Emotional Well-being", 
                "Many mothers experience 'Baby Blues' due to sudden hormonal shifts, characterized by mood swings, crying spells, and anxiety. " +
                "This usually resolves within two weeks. However, if these feelings persist or worsen, it may indicate Postpartum Depression (PPD). " +
                "Signs of PPD include persistent sadness, lack of energy, or difficulty bonding with your baby. " +
                "Do not suffer in silence—speak to your partner, family, or a healthcare professional. " +
                "Prioritize sleep; whenever the baby naps, try to rest as well. Mental exhaustion can worsen physical recovery and mood stability. " +
                "Remember, it is okay to ask for help with household chores or baby care."));

            contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));

            contentPanel.add(createCard("Newborn Care & Breastfeeding", 
                "Exclusive breastfeeding is recommended for the first 6 months as breast milk contains all necessary nutrients and antibodies. " +
                "Ensure a proper latch to prevent nipple soreness. Newborns need to feed every 2-3 hours initially. " +
                "Hygiene is vital—wash your hands before handling the baby. Keep the umbilical cord stump clean; it will fall off naturally in 1-2 weeks. " +
                "Monitor the baby for signs like yellow skin (Jaundice) or high fever. " +
                "Skin-to-skin contact (Kangaroo Care) is highly beneficial for the baby's emotional security and temperature regulation. " +
                "Strictly follow the EPI vaccination schedule to protect your child from preventable diseases."));
        }

        JScrollPane scroll = new JScrollPane(contentPanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        JButton backBtn = new JButton("← Back to Dashboard");
        backBtn.setBackground(darkPink);
        backBtn.setForeground(Color.WHITE);
        backBtn.setFont(new Font("Arial", Font.BOLD, 15));
        backBtn.setPreferredSize(new Dimension(0, 55));
        backBtn.addActionListener(e -> parent.showDashboard());

        add(header, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(backBtn, BorderLayout.SOUTH);
    }

    private JPanel createCard(String title, String info) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(lightPink, 2),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Arial", Font.BOLD, 16));
        titleLbl.setForeground(darkPink);
        titleLbl.setBorder(new EmptyBorder(0, 0, 8, 0));

        JTextArea area = new JTextArea(info);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font("Arial", Font.PLAIN, 14));
        area.setBackground(Color.WHITE);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(area, BorderLayout.CENTER);
        return card;
    }
}