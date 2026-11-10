package com.seras.ui;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.JTableHeader;

import com.seras.dao.ResourceDAO;
import com.seras.dao.EventDAO; // ✅ IMPORTANT
import com.seras.model.Resource;

import java.awt.*;
import java.util.ArrayList;

public class AdminPanel extends JPanel {

    JTable table;
    MainFrame frame;

    public AdminPanel(MainFrame frame) {

        this.frame = frame;
        setLayout(null);
        setBackground(new Color(245, 247, 250));

        // 🔥 TITLE
        JLabel title = new JLabel("Dashboard");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBounds(30, 20, 200, 30);
        add(title);

        // 🔥 DAO
        ResourceDAO rdao = new ResourceDAO();
        EventDAO edao = new EventDAO();

        // 🔥 CARDS
        JPanel card1 = createCard("Total Resources", String.valueOf(rdao.getTotalResources()));
        card1.setBounds(30, 70, 180, 80);
        add(card1);

        JPanel card2 = createCard("Events", String.valueOf(edao.getTotalEvents()));
        card2.setBounds(230, 70, 180, 80);
        add(card2);

        JPanel card3 = createCard("Conflicts", String.valueOf(edao.getConflictCount()));
        card3.setBounds(430, 70, 180, 80);
        add(card3);

        // 🔥 BUTTONS
        JButton addBtn = new JButton("Add Resource");
        addBtn.setBounds(30, 170, 150, 30);
        add(addBtn);

        JButton eventBtn = new JButton("Create Event");
        eventBtn.setBounds(200, 170, 150, 30);
        add(eventBtn);

        JButton deleteBtn = new JButton("Delete Resource"); // ✅ added
        deleteBtn.setBounds(370, 170, 150, 30);
        add(deleteBtn);

        // 🔥 TABLE
        table = new JTable();

        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(240, 240, 240));

        styleTable();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(30, 220, 600, 250);
        add(scrollPane);

        // 🔥 ADD ACTION
        addBtn.addActionListener(e -> {

            String name = JOptionPane.showInputDialog("Enter Resource Name:");
            if (name == null || name.isEmpty()) return;

            String type = JOptionPane.showInputDialog("Enter Type (Hall/Lab/Equipment)");

            new ResourceDAO().addResource(name, type);

            JOptionPane.showMessageDialog(null, "Added!");

            frame.showPanel("admin"); // ✅ reload everything clean
        });

        // 🔥 DELETE ACTION
        deleteBtn.addActionListener(e -> {

            int row = table.getSelectedRow();

            if (row == -1) {
                JOptionPane.showMessageDialog(null, "Select a row first!");
                return;
            }

            int id = Integer.parseInt(table.getValueAt(row, 0).toString());

            int confirm = JOptionPane.showConfirmDialog(null, "Delete this resource?");
            if (confirm == JOptionPane.YES_OPTION) {

                new ResourceDAO().deleteResource(id);

                JOptionPane.showMessageDialog(null, "Deleted!");
                
                frame.showPanel("admin"); // ✅ reload everything
            }
        });

        // 🔥 NAVIGATION
        eventBtn.addActionListener(e -> frame.showPanel("event"));

        loadTable();
    }

    // 🔥 CARD UI
    private JPanel createCard(String title, String value) {

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.WHITE);
        panel.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));

        JLabel t = new JLabel(title);
        t.setBounds(10, 10, 150, 20);

        JLabel v = new JLabel(value);
        v.setBounds(10, 35, 150, 30);
        v.setFont(new Font("Arial", Font.BOLD, 18));

        panel.add(t);
        panel.add(v);

        return panel;
    }

    // 🔥 TABLE STYLE
    private void styleTable() {

        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 247, 250));
                }

                return c;
            }
        });
    }

    // 🔥 REFRESH DASHBOARD
    public void refreshDashboard() {

        // Just update values, DO NOT recreate panel
        removeAll();

        setLayout(null);

        // 🔥 TITLE
        JLabel title = new JLabel("Dashboard");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBounds(30, 20, 200, 30);
        add(title);

        ResourceDAO rdao = new ResourceDAO();
        EventDAO edao = new EventDAO();

        JPanel card1 = createCard("Total Resources", String.valueOf(rdao.getTotalResources()));
        card1.setBounds(30, 70, 180, 80);
        add(card1);

        JPanel card2 = createCard("Events", String.valueOf(edao.getTotalEvents()));
        card2.setBounds(230, 70, 180, 80);
        add(card2);

        JPanel card3 = createCard("Conflicts", String.valueOf(edao.getConflictCount()));
        card3.setBounds(430, 70, 180, 80);
        add(card3);

        // 🔥 reload table
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(30, 220, 600, 250);
        add(scrollPane);

        loadTable();

        revalidate();
        repaint();
    }

    // 🔥 LOAD TABLE
    private void loadTable() {

        ArrayList<Resource> list = new ResourceDAO().getAllResources();

        String[][] data = new String[list.size()][3];

        for (int i = 0; i < list.size(); i++) {
            data[i][0] = String.valueOf(list.get(i).getId());
            data[i][1] = list.get(i).getName();
            data[i][2] = list.get(i).getType();
        }

        String[] columns = {"ID", "Name", "Type"};
        table.setModel(new javax.swing.table.DefaultTableModel(data, columns));
    }
}