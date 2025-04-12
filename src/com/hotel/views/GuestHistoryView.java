package com.hotel.views;

import com.hotel.database.ReservationDAO;
import com.hotel.models.Reservation;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.stream.Collectors;

public class GuestHistoryView extends JFrame {

    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField nameField;

    public GuestHistoryView() {
        setTitle("🔎 Guest History");
        setSize(900, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Title
        JLabel title = new JLabel("📖 Guest Reservation History", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        // Top Panel with search
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JLabel nameLabel = new JLabel("Enter Guest Name:");
        nameField = new JTextField(20);
        JButton searchBtn = new JButton("🔍 Search");
        JButton backBtn = new JButton("← Back");

        topPanel.add(nameLabel);
        topPanel.add(nameField);
        topPanel.add(searchBtn);
        topPanel.add(backBtn);

        // Combine title + search into one top container
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(title, BorderLayout.NORTH);
        topContainer.add(topPanel, BorderLayout.SOUTH);

        // Table with non-editable model
        String[] columns = {"ID", "Guest Name", "Check-In", "Check-Out", "Room Type", "Status", "Requests"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false; // Make table fully non-editable
            }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 15, 20, 15));

        // Listeners
        searchBtn.addActionListener(e -> performSearch());
        nameField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                performSearch();
            }
        });

        backBtn.addActionListener(e -> {
            dispose();
            new MainMenuView(LoginView.loggedInUser);
        });

        // Layout
        add(topContainer, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        performSearch(); // Load all by default
        setVisible(true);
    }

    private void performSearch() {
        String keyword = nameField.getText().trim().toLowerCase();
        List<Reservation> all = new ReservationDAO().getAllReservations();

        List<Reservation> filtered = all.stream()
                .filter(r -> r.getGuestName().toLowerCase().contains(keyword))
                .collect(Collectors.toList());

        tableModel.setRowCount(0);
        for (Reservation r : filtered) {
            tableModel.addRow(new Object[]{
                    r.getId(),
                    r.getGuestName(),
                    r.getCheckIn(),
                    r.getCheckOut(),
                    r.getRoomType(),
                    r.getPaymentStatus(),
                    r.getSpecialRequests()
            });
        }
    }

    public static void main(String[] args) {
        new GuestHistoryView();
    }
}
