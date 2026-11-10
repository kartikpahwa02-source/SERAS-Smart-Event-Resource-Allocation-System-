package com.seras.ui;

import javax.swing.*;
import javax.swing.border.LineBorder;

import com.seras.dao.EventDAO;
import com.seras.dao.ResourceDAO;
import com.seras.model.Resource;
import com.toedter.calendar.JDateChooser; // 🔥 DATE PICKER

import java.awt.*;
import java.util.ArrayList;
import java.text.SimpleDateFormat;

public class EventPanel extends JPanel {

    MainFrame frame;
    String role;

    public EventPanel(MainFrame frame, String role) {

        this.frame = frame;
        this.role = role;

        setLayout(null);
        setBackground(new Color(245, 247, 250));

        JLabel title = new JLabel("Schedule Event");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setBounds(30, 20, 200, 30);
        add(title);

        JPanel card = new JPanel();
        card.setLayout(null);
        card.setBackground(Color.WHITE);
        card.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));
        card.setBounds(100, 80, 450, 420);
        add(card);

        JLabel nameLabel = new JLabel("Event Name");
        nameLabel.setBounds(30, 20, 200, 20);
        card.add(nameLabel);

        JTextField nameField = new JTextField();
        nameField.setBounds(30, 40, 350, 30);
        card.add(nameField);

        JLabel resLabel = new JLabel("Resource");
        resLabel.setBounds(30, 80, 200, 20);
        card.add(resLabel);

        JComboBox<String> resourceBox = new JComboBox<>();
        resourceBox.setBounds(30, 100, 350, 30);
        card.add(resourceBox);

        // 🔥 DATE PICKER
        JLabel dateLabel = new JLabel("Select Date");
        dateLabel.setBounds(30, 140, 200, 20);
        card.add(dateLabel);

        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setBounds(30, 160, 350, 30);
        card.add(dateChooser);

        // 🔥 TIME PICKER
        JLabel timeLabel = new JLabel("Time");
        timeLabel.setBounds(30, 200, 200, 20);
        card.add(timeLabel);

        SpinnerDateModel startModel = new SpinnerDateModel();
        JSpinner startSpinner = new JSpinner(startModel);
        startSpinner.setBounds(30, 220, 160, 30);
        JSpinner.DateEditor startEditor = new JSpinner.DateEditor(startSpinner, "HH:mm");
        startSpinner.setEditor(startEditor);
        card.add(startSpinner);

        SpinnerDateModel endModel = new SpinnerDateModel();
        JSpinner endSpinner = new JSpinner(endModel);
        endSpinner.setBounds(220, 220, 160, 30);
        JSpinner.DateEditor endEditor = new JSpinner.DateEditor(endSpinner, "HH:mm");
        endSpinner.setEditor(endEditor);
        card.add(endSpinner);

        // 🔥 PRIORITY
        JLabel priorityLabel = new JLabel("Priority");
        priorityLabel.setBounds(30, 260, 200, 20);
        card.add(priorityLabel);

        String[] priorities = {"HIGH", "MEDIUM", "LOW"};
        JComboBox<String> priorityBox = new JComboBox<>(priorities);
        priorityBox.setBounds(30, 280, 200, 30);
        card.add(priorityBox);

        if (role.equalsIgnoreCase("USER")) {
            priorityBox.setSelectedItem("LOW");
            priorityBox.setEnabled(false);
        }

        JButton createBtn = new JButton("Create Event");
        createBtn.setBounds(30, 330, 150, 35);
        createBtn.setBackground(new Color(33, 115, 255));
        createBtn.setForeground(Color.WHITE);
        card.add(createBtn);

        JButton backBtn = new JButton("Back");
        backBtn.setBounds(200, 330, 100, 35);
        card.add(backBtn);

        loadResources(resourceBox);

        // 🔥 ACTION
        createBtn.addActionListener(e -> {

            String name = nameField.getText();
            String resourceText = (String) resourceBox.getSelectedItem();

            if (dateChooser.getDate() == null) {
                JOptionPane.showMessageDialog(null, "Select a date!");
                return;
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String date = dateFormat.format(dateChooser.getDate());

            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            String start = timeFormat.format(startSpinner.getValue());
            String end = timeFormat.format(endSpinner.getValue());

            String priority = priorityBox.getSelectedItem().toString();

            if (name.isEmpty() || resourceText == null) {
                JOptionPane.showMessageDialog(null, "Fill all fields!");
                return;
            }

            int resourceId = Integer.parseInt(resourceText.split(" - ")[0]);

            EventDAO dao = new EventDAO();

            try {
                String status = role.equalsIgnoreCase("USER") ? "PENDING" : "APPROVED";

                dao.addEvent(name, resourceId, date, start, end, priority, status);

                if (role.equalsIgnoreCase("USER")) {
                    JOptionPane.showMessageDialog(null, "Request Sent to Admin!");
                } else {
                    JOptionPane.showMessageDialog(null, "Event Created!");
                }

                // reset
                nameField.setText("");
                dateChooser.setDate(null);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        });

        backBtn.addActionListener(e -> {
            if (role.equalsIgnoreCase("ADMIN")) {
                frame.showPanel("admin");
            } else {
                frame.showPanel("organizer");
            }
        });
    }

    private void loadResources(JComboBox<String> box) {

        box.removeAllItems();

        ArrayList<Resource> list = new ResourceDAO().getAllResources();

        for (Resource r : list) {
            box.addItem(r.getId() + " - " + r.getName());
        }
    }
}