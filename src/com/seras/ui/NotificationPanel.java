package com.seras.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.seras.util.DBConnection;

import java.awt.*;
import java.sql.*;

public class NotificationPanel extends JPanel {

    JTable table;
    DefaultTableModel model;

    public NotificationPanel(MainFrame frame) {

        setLayout(null);

        JLabel title = new JLabel("Notifications");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setBounds(300, 20, 200, 30);
        add(title);

        // 🔥 MODEL (NON EDITABLE)
        model = new DefaultTableModel() {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        model.setColumnIdentifiers(new String[]{
                "ID", "Event ID", "Message", "Start", "End", "Status"
        });

        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(50, 70, 800, 300);
        add(scroll);

        // 🔥 BUTTONS (ALIGNED)
        JButton acceptBtn = new JButton("Accept");
        acceptBtn.setBounds(250, 400, 120, 35);
        add(acceptBtn);

        JButton rejectBtn = new JButton("Reject");
        rejectBtn.setBounds(400, 400, 120, 35);
        add(rejectBtn);

        loadData();

        // ✅ ACCEPT
        acceptBtn.addActionListener(e -> {

            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a notification!");
                return;
            }

            Object val = model.getValueAt(row, 1);
            if (val == null) {
                JOptionPane.showMessageDialog(this, "Invalid data!");
                return;
            }

            int notifId = Integer.parseInt(model.getValueAt(row, 0).toString());
            int eventId = Integer.parseInt(val.toString());

            String start = model.getValueAt(row, 3).toString();
            String end = model.getValueAt(row, 4).toString();

            try {
                Connection conn = DBConnection.getConnection();

                // 🔥 UPDATE EVENT (TIME + APPROVED)
                PreparedStatement ps = conn.prepareStatement(
                        "UPDATE events SET start_time=?, end_time=?, status='APPROVED' WHERE id=?"
                );
                ps.setString(1, start);
                ps.setString(2, end);
                ps.setInt(3, eventId);
                ps.executeUpdate();

                // 🔥 UPDATE NOTIFICATION
                PreparedStatement ps2 = conn.prepareStatement(
                        "UPDATE notifications SET status='ACCEPTED' WHERE id=?"
                );
                ps2.setInt(1, notifId);
                ps2.executeUpdate();

                JOptionPane.showMessageDialog(this, "Accepted!");
                loadData();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // ❌ REJECT
        rejectBtn.addActionListener(e -> {

            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a notification!");
                return;
            }

            int notifId = Integer.parseInt(model.getValueAt(row, 0).toString());
            int eventId = Integer.parseInt(model.getValueAt(row, 1).toString());

            try {
                Connection conn = DBConnection.getConnection();

                // 🔥 DELETE EVENT
                PreparedStatement ps = conn.prepareStatement(
                        "DELETE FROM events WHERE id=?"
                );
                ps.setInt(1, eventId);
                ps.executeUpdate();

                // 🔥 UPDATE NOTIFICATION
                PreparedStatement ps2 = conn.prepareStatement(
                        "UPDATE notifications SET status='REJECTED' WHERE id=?"
                );
                ps2.setInt(1, notifId);
                ps2.executeUpdate();

                JOptionPane.showMessageDialog(this, "Rejected!");
                loadData();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    // 🔥 LOAD ONLY ACTIVE NOTIFICATIONS
    private void loadData() {

        model.setRowCount(0);

        try {
            Connection conn = DBConnection.getConnection();

            ResultSet rs = conn.createStatement().executeQuery(
                    "SELECT * FROM notifications WHERE status='PENDING'"
            );

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getInt("event_id"),
                        rs.getString("message"),
                        rs.getString("suggested_start"),
                        rs.getString("suggested_end"),
                        rs.getString("status")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔥 OPTIONAL MANUAL REFRESH (if needed)
    public void refreshTable() {
        loadData();
    }
}