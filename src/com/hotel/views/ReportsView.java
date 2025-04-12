package com.hotel.views;

import com.hotel.database.BillDAO;
import com.hotel.database.InventoryDAO;
import com.hotel.database.RoomDAO;
import com.hotel.models.Bill;
import com.hotel.models.InventoryItem;
import com.hotel.models.Room;
import com.hotel.models.Service;
import com.hotel.models.User;
import java.awt.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.*;

public class ReportsView extends JFrame {
    private final User user;
    private JLabel revenueLabel, totalRoomsLabel, occupiedLabel, availableLabel, lowStockLabel;
    private JTable usageTable;
    private DefaultTableModel usageTableModel;
    private Map<String, Integer> usageMap;
    private Map<String, Double> revenueMap;

    public ReportsView(User user) {
        this.user = user;
        setTitle("Hotel Reports");
        setSize(1000, 700);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setResizable(false);

        JLabel title = new JLabel("\uD83D\uDCCA Hotel Analytics Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        contentPanel.setBackground(Color.WHITE);

        revenueLabel = makeStyledLabel();
        totalRoomsLabel = makeStyledLabel();
        occupiedLabel = makeStyledLabel();
        availableLabel = makeStyledLabel();
        lowStockLabel = makeStyledLabel();

        contentPanel.add(revenueLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(totalRoomsLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(occupiedLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(availableLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(lowStockLabel);
        contentPanel.add(Box.createVerticalStrut(20));

        JLabel usageTitle = new JLabel("\uD83D\uDCCC Service Usage & Revenue Report");
        usageTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        contentPanel.add(usageTitle);
        contentPanel.add(Box.createVerticalStrut(10));

        String[] usageCols = {"Service", "Usage Count", "Revenue Generated"};
        usageTableModel = new DefaultTableModel(usageCols, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        usageTable = new JTable(usageTableModel);
        usageTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usageTable.setRowHeight(26);
        usageTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        JScrollPane usageScroll = new JScrollPane(usageTable);
        usageScroll.setPreferredSize(new Dimension(700, 160));
        usageScroll.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        contentPanel.add(usageScroll);

        loadReportData();

        JPanel chartsWrapper = new JPanel();
        chartsWrapper.setLayout(new BoxLayout(chartsWrapper, BoxLayout.Y_AXIS));
        chartsWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel pieWrapper = new JPanel(new BorderLayout());
        pieWrapper.add(new PieChartPanel(usageMap), BorderLayout.CENTER);
        JLabel pieLabel = new JLabel("\uD83D\uDFE0 Service Usage Distribution", SwingConstants.CENTER);
        pieLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pieWrapper.add(pieLabel, BorderLayout.SOUTH);

        JPanel barWrapper = new JPanel(new BorderLayout());
        barWrapper.add(new BarChartPanel(revenueMap), BorderLayout.CENTER);
        JLabel barLabel = new JLabel("\uD83D\uDD35 Revenue Breakdown by Service", SwingConstants.CENTER);
        barLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        barWrapper.add(barLabel, BorderLayout.SOUTH);

        chartsWrapper.add(pieWrapper);
        chartsWrapper.add(Box.createVerticalStrut(20));
        chartsWrapper.add(barWrapper);

        JScrollPane chartsScroll = new JScrollPane(chartsWrapper);
        chartsScroll.setPreferredSize(new Dimension(700, 500));
        chartsScroll.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        chartsScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        chartsScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        contentPanel.add(chartsScroll);
        contentPanel.add(Box.createVerticalStrut(20));

        JButton backBtn = new JButton("\u2190 Back to Dashboard");
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.setBackground(Color.WHITE);
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        backBtn.setFocusPainted(false);
        backBtn.addActionListener(e -> {
            dispose();
            new MainMenuView(user);
        });
        contentPanel.add(backBtn);

        add(contentPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private JLabel makeStyledLabel() {
        JLabel label = new JLabel();
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        return label;
    }

    private void loadReportData() {
        List<Bill> bills = new BillDAO().getAllBills();
        double totalRevenue = bills.stream().mapToDouble(Bill::getTotalAmount).sum();
        revenueLabel.setText("\uD83D\uDCB0 Total Revenue: $" + String.format("%.2f", totalRevenue));

        List<Room> rooms = new RoomDAO().getAllRooms();
        int totalRooms = rooms.size(), occupied = 0, available = 0;
        for (Room r : rooms) {
            switch (r.getStatus().toLowerCase()) {
                case "occupied" -> occupied++;
                case "available" -> available++;
            }
        }
        totalRoomsLabel.setText("\uD83C\uDFE8 Total Rooms: " + totalRooms);
        occupiedLabel.setText("\uD83D\uDEAA Occupied Rooms: " + occupied);
        availableLabel.setText("\u2705 Available Rooms: " + available);

        List<InventoryItem> items = new InventoryDAO().getAllItems();
        Set<String> lowStock = new HashSet<>();
        for (InventoryItem i : items) {
            if (i.getQuantity() < i.getThreshold()) {
                lowStock.add(i.getItemName() + " (" + i.getQuantity() + ")");
            }
        }
        lowStockLabel.setText("\uD83D\uDCE6 Low Stock: " + (lowStock.isEmpty() ? "\u2705 All Good" : String.join(", ", lowStock)));

        usageMap = new HashMap<>();
        revenueMap = new HashMap<>();
        usageTableModel.setRowCount(0);

        for (Bill bill : bills) {
            for (Service s : bill.getServices()) {
                String name = capitalize(s.getServiceName().trim());
                usageMap.put(name, usageMap.getOrDefault(name, 0) + 1);
                revenueMap.put(name, revenueMap.getOrDefault(name, 0.0) + s.getPrice());
            }
        }

        for (String service : usageMap.keySet()) {
            usageTableModel.addRow(new Object[]{
                    service,
                    usageMap.get(service),
                    "$" + String.format("%.2f", revenueMap.get(service))
            });
        }
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return "";
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }

    class PieChartPanel extends JPanel {
        private final Map<String, Integer> data;

        public PieChartPanel(Map<String, Integer> data) {
            this.data = data;
            setPreferredSize(new Dimension(400, 250));
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (data.isEmpty()) return;

            int total = data.values().stream().mapToInt(Integer::intValue).sum();
            int startAngle = 0;
            int x = 60, y = 20, size = 160;

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color[] palette = {Color.BLUE, Color.ORANGE, Color.GREEN, Color.RED, Color.MAGENTA};
            int i = 0;
            int accumulatedAngle = 0;
            int index = 0;

            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                String label = entry.getKey();
                int value = entry.getValue();
                int angle = (int) Math.round(360.0 * value / total);
                if (++index == data.size()) {
                    angle = 360 - accumulatedAngle;
                }

                g2.setColor(palette[i % palette.length]);
                g2.fillArc(x, y, size, size, startAngle, angle);

                double theta = Math.toRadians(startAngle + angle / 2.0);
                int labelX = x + size / 2 + (int) (size / 2.5 * Math.cos(theta));
                int labelY = y + size / 2 - (int) (size / 2.5 * Math.sin(theta));

                g2.setColor(Color.BLACK);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                g2.drawString(label + " (" + value + ")", labelX, labelY);

                startAngle += angle;
                accumulatedAngle += angle;
                i++;
            }
        }
    }

    class BarChartPanel extends JPanel {
        private final Map<String, Double> data;

        public BarChartPanel(Map<String, Double> data) {
            this.data = data;
            setPreferredSize(new Dimension(400, 250));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (data.isEmpty()) return;

            int x = 40;
            int barWidth = 40;
            int chartHeight = 180;
            double maxRevenue = data.values().stream().mapToDouble(Double::doubleValue).max().orElse(1);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));

            int i = 0;
            for (Map.Entry<String, Double> entry : data.entrySet()) {
                double revenue = entry.getValue();
                int barHeight = (int) ((revenue / maxRevenue) * chartHeight);
                int y = chartHeight - barHeight + 20;
                int barX = x + i * (barWidth + 30);

                g2.setColor(new Color(70, 130, 180));
                g2.fillRect(barX, y, barWidth, barHeight);

                g2.setColor(Color.BLACK);
                g2.drawString(entry.getKey(), barX, chartHeight + 35);
                g2.drawString("$" + String.format("%.2f", revenue), barX, y - 5);

                i++;
            }
        }
    }
}
