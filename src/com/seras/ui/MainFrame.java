package com.seras.ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    JPanel contentPanel;
    CardLayout cardLayout;

    // 🔥 CURRENT ROLE TRACKING
    public String currentRole;

    public MainFrame() {

        setTitle("SERAS Dashboard");
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 🔥 SIDEBAR (FIX WIDTH ISSUE)
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(200, 600)); // ✅ IMPORTANT FIX
        sidebar.setBackground(new Color(18, 24, 38));
        sidebar.setLayout(null);
        add(sidebar, BorderLayout.WEST);

        // 🔥 LOGO
        JLabel logo = new JLabel("SERAS");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logo.setBounds(50, 20, 100, 30);
        sidebar.add(logo);

        // 🔥 MAIN CONTENT PANEL
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        add(contentPanel, BorderLayout.CENTER);

        // 🔥 INITIAL PANEL
        contentPanel.add(new LoginPanel(this), "login");

        // 🔥 SIDEBAR BUTTONS
        JButton dashboardBtn = createSidebarButton("Dashboard", 80);
        JButton resourceBtn = createSidebarButton("Resources", 130);
        JButton eventBtn = createSidebarButton("Events", 180);
        JButton eventListBtn = createSidebarButton("View Events", 230);
        JButton requestBtn = createSidebarButton("Requests", 280);
        JButton notifBtn = createSidebarButton("Notifications", 330);
        JButton logoutBtn = createSidebarButton("Logout", 500);

        sidebar.add(dashboardBtn);
        sidebar.add(resourceBtn);
        sidebar.add(eventBtn);
        sidebar.add(eventListBtn);
        sidebar.add(requestBtn);
        sidebar.add(notifBtn);
        sidebar.add(logoutBtn);

        // 🔥 HOVER EFFECT
        addHoverEffect(dashboardBtn);
        addHoverEffect(resourceBtn);
        addHoverEffect(eventBtn);
        addHoverEffect(eventListBtn);
        addHoverEffect(requestBtn);
        addHoverEffect(notifBtn); // ✅ FIXED (was missing)
        addHoverEffect(logoutBtn);

        // 🔥 BUTTON ACTIONS
        dashboardBtn.addActionListener(e -> showPanel("admin"));
        resourceBtn.addActionListener(e -> showPanel("admin"));
        eventBtn.addActionListener(e -> showPanel("event"));
        eventListBtn.addActionListener(e -> showPanel("eventList"));
        requestBtn.addActionListener(e -> showPanel("requests"));
        notifBtn.addActionListener(e -> showPanel("notifications")); // ✅ WORKING NOW
        logoutBtn.addActionListener(e -> showPanel("login"));

        // 🔥 START PANEL
        cardLayout.show(contentPanel, "login");

        setVisible(true);
    }

    // 🔥 SET ROLE FROM LOGIN
    public void setRole(String role) {
        this.currentRole = role;
    }

    // 🔥 PANEL SWITCH METHOD (FIXED)
    public void showPanel(String name) {

        contentPanel.removeAll();

        switch (name) {

            case "admin":
                contentPanel.add(new AdminPanel(this), "admin");
                break;

            case "organizer":
                contentPanel.add(new OrganizerDashboard(), "organizer");
                break;

            case "event":
                contentPanel.add(new EventPanel(this, currentRole), "event");
                break;

            case "eventList":
                contentPanel.add(new EventListPanel(this), "eventList");
                break;

            case "requests":
                contentPanel.add(new RequestPanel(this), "requests");
                break;

            case "notifications": // ✅ THIS WAS MISSING → CAUSED BLANK SCREEN
                contentPanel.add(new NotificationPanel(this), "notifications");
                break;

            case "login":
            default:
                contentPanel.add(new LoginPanel(this), "login");
                break;
        }

        cardLayout.show(contentPanel, name);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // 🔥 BUTTON STYLE
    private JButton createSidebarButton(String text, int y) {

        JButton btn = new JButton(text);
        btn.setBounds(20, y, 160, 40);

        btn.setFocusPainted(false);
        btn.setBackground(new Color(18, 24, 38));
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder());

        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        return btn;
    }

    // 🔥 HOVER EFFECT
    private void addHoverEffect(JButton btn) {

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(40, 60, 90));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(18, 24, 38));
            }
        });
    }
}