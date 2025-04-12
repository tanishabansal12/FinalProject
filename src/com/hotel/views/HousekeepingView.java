package com.hotel.views;

import com.hotel.database.HousekeepingDAO;
import com.hotel.database.RoomDAO;
import com.hotel.models.HousekeepingTask;
import com.hotel.models.Room;
import com.hotel.utils.PopupUtil;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class HousekeepingView extends JFrame {

    private JTextField roomNumberField, assignedToField;
    private JComboBox<String> statusBox;
    private DefaultTableModel tableModel;
    private JTable table;

    public HousekeepingView() {
        setTitle("Housekeeping Management");
        setSize(950, 550);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("🏡 Housekeeping Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // Input form
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);

        roomNumberField = new JTextField();
        assignedToField = new JTextField();
        statusBox = new JComboBox<>(new String[]{"Pending", "In Progress", "Completed"});

        formPanel.add(makeField("Room Number:", roomNumberField));
        formPanel.add(makeField("Assigned To:", assignedToField));
        formPanel.add(makeField("Status:", statusBox));
        formPanel.add(Box.createVerticalStrut(15));

        JButton addBtn = new JButton("➕ Add Task");
        addBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        addBtn.setBackground(Color.WHITE);
        addBtn.setForeground(Color.BLACK);
        addBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        addBtn.setFocusPainted(false);
        addBtn.addActionListener(this::handleAddTask);
        formPanel.add(addBtn);
        formPanel.add(Box.createVerticalStrut(15));

        JButton backBtn = new JButton("← Back to Dashboard");
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.setBackground(Color.WHITE);
        backBtn.setForeground(Color.BLACK);
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        backBtn.setFocusPainted(false);
        backBtn.addActionListener(e -> {
            dispose();
            new MainMenuView(LoginView.loggedInUser);
        });
        formPanel.add(backBtn);

        // Table setup
        String[] columns = {"ID", "Room Number", "Assigned To", "Status", "Date Assigned"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) {
                return col == 3;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(new JComboBox<>(new String[]{"Pending", "In Progress", "Completed"})));

        table.getModel().addTableModelListener(e -> {
            int row = e.getFirstRow();
            int col = e.getColumn();
            if (col == 3) {
                int taskId = (int) tableModel.getValueAt(row, 0);
                String newStatus = (String) tableModel.getValueAt(row, col);
                boolean success = new HousekeepingDAO().updateStatus(taskId, newStatus);
                if (success) {
                    PopupUtil.showSuccess(this, "Status updated successfully!");
                } else {
                    PopupUtil.showError(this, "Failed to update status.");
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(formPanel, BorderLayout.WEST);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        loadTasks();
        setVisible(true);
    }

    private void handleAddTask(ActionEvent e) {
        try {
            String roomNumber = roomNumberField.getText().trim();
            String assignedTo = assignedToField.getText().trim();
            String status = (String) statusBox.getSelectedItem();
            Date today = Date.valueOf(LocalDate.now());

            if (roomNumber.isEmpty()) {
                PopupUtil.showError(this, "Room number cannot be empty.");
                return;
            }

            Room room = new RoomDAO().getRoomByNumber(roomNumber);
            if (room == null) {
                PopupUtil.showError(this, "Room number not found in database!");
                return;
            }

            String currentStatus = room.getStatus();
            if (!(currentStatus.equalsIgnoreCase("Available") ||
                    currentStatus.equalsIgnoreCase("Maintenance") ||
                    currentStatus.equalsIgnoreCase("Cleaning"))) {
                PopupUtil.showError(this, "Room is not eligible for housekeeping! Current status: " + currentStatus);
                return;
            }

            if (assignedTo.isEmpty()) {
                PopupUtil.showError(this, "Assigned To cannot be empty.");
                return;
            }

            HousekeepingTask task = new HousekeepingTask(room.getId(), assignedTo, status, today);
            boolean success = new HousekeepingDAO().addTask(task);

            if (success) {
                loadTasks();
                roomNumberField.setText("");
                assignedToField.setText("");
                PopupUtil.showSuccess(this, "Task added successfully!");
            } else {
                PopupUtil.showError(this, "Failed to add task.");
            }
        } catch (Exception ex) {
            PopupUtil.showError(this, "Invalid input: " + ex.getMessage());
        }
    }

    private void loadTasks() {
        tableModel.setRowCount(0);
        List<HousekeepingTask> tasks = new HousekeepingDAO().getAllTasks();
        RoomDAO roomDAO = new RoomDAO();
        for (HousekeepingTask t : tasks) {
            Room room = roomDAO.getRoomById(t.getRoomId());
            String roomNumber = room != null ? room.getRoomNumber() : "N/A";
            tableModel.addRow(new Object[]{
                    t.getId(),
                    roomNumber,
                    t.getAssignedTo(),
                    t.getStatus(),
                    t.getScheduledDate()
            });
        }
    }

    private JPanel makeField(String label, JComponent input) {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBackground(Color.WHITE);
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        p.add(l, BorderLayout.NORTH);
        p.add(input, BorderLayout.CENTER);
        p.setMaximumSize(new Dimension(250, 70));
        return p;
    }
}
