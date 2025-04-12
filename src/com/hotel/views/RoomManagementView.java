package com.hotel.views;

import com.hotel.database.RoomDAO;
import com.hotel.models.Room;
import com.hotel.utils.PopupUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class RoomManagementView extends JFrame {

    private JComboBox<Integer> floorBox, roomBox;
    private JComboBox<String> roomTypeBox, roomStatusBox;
    private DefaultTableModel tableModel;
    private JTable table;

    public RoomManagementView() {
        setTitle("Room Management");
        setSize(1000, 500);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Title Section
        JLabel title = new JLabel("🏨 Room Management", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // Left: Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblFloor = new JLabel("Floor:");
        floorBox = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});

        JLabel lblRoom = new JLabel("Room:");
        roomBox = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10});

        JLabel lblType = new JLabel("Room Type:");
        roomTypeBox = new JComboBox<>(new String[]{"Standard", "Deluxe", "Suite", "Executive"});

        JLabel lblStatus = new JLabel("Room Status:");
        roomStatusBox = new JComboBox<>(new String[]{"Available", "Occupied", "Maintenance", "Cleaning"});

        JButton addButton = new JButton("➕ Add Room");
        addButton.setFocusPainted(false);
        addButton.addActionListener(this::handleAddRoom);

        JButton backBtn = new JButton("← Back");
        backBtn.setFocusPainted(false);
        backBtn.addActionListener(e -> {
            dispose();
            new MainMenuView(LoginView.loggedInUser);
        });

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(lblFloor, gbc);
        gbc.gridx = 1;
        formPanel.add(floorBox, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(lblRoom, gbc);
        gbc.gridx = 1;
        formPanel.add(roomBox, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(lblType, gbc);
        gbc.gridx = 1;
        formPanel.add(roomTypeBox, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(lblStatus, gbc);
        gbc.gridx = 1;
        formPanel.add(roomStatusBox, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        formPanel.add(addButton, gbc);

        gbc.gridy = 5;
        formPanel.add(backBtn, gbc);

        add(formPanel, BorderLayout.WEST);

        // Right: Room Table
        String[] columns = {"ID", "Room Number", "Type", "Status", "Update Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) {
                return col == 4;
            }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        table.getColumn("Update Status").setCellRenderer(new ButtonRenderer());
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (col == 4) {
                    int roomId = (int) tableModel.getValueAt(row, 0);
                    String currentStatus = (String) tableModel.getValueAt(row, 3);
                    String newStatus = (String) JOptionPane.showInputDialog(RoomManagementView.this,
                            "Update Room Status:",
                            "Status Change",
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            new String[]{"Available", "Occupied", "Maintenance", "Cleaning"},
                            currentStatus);
                    if (newStatus != null && !newStatus.equals(currentStatus)) {
                        new RoomDAO().updateRoomStatus(roomId, newStatus);
                        loadRooms();
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(600, 400));
        add(scrollPane, BorderLayout.CENTER);

        loadRooms();
        setVisible(true);
    }

    private void handleAddRoom(ActionEvent e) {
        int floor = (int) floorBox.getSelectedItem();
        int room = (int) roomBox.getSelectedItem();
        String roomNumber = String.valueOf(floor * 100 + room);

        String type = (String) roomTypeBox.getSelectedItem();
        String status = (String) roomStatusBox.getSelectedItem();

        // Check for duplicates
        List<Room> existing = new RoomDAO().getAllRooms();
        for (Room r : existing) {
            if (r.getRoomNumber().equals(roomNumber)) {
                PopupUtil.showError(this, "Room number already exists!");
                return;
            }
        }

        Room newRoom = new Room(roomNumber, type, status);
        boolean added = new RoomDAO().addRoom(newRoom);

        if (added) {
            floorBox.setSelectedIndex(0);
            roomBox.setSelectedIndex(0);
            roomTypeBox.setSelectedIndex(0);
            roomStatusBox.setSelectedIndex(0);
            loadRooms();
            PopupUtil.showSuccess(this, "Room " + roomNumber + " added successfully!");
        } else {
            PopupUtil.showError(this, "Failed to add room.");
        }
    }

    private void loadRooms() {
        tableModel.setRowCount(0);
        List<Room> rooms = new RoomDAO().getAllRooms();
        for (Room r : rooms) {
            tableModel.addRow(new Object[]{
                    r.getId(),
                    r.getRoomNumber(),
                    r.getType(),
                    r.getStatus(),
                    "✏️ Edit"
            });
        }
    }

    static class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
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
