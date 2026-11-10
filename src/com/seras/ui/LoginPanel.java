package com.seras.ui;

import javax.swing.*;

public class LoginPanel extends JPanel {

    MainFrame frame;

    public LoginPanel(MainFrame frame) {

        this.frame = frame;
        setLayout(null);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(200, 100, 100, 30);
        add(userLabel);

        JTextField userField = new JTextField();
        userField.setBounds(300, 100, 150, 30);
        add(userField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(200, 150, 100, 30);
        add(passLabel);

        JPasswordField passField = new JPasswordField();
        passField.setBounds(300, 150, 150, 30);
        add(passField);

        JLabel roleLabel = new JLabel("Login As:");
        roleLabel.setBounds(200, 190, 100, 30);
        add(roleLabel);

        String[] roles = {"ADMIN", "USER"};
        JComboBox<String> roleBox = new JComboBox<>(roles);
        roleBox.setBounds(300, 190, 150, 30);
        add(roleBox);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBounds(260, 240, 120, 30);
        add(loginBtn);

        // 🔥 LOGIN ACTION
        loginBtn.addActionListener(e -> {

            String username = userField.getText();
            String password = new String(passField.getPassword());
            String selectedRole = (String) roleBox.getSelectedItem();

            // 🔥 HARDCODED CREDENTIALS
            String adminUser = "admin";
            String adminPass = "123";

            String normalUser = "user";
            String normalPass = "123";

            if (selectedRole.equalsIgnoreCase("ADMIN")) {

                if (username.equals(adminUser) && password.equals(adminPass)) {

                    frame.setRole("ADMIN"); // ✅ IMPORTANT
                    JOptionPane.showMessageDialog(this, "Admin Login Successful");
                    frame.showPanel("admin");

                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Admin Credentials");
                }

            } else if (selectedRole.equalsIgnoreCase("USER")) {

                if (username.equals(normalUser) && password.equals(normalPass)) {

                    frame.setRole("USER"); // ✅ IMPORTANT
                    JOptionPane.showMessageDialog(this, "User Login Successful");
                    frame.showPanel("organizer");

                } else {
                    JOptionPane.showMessageDialog(this, "Invalid User Credentials");
                }
            }
        });
    }
}