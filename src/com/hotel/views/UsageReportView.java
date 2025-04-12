package com.hotel.views;

import com.hotel.database.InventoryDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UsageReportView extends JFrame {

    private DefaultTableModel tableModel;

    public UsageReportView() {
        setTitle("📊 Inventory Usage Report");
        setSize(600, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Usage Analytics", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        String[] columns = {"Item Name", "Total Usage"};
        tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
        add(scrollPane, BorderLayout.CENTER);

        loadUsageReport();
        setVisible(true);
    }

    private void loadUsageReport() {
        List<String> usageData = new InventoryDAO().generateUsageReport();
        tableModel.setRowCount(0);

        for (String entry : usageData) {
            String[] parts = entry.split(":");
            if (parts.length == 2) {
                tableModel.addRow(new Object[]{parts[0].trim(), parts[1].trim()});
            }
        }
    }
}
