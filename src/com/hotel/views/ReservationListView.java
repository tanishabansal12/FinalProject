package com.hotel.views;

import com.hotel.database.ReservationDAO;
import com.hotel.database.RoomDAO;
import com.hotel.models.Reservation;
import com.hotel.models.Room;
import com.hotel.utils.PopupUtil;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Date;
import java.util.List;

public class ReservationListView extends JFrame {

    private DefaultTableModel model;
    private JTable table;
    private JTextField searchField;
    private JComboBox<String> filterBox;

    public ReservationListView() {
        setTitle("All Reservations");
        setSize(1000, 550);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("📋 All Reservations");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        searchField = new JTextField(15);
        filterBox = new JComboBox<>(new String[]{"Guest Name", "Payment Status"});
        JButton backBtn = new JButton("← Back");

        topPanel.add(new JLabel("Search:"));
        topPanel.add(searchField);
        topPanel.add(filterBox);
        topPanel.add(backBtn);

        String[] columns = {
                "ID", "Guest Name", "Check-In", "Check-Out",
                "Room Type", "Status", "Requests", "Room Number",
                "Edit", "Delete", "Check-Out"
        };

        model = new DefaultTableModel(columns, 0);
        table = new JTable(model) {
            public boolean isCellEditable(int row, int column) {
                return column >= 8; // Edit/Delete/Check-Out
            }
        };

        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        table.getColumn("Edit").setCellRenderer(new ButtonRenderer());
        table.getColumn("Delete").setCellRenderer(new ButtonRenderer());
        table.getColumn("Check-Out").setCellRenderer(new ButtonRenderer());

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int col = table.columnAtPoint(e.getPoint());
                int row = table.rowAtPoint(e.getPoint());

                switch (col) {
                    case 8 -> editReservation(row);
                    case 9 -> deleteReservation(row);
                    case 10 -> checkOutReservation(row);
                }
            }
        });

        loadTableData();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(title, BorderLayout.NORTH);
        topContainer.add(topPanel, BorderLayout.SOUTH);

        add(topContainer, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                filterTable();
            }
        });

        backBtn.addActionListener(e -> {
            dispose();
            new MainMenuView(LoginView.loggedInUser);
        });

        setVisible(true);
    }

    private void loadTableData() {
        model.setRowCount(0);
        List<Reservation> reservations = new ReservationDAO().getAllReservations();

        for (Reservation r : reservations) {
            model.addRow(new Object[]{
                    r.getId(),
                    r.getGuestName(),
                    r.getCheckIn(),
                    r.getCheckOut(),
                    r.getRoomType(),
                    r.getPaymentStatus(),
                    r.getSpecialRequests(),
                    r.getRoomNumber(),
                    "✏️ Edit",
                    "🗑️ Delete",
                    "✅ Check-Out"
            });
        }
    }

    private void filterTable() {
        String keyword = searchField.getText().toLowerCase();
        int colIndex = filterBox.getSelectedIndex() == 0 ? 1 : 5;

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + keyword, colIndex));
    }

    private void deleteReservation(int row) {
        int id = (int) model.getValueAt(row, 0);
        String roomNumber = (String) model.getValueAt(row, 7);

        // Mark associated room as available
        Room room = new RoomDAO().getRoomByNumber(roomNumber);
        if (room != null) {
            new RoomDAO().updateRoomStatusToAvailable(room.getId());
        }

        // Delete the reservation
        boolean success = new ReservationDAO().deleteReservationById(id);

        if (success) {
            model.removeRow(row);
            PopupUtil.showSuccess(this, "Reservation deleted and room marked available.");
        } else {
            PopupUtil.showError(this, "Failed to delete reservation.");
        }
    }

    private void checkOutReservation(int row) {
        int id = (int) model.getValueAt(row, 0);
        String roomNumber = (String) model.getValueAt(row, 7);

        // Mark room as available
        Room room = new RoomDAO().getRoomByNumber(roomNumber);
        if (room != null) {
            new RoomDAO().updateRoomStatusToAvailable(room.getId());
        }

        // Remove reservation
        boolean deleted = new ReservationDAO().deleteReservationById(id);
        if (deleted) {
            model.removeRow(row);
            PopupUtil.showSuccess(this, "Guest checked out. Room is now available.");
        } else {
            PopupUtil.showError(this, "Check-out failed.");
        }
    }

    private void editReservation(int row) {
        int id = (int) model.getValueAt(row, 0);
        String oldRoomNumber = (String) model.getValueAt(row, 7);
        String name = (String) model.getValueAt(row, 1);
        Date checkIn = Date.valueOf(model.getValueAt(row, 2).toString());
        Date checkOut = Date.valueOf(model.getValueAt(row, 3).toString());
        String oldRoomType = (String) model.getValueAt(row, 4);
        String status = (String) model.getValueAt(row, 5);
        String request = (String) model.getValueAt(row, 6);

        JDialog dialog = new JDialog(this, "Edit Reservation", true);
        dialog.setUndecorated(true);

        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("✏️ Edit Reservation");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JTextField nameField = new JTextField(name);
        JTextField checkInField = new JTextField(checkIn.toString());
        JTextField checkOutField = new JTextField(checkOut.toString());

        JComboBox<String> roomBox = new JComboBox<>(new String[]{"Standard", "Deluxe", "Suite", "Executive"});
        roomBox.setSelectedItem(oldRoomType);

        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Paid", "Pending", "Cancelled"});
        statusBox.setSelectedItem(status);

        JButton saveBtn = new JButton("💾 Save Changes");
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveBtn.setForeground(Color.BLACK);
        saveBtn.setBackground(new Color(230, 230, 230));
        saveBtn.setFocusPainted(false);
        saveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        saveBtn.addActionListener(e -> {
            try {
                String guestName = nameField.getText().trim();
                Date checkInDate = Date.valueOf(checkInField.getText().trim());
                Date checkOutDate = Date.valueOf(checkOutField.getText().trim());

                if (guestName.isEmpty()) {
                    PopupUtil.showError(dialog, "Guest name cannot be empty!");
                    return;
                }
                if (!checkInDate.before(checkOutDate)) {
                    PopupUtil.showError(dialog, "Check-In must be before Check-Out!");
                    return;
                }

                String newRoomType = (String) roomBox.getSelectedItem();
                String newStatus = (String) statusBox.getSelectedItem();
                RoomDAO roomDAO = new RoomDAO();

                String finalRoomNumber = oldRoomNumber;

                // If room type changed, get new room & update availability
                if (!newRoomType.equals(oldRoomType)) {
                    Room newAvailable = roomDAO.getAvailableRoomByType(newRoomType);
                    if (newAvailable == null) {
                        PopupUtil.showError(dialog, "❌ No available " + newRoomType + " room found.");
                        return;
                    }
                    finalRoomNumber = newAvailable.getRoomNumber();

                    // Mark old room available
                    Room oldRoom = roomDAO.getRoomByNumber(oldRoomNumber);
                    if (oldRoom != null) {
                        roomDAO.updateRoomStatusToAvailable(oldRoom.getId());
                    }

                    // Mark new room occupied
                    roomDAO.updateRoomStatusToOccupied(newAvailable.getId());
                }

                Reservation updated = new Reservation(
                        id,
                        guestName,
                        checkInDate,
                        checkOutDate,
                        newRoomType,
                        newStatus,
                        request,
                        finalRoomNumber
                );

                boolean success = new ReservationDAO().updateReservation(updated);
                if (success) {
                    loadTableData();  // Refresh table
                    dialog.dispose();
                    PopupUtil.showSuccess(this, "Reservation updated successfully!");
                } else {
                    PopupUtil.showError(this, "Failed to update reservation.");
                }
            } catch (Exception ex) {
                PopupUtil.showError(this, "Invalid input: " + ex.getMessage());
            }
        });

        panel.add(title);
        panel.add(makeLabeledField("Guest Name:", nameField));
        panel.add(makeLabeledField("Check-In (YYYY-MM-DD):", checkInField));
        panel.add(makeLabeledField("Check-Out (YYYY-MM-DD):", checkOutField));
        panel.add(makeLabeledField("Room Type:", roomBox));
        panel.add(makeLabeledField("Payment Status:", statusBox));
        panel.add(Box.createVerticalStrut(10));
        panel.add(saveBtn);

        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }


    private JPanel makeLabeledField(String label, JComponent input) {
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        p.setLayout(new BorderLayout(5, 5));
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        p.add(l, BorderLayout.NORTH);
        p.add(input, BorderLayout.CENTER);
        p.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
        return p;
    }

    static class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }
}
