package com.hotel.views;

import com.hotel.database.ReservationDAO;
import com.hotel.database.RoomDAO;
import com.hotel.models.Reservation;
import com.hotel.models.Room;
import com.hotel.models.User;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReservationForm extends JFrame {
    private JTextField nameField;
    private JDateChooser checkInPicker, checkOutPicker;
    private JComboBox<String> statusBox;
    private JTextArea specialRequestArea, groupGuestArea;
    private JCheckBox groupBookingCheck, lateCheckoutCheck;
    private JSpinner roomCountSpinner;
    private List<JComboBox<String>> roomTypeBoxes = new ArrayList<>();
    private JComboBox<String> roomTypeBox;
    private JPanel dynamicRoomPanel;
    private User currentUser;

    public ReservationForm(User user) {
        this.currentUser = user;
        setTitle("New Reservation");
        setSize(600, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel titleLabel = new JLabel("\uD83D\uDCDD New Reservation");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(titleLabel, gbc);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Guest Name:"), gbc);

        nameField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        groupBookingCheck = new JCheckBox("Group Booking?");
        groupBookingCheck.setBackground(Color.WHITE);
        gbc.gridwidth = 2;
        panel.add(groupBookingCheck, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Group Guest Names:"), gbc);

        groupGuestArea = new JTextArea(3, 20);
        groupGuestArea.setLineWrap(true);
        groupGuestArea.setWrapStyleWord(true);
        JScrollPane groupScroll = new JScrollPane(groupGuestArea);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        panel.add(groupScroll, gbc);
        groupScroll.setVisible(false);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Room Type:"), gbc);

        roomTypeBox = new JComboBox<>(new String[]{"Standard", "Deluxe", "Suite", "Executive"});
        gbc.gridx = 1;
        panel.add(roomTypeBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Number of Rooms:"), gbc);

        roomCountSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        gbc.gridx = 1;
        panel.add(roomCountSpinner, gbc);
        roomCountSpinner.setVisible(false);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        dynamicRoomPanel = new JPanel();
        dynamicRoomPanel.setLayout(new BoxLayout(dynamicRoomPanel, BoxLayout.Y_AXIS));
        panel.add(dynamicRoomPanel, gbc);
        dynamicRoomPanel.setVisible(false);
        gbc.gridwidth = 1;

        groupBookingCheck.addActionListener(e -> {
            boolean isGroup = groupBookingCheck.isSelected();
            nameField.setEnabled(!isGroup);
            roomTypeBox.setEnabled(!isGroup);
            groupScroll.setVisible(isGroup);
            roomCountSpinner.setVisible(isGroup);
            dynamicRoomPanel.setVisible(isGroup);
            refreshRoomTypeFields();
        });

        roomCountSpinner.addChangeListener(e -> refreshRoomTypeFields());

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Check-in Date:"), gbc);

        checkInPicker = new JDateChooser();
        gbc.gridx = 1;
        panel.add(checkInPicker, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Check-out Date:"), gbc);

        checkOutPicker = new JDateChooser();
        gbc.gridx = 1;
        panel.add(checkOutPicker, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Payment Status:"), gbc);

        statusBox = new JComboBox<>(new String[]{"Paid", "Pending", "Cancelled"});
        gbc.gridx = 1;
        panel.add(statusBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Special Requests:"), gbc);

        specialRequestArea = new JTextArea(3, 20);
        specialRequestArea.setLineWrap(true);
        specialRequestArea.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(specialRequestArea);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        panel.add(scroll, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        lateCheckoutCheck = new JCheckBox("Late Checkout Requested?");
        lateCheckoutCheck.setBackground(Color.WHITE);
        panel.add(lateCheckoutCheck, gbc);
        gbc.gridwidth = 1;

        JButton submitBtn = new JButton("➕ Add Reservation");
        submitBtn.addActionListener(this::handleSubmit);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(submitBtn, gbc);

        JButton backButton = new JButton("← Back to Dashboard");
        backButton.addActionListener(e -> {
            dispose();
            new MainMenuView(currentUser);
        });
        gbc.gridy++;
        panel.add(backButton, gbc);

        add(panel);
        setVisible(true);
    }

    private void refreshRoomTypeFields() {
        dynamicRoomPanel.removeAll();
        roomTypeBoxes.clear();
        int count = (int) roomCountSpinner.getValue();
        for (int i = 0; i < count; i++) {
            JComboBox<String> box = new JComboBox<>(new String[]{"Standard", "Deluxe", "Suite", "Executive"});
            dynamicRoomPanel.add(new JLabel("Room " + (i + 1) + " Type:"));
            dynamicRoomPanel.add(box);
            roomTypeBoxes.add(box);
        }
        dynamicRoomPanel.revalidate();
        dynamicRoomPanel.repaint();
    }

    private void handleSubmit(ActionEvent e) {
        try {
            Date checkIn = new Date(checkInPicker.getDate().getTime());
            Date checkOut = new Date(checkOutPicker.getDate().getTime());
            String paymentStatus = (String) statusBox.getSelectedItem();
            String specialRequests = specialRequestArea.getText().trim();
            boolean lateCheckout = lateCheckoutCheck.isSelected();

            RoomDAO roomDAO = new RoomDAO();
            ReservationDAO dao = new ReservationDAO();

            if (groupBookingCheck.isSelected()) {
                String guestNames = groupGuestArea.getText().trim();
                int roomCount = (int) roomCountSpinner.getValue();
                String groupName = "Group-" + UUID.randomUUID().toString().substring(0, 5);

                for (int i = 0; i < roomCount; i++) {
                    String roomType = (String) roomTypeBoxes.get(i).getSelectedItem();
                    Room available = roomDAO.getAvailableRoomByType(roomType);
                    if (available == null) {
                        showPopup("❌ No available " + roomType + " room found.", "Room Error");
                        return;
                    }
                    Reservation res = new Reservation(groupName, checkIn, checkOut, roomType,
                            paymentStatus, specialRequests, available.getRoomNumber(),
                            groupName, lateCheckout);
                    dao.addReservation(res);
                    roomDAO.updateRoomStatusToOccupied(available.getId());
                }

                showPopup("✅ Group reservation added for " + groupName + ".", "Success");
            } else {
                String guestName = nameField.getText().trim();
                if (guestName.isEmpty()) {
                    showPopup("❌ Guest Name is required!", "Input Error");
                    return;
                }
                String roomType = (String) roomTypeBox.getSelectedItem();
                Room availableRoom = roomDAO.getAvailableRoomByType(roomType);
                if (availableRoom != null) {
                    Reservation res = new Reservation(guestName, checkIn, checkOut, roomType,
                            paymentStatus, specialRequests, availableRoom.getRoomNumber(),
                            null, lateCheckout);
                    dao.addReservation(res);
                    roomDAO.updateRoomStatusToOccupied(availableRoom.getId());
                    showPopup("✅ Reservation added successfully!", "Success");
                } else {
                    showPopup("❌ No available room found.", "Room Error");
                }
            }
        } catch (Exception ex) {
            showPopup("⚠️ Error: " + ex.getMessage(), "Exception");
        }
    }

    private void showPopup(String msg, String title) {
        JOptionPane.showMessageDialog(this, msg, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        User dummy = new User(1, "Tanisha", "admin", "Admin");
        new ReservationForm(dummy);
    }
}
