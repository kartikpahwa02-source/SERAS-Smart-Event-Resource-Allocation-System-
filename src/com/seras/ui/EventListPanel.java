package com.seras.ui;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.seras.dao.EventDAO;
import java.awt.*;
import java.util.ArrayList;

public class EventListPanel extends JPanel {

    JTable table;
    MainFrame frame;

    public EventListPanel(MainFrame frame) {

        this.frame = frame;
        setLayout(null);
        setBackground(new Color(245, 247, 250));

        // 🔥 TITLE
        JLabel title = new JLabel("Events Overview");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setBounds(30, 20, 200, 30);
        add(title);

        JButton usageBtn = new JButton("Resource Usage");
        usageBtn.setBounds(200, 20, 140, 30);
        add(usageBtn);

        JButton filterBtn = new JButton("Filter by Resource");
        filterBtn.setBounds(350, 20, 160, 30);
        add(filterBtn);

        JButton resetBtn = new JButton("Show All");
        resetBtn.setBounds(520, 20, 120, 30);
        add(resetBtn);

        JButton deleteBtn = new JButton("Delete Event");
        deleteBtn.setBounds(650, 20, 140, 30);
        add(deleteBtn);

        JButton backBtn = new JButton("Back");
        backBtn.setBounds(800, 20, 100, 30);
        add(backBtn);

        // 🔥 CARDS
        EventDAO dao = new EventDAO();

        JPanel totalCard = createCard("Total Events", String.valueOf(dao.getTotalEvents()));
        totalCard.setBounds(30, 70, 200, 80);
        add(totalCard);

        JPanel todayCard = createCard("Today Events", String.valueOf(dao.getTodayEvents()));
        todayCard.setBounds(250, 70, 200, 80);
        add(todayCard);

        JPanel topCard = createCard("Top Resource", "N/A");
        topCard.setBounds(470, 70, 200, 80);
        add(topCard);

        // 🔥 TABLE
        table = new JTable();
        styleTable();

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(30, 170, 620, 300);
        add(scroll);

        // 🔥 ACTIONS

        // FILTER
        filterBtn.addActionListener(e -> {

            String input = JOptionPane.showInputDialog("Enter Resource ID:");
            if (input == null || input.isEmpty()) return;

            int id = Integer.parseInt(input);

            ArrayList<String[]> list = new EventDAO().getEventsByResource(id);
            setTableData(list);
        });

        // RESET
        resetBtn.addActionListener(e -> loadTable());

        // USAGE
        usageBtn.addActionListener(e -> {

            ArrayList<String[]> list = new EventDAO().getResourceUsage();

            String[][] data = new String[list.size()][2];

            for (int i = 0; i < list.size(); i++) {
                data[i] = list.get(i);
            }

            String[] cols = {"Resource ID", "Usage Count"};

            table.setModel(new DefaultTableModel(data, cols) {
                public boolean isCellEditable(int r, int c) { return false; }
            });
        });

        // DELETE
        deleteBtn.addActionListener(e -> {

            String input = JOptionPane.showInputDialog("Enter Event ID to delete:");
            if (input == null || input.isEmpty()) return;

            int id = Integer.parseInt(input);

            int confirm = JOptionPane.showConfirmDialog(
                    null, "Delete this event?", "Confirm", JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {

                new EventDAO().deleteEvent(id);
                JOptionPane.showMessageDialog(null, "Event Deleted!");
                loadTable();
            }
        });

        // BACK
        backBtn.addActionListener(e -> frame.showPanel("admin"));

        loadTable();
    }

    // 🔥 SET TABLE DATA (REUSABLE)
    private void setTableData(ArrayList<String[]> list) {

        String[][] data = new String[list.size()][5];

        for (int i = 0; i < list.size(); i++) {
            data[i] = list.get(i);
        }

        String[] cols = {"ID", "Event", "Resource", "Date", "Time"};

        table.setModel(new DefaultTableModel(data, cols) {
            public boolean isCellEditable(int r, int c) { return false; }
        });
    }

    // 🔥 LOAD APPROVED EVENTS ONLY
    private void loadTable() {
        ArrayList<String[]> list = new EventDAO().getApprovedEvents();
        setTableData(list);
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
        v.setFont(new Font("Segoe UI", Font.BOLD, 18));

        panel.add(t);
        panel.add(v);

        return panel;
    }

    // 🔥 TABLE STYLE
    private void styleTable() {

        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(240, 240, 240));

        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 247, 250));
                }

                return c;
            }
        });
    }
    
}