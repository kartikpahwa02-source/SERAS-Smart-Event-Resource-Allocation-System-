package com.seras.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import com.seras.dao.EventDAO;
import com.seras.util.DBConnection;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RequestPanel extends JPanel {

    JTable table;
    DefaultTableModel model;
    MainFrame frame;
    String role;

    public RequestPanel(MainFrame frame) {

        this.frame = frame;
        this.role = frame.currentRole;

        setLayout(null);

        JLabel title = new JLabel("Requests Panel");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setBounds(300, 20, 300, 30);
        add(title);

        // 🔥 MODEL
        model = new DefaultTableModel() {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        model.setColumnIdentifiers(new String[]{
                "ID", "Name", "Resource", "Date", "Time", "Priority", "Status"
        });

        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(50, 70, 800, 300);
        add(scroll);

        // 🔥 COLOR SYSTEM (KEEPED)
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {

            Map<String, Color> groupColors = new HashMap<>();
            Color[] palette = {
                    new Color(255, 220, 220),
                    new Color(220, 255, 220),
                    new Color(220, 220, 255),
                    new Color(255, 255, 200)
            };

            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                if (isSelected) {
                    c.setBackground(new Color(70, 130, 255));
                    c.setForeground(Color.WHITE);
                    return c;
                }

                try {
                    String status = table.getValueAt(row, 6).toString();
                    int resource = Integer.parseInt(table.getValueAt(row, 2).toString());
                    String date = table.getValueAt(row, 3).toString();

                    String key = resource + "_" + date;

                    if (!groupColors.containsKey(key)) {
                        groupColors.put(key, palette[groupColors.size() % palette.length]);
                    }

                    if (status.equalsIgnoreCase("APPROVED")) {
                        c.setBackground(new Color(144, 238, 144));
                    } else if (status.equalsIgnoreCase("REJECTED")) {
                        c.setBackground(new Color(255, 182, 193));
                    } else {
                        c.setBackground(groupColors.get(key));
                    }

                    c.setForeground(Color.BLACK);

                } catch (Exception e) {
                    c.setBackground(Color.WHITE);
                }

                return c;
            }
        });

        // 🔥 BOTTOM PANEL
        JPanel bottomPanel = new JPanel(null);
        bottomPanel.setBounds(50, 380, 850, 120);
        add(bottomPanel);

        JButton approveBtn = new JButton("Approve");
        approveBtn.setBounds(50, 20, 120, 35);
        bottomPanel.add(approveBtn);

        JButton rejectBtn = new JButton("Reject");
        rejectBtn.setBounds(190, 20, 120, 35);
        bottomPanel.add(rejectBtn);

        JButton backBtn = new JButton("Back");
        backBtn.setBounds(330, 20, 120, 35);
        bottomPanel.add(backBtn);

        JLabel timeLabel = new JLabel("New Time:");
        timeLabel.setBounds(480, 0, 100, 20);
        bottomPanel.add(timeLabel);

        SpinnerDateModel startModel = new SpinnerDateModel();
        JSpinner startSpinner = new JSpinner(startModel);
        startSpinner.setBounds(480, 20, 80, 30);
        startSpinner.setEditor(new JSpinner.DateEditor(startSpinner, "HH:mm"));
        bottomPanel.add(startSpinner);

        SpinnerDateModel endModel = new SpinnerDateModel();
        JSpinner endSpinner = new JSpinner(endModel);
        endSpinner.setBounds(570, 20, 80, 30);
        endSpinner.setEditor(new JSpinner.DateEditor(endSpinner, "HH:mm"));
        bottomPanel.add(endSpinner);

        String[] priorities = {"HIGH", "MEDIUM", "LOW"};
        JComboBox<String> priorityBox = new JComboBox<>(priorities);
        priorityBox.setBounds(670, 20, 100, 30);
        bottomPanel.add(priorityBox);

        JButton updatePriorityBtn = new JButton("Update Priority");
        updatePriorityBtn.setBounds(670, 60, 140, 30); // 🔥 FIXED POSITION
        bottomPanel.add(updatePriorityBtn);

        JButton sendBtn = new JButton("Send Suggestion");
        sendBtn.setBounds(480, 60, 160, 30);
        bottomPanel.add(sendBtn);

        loadRequests();

        // 🔥 USER MODE
        if (role.equalsIgnoreCase("USER")) {
            approveBtn.setVisible(false);
            rejectBtn.setVisible(false);
            priorityBox.setVisible(false);
            updatePriorityBtn.setVisible(false);
            startSpinner.setVisible(false);
            endSpinner.setVisible(false);
            timeLabel.setVisible(false);
            sendBtn.setVisible(false);
        }

        // ✅ APPROVE
        approveBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) return;

            int id = Integer.parseInt(model.getValueAt(row, 0).toString());

            new EventDAO().updateStatus(id, "APPROVED");

            loadRequests();
        });

        // ❌ REJECT
        rejectBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) return;

            int id = Integer.parseInt(model.getValueAt(row, 0).toString());

            new EventDAO().updateStatus(id, "REJECTED");
            loadRequests();
        });

        // 🔥 SEND SUGGESTION
        sendBtn.addActionListener(e -> {

            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a request!");
                return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            String newStart = sdf.format(startSpinner.getValue());
            String newEnd = sdf.format(endSpinner.getValue());

            String oldTime = model.getValueAt(row, 4).toString();

            if (oldTime.equals(newStart + " - " + newEnd)) {
                JOptionPane.showMessageDialog(this, "Choose different time!");
                return;
            }

            int id = Integer.parseInt(model.getValueAt(row, 0).toString());
            int resourceId = Integer.parseInt(model.getValueAt(row, 2).toString());
            String date = model.getValueAt(row, 3).toString();

            String start = oldTime.split(" - ")[0];
            String end = oldTime.split(" - ")[1];

            EventDAO dao = new EventDAO();

            dao.sendRescheduleSuggestions(id, resourceId, date, start, end, newStart, newEnd);
            dao.updateStatus(id, "WAITING");

            loadRequests();
        });

        // 🔥 PRIORITY UPDATE
        updatePriorityBtn.addActionListener(e -> {

            int row = table.getSelectedRow();
            if (row == -1) return;

            int id = Integer.parseInt(model.getValueAt(row, 0).toString());
            String newPriority = priorityBox.getSelectedItem().toString();

            try {
                Connection conn = DBConnection.getConnection();

                PreparedStatement ps = conn.prepareStatement(
                        "UPDATE events SET priority=? WHERE id=?"
                );

                ps.setString(1, newPriority);
                ps.setInt(2, id);
                ps.executeUpdate();

                loadRequests();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        backBtn.addActionListener(e ->
                frame.showPanel(role.equalsIgnoreCase("ADMIN") ? "admin" : "organizer")
        );
    }

    private void loadRequests() {

        model.setRowCount(0);

        ArrayList<String[]> list = new EventDAO().getAllEvents();

        for (String[] row : list) {
            if (row[6].equalsIgnoreCase("PENDING") || row[6].equalsIgnoreCase("REJECTED")) {
                model.addRow(row);
            }
        }
    }

    public void refreshTable() {
        loadRequests(); // ✅ FIXED
    }
}