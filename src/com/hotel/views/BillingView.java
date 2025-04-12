package com.hotel.views;

import com.hotel.database.BillDAO;
import com.hotel.database.ReservationDAO;
import com.hotel.database.ServiceDAO;
import com.hotel.models.Bill;
import com.hotel.models.Reservation;
import com.hotel.models.Service;
import com.hotel.utils.PopupUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;

public class BillingView extends JFrame {
    private JRadioButton individualBtn, groupBtn;
    private JPanel individualPanel, groupPanel;
    private JComboBox<String> guestBox, groupBox;
    private JTextField individualDiscountField, groupDiscountField;
    private JLabel individualTotalLabel, groupTotalLabel, nightsLabel, roomTypeLabel;
    private JList<String> individualServiceList, groupServiceList;
    private DefaultTableModel tableModel;

    public BillingView() {
        setTitle("Billing System");
        setSize(1100, 650);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("\uD83D\uDCB3 Billing System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);

        // Toggle Buttons
        individualBtn = new JRadioButton("Individual", true);
        groupBtn = new JRadioButton("Group");
        ButtonGroup toggleGroup = new ButtonGroup();
        toggleGroup.add(individualBtn);
        toggleGroup.add(groupBtn);

        JPanel togglePanel = new JPanel();
        togglePanel.add(individualBtn);
        togglePanel.add(groupBtn);

        JPanel centerPanel = new JPanel(new CardLayout());
        individualPanel = buildIndividualPanel();
        groupPanel = buildGroupPanel();
        centerPanel.add(individualPanel, "individual");
        centerPanel.add(groupPanel, "group");

        individualBtn.addActionListener(e -> switchPanel(centerPanel, "individual"));
        groupBtn.addActionListener(e -> switchPanel(centerPanel, "group"));

        // Ensure default panel shows
        CardLayout cl = (CardLayout) centerPanel.getLayout();
        cl.show(centerPanel, "individual");

        // Table setup
        String[] cols = {"ID", "Guest/Group", "Room", "Nights", "Services", "Total"};
        tableModel = new DefaultTableModel(cols, 0);
        JTable table = new JTable(tableModel);
        table.setRowHeight(28);
        JScrollPane scrollPane = new JScrollPane(table);

        // Wrap toggle buttons + panels
        JPanel formWrapper = new JPanel(new BorderLayout());
        formWrapper.add(togglePanel, BorderLayout.NORTH);
        formWrapper.add(centerPanel, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, formWrapper, scrollPane);
        splitPane.setResizeWeight(0.35);
        splitPane.setDividerSize(5);
        splitPane.setOneTouchExpandable(true);
        add(splitPane, BorderLayout.CENTER);

        // Back Button
        JButton backButton = new JButton("\u2190 Back to Dashboard");
        backButton.setBackground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> {
            dispose();
            new MainMenuView(LoginView.loggedInUser);
        });
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.PAGE_END);

        loadBills();
        setVisible(true);
    }
    private JPanel buildIndividualPanel() {
        JPanel panel = createBasePanel();

        guestBox = new JComboBox<>(new ReservationDAO().getOnlyIndividualGuestNames().toArray(new String[0]));
        guestBox.addActionListener(e -> fillIndividualData());

        roomTypeLabel = new JLabel();
        nightsLabel = new JLabel();
        individualDiscountField = new JTextField("0");

        List<String> services = new ArrayList<>();
        for (Service s : ServiceDAO.getAllServices()) {
            services.add(s.getServiceName());
        }

        individualServiceList = new JList<>(services.toArray(new String[0]));
        individualServiceList.setVisibleRowCount(4);
        individualServiceList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        panel.add(makeField("Guest Name:", guestBox));
        panel.add(makeField("Room Type:", roomTypeLabel));
        panel.add(makeField("Nights:", nightsLabel));
        panel.add(makeField("Services Used:", new JScrollPane(individualServiceList)));
        panel.add(makeField("Discount (%):", individualDiscountField));

        JButton genBillBtn = new JButton("🧾 Generate Bill");
        genBillBtn.addActionListener(this::handleIndividualBill);
        panel.add(genBillBtn);

        JButton previewBtn = new JButton("🪪 Preview Bill");
        previewBtn.addActionListener(this::previewIndividualBill);
        panel.add(previewBtn);

        individualTotalLabel = new JLabel("Total: $0.00");
        panel.add(individualTotalLabel);

        return panel;
    }

    private JPanel buildGroupPanel() {
        JPanel panel = createBasePanel();

        groupBox = new JComboBox<>(new ReservationDAO().getAllGroupNames().toArray(new String[0]));
        groupDiscountField = new JTextField("0");

        List<String> services = new ArrayList<>();
        for (Service s : ServiceDAO.getAllServices()) {
            services.add(s.getServiceName());
        }

        groupServiceList = new JList<>(services.toArray(new String[0]));
        groupServiceList.setVisibleRowCount(4);
        groupServiceList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        panel.add(makeField("Group Name:", groupBox));
        panel.add(makeField("Group Services:", new JScrollPane(groupServiceList)));
        panel.add(makeField("Group Discount (%):", groupDiscountField));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton payFullBtn = new JButton("💰 Pay Full");
        JButton splitBtn = new JButton("🔀 Split Bill");

        payFullBtn.addActionListener(this::handleGroupBill);
        splitBtn.addActionListener(this::handleSplitBill);

        buttonPanel.add(payFullBtn);
        buttonPanel.add(splitBtn);
        panel.add(buttonPanel);

        JButton previewBtn = new JButton("🪪 Preview Group Bill");
        previewBtn.addActionListener(this::previewGroupBill);
        panel.add(previewBtn);

        groupTotalLabel = new JLabel("Total: $0.00");
        panel.add(groupTotalLabel);

        return panel;
    }

    private void fillIndividualData() {
        String guest = (String) guestBox.getSelectedItem();
        if (guest == null) return;
        Reservation r = new ReservationDAO().getReservationByGuestName(guest);
        if (r != null) {
            roomTypeLabel.setText(r.getRoomType());
            nightsLabel.setText(String.valueOf((r.getCheckOut().getTime() - r.getCheckIn().getTime()) / (1000 * 60 * 60 * 24)));
        }
    }
    private void previewIndividualBill(ActionEvent e) {
        String guest = (String) guestBox.getSelectedItem();
        if (guest == null) return;
        Reservation r = new ReservationDAO().getReservationByGuestName(guest);
        if (r == null) return;

        int nights = (int) ((r.getCheckOut().getTime() - r.getCheckIn().getTime()) / (1000 * 60 * 60 * 24));
        double base = getBaseCharge(r.getRoomType()) * nights;
        boolean isLate = r.isLateCheckout();
        if (isLate) base += 25;

        List<Service> selected = convertSelectedServices(individualServiceList);
        double service = selected.stream().mapToDouble(Service::getPrice).sum();
        double tax = (base + service) * 0.12;
        double discount = (base + service + tax) * (Double.parseDouble(individualDiscountField.getText()) / 100);
        double total = base + service + tax - discount;

        individualTotalLabel.setText("Total: $" + String.format("%.2f", total));

        StringBuilder breakdown = new StringBuilder("<html><b>Billing Breakdown for " + guest + "</b><br><br>");
        breakdown.append("Room Type: ").append(r.getRoomType()).append("<br>");
        breakdown.append("Nights: ").append(nights).append("<br>");
        breakdown.append("Late Checkout: ").append(isLate ? "Yes (+$25)" : "No").append("<br>");
        breakdown.append("Base: $").append(String.format("%.2f", base)).append("<br>");
        breakdown.append("Services: $").append(String.format("%.2f", service)).append("<br>");
        breakdown.append("Tax (12%): $").append(String.format("%.2f", tax)).append("<br>");
        breakdown.append("Discount: $").append(String.format("%.2f", discount)).append("<br><br>");
        breakdown.append("<b>Total: $").append(String.format("%.2f", total)).append("</b></html>");

        JOptionPane.showMessageDialog(this, new JLabel(breakdown.toString()), "Preview Bill", JOptionPane.INFORMATION_MESSAGE);
    }

    private void previewGroupBill(ActionEvent e) {
        String group = (String) groupBox.getSelectedItem();
        if (group == null) return;
        List<Reservation> reservations = new ReservationDAO().getReservationsByGroup(group);

        double baseTotal = 0;
        int totalNights = 0;
        int lateCount = 0;

        for (Reservation r : reservations) {
            int nights = (int) ((r.getCheckOut().getTime() - r.getCheckIn().getTime()) / (1000 * 60 * 60 * 24));
            double base = getBaseCharge(r.getRoomType()) * nights;
            if (r.isLateCheckout()) {
                base += 25;
                lateCount++;
            }
            baseTotal += base;
            totalNights += nights;
        }

        List<Service> services = convertSelectedServices(groupServiceList);
        double service = services.stream().mapToDouble(Service::getPrice).sum();
        double tax = (baseTotal + service) * 0.12;
        double discount = (baseTotal + service + tax) * (Double.parseDouble(groupDiscountField.getText()) / 100);
        double total = baseTotal + service + tax - discount;

        groupTotalLabel.setText("Total: $" + String.format("%.2f", total));

        StringBuilder breakdown = new StringBuilder("<html><b>Group Billing: " + group + "</b><br><br>");
        breakdown.append("Total Nights: ").append(totalNights).append("<br>");
        breakdown.append("Late Checkouts: ").append(lateCount).append(" (+$25 each)<br>");
        breakdown.append("Base: $").append(String.format("%.2f", baseTotal)).append("<br>");
        breakdown.append("Services: $").append(String.format("%.2f", service)).append("<br>");
        breakdown.append("Tax (12%): $").append(String.format("%.2f", tax)).append("<br>");
        breakdown.append("Discount: $").append(String.format("%.2f", discount)).append("<br><br>");
        breakdown.append("<b>Total: $").append(String.format("%.2f", total)).append("</b></html>");

        JOptionPane.showMessageDialog(this, new JLabel(breakdown.toString()), "Preview Group Bill", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleIndividualBill(ActionEvent e) {
        try {
            String guest = (String) guestBox.getSelectedItem();
            if (new BillDAO().isBillAlreadyGenerated(guest)) {
                PopupUtil.showError(this, "Bill already generated for this guest!");
                return;
            }

            Reservation r = new ReservationDAO().getReservationByGuestName(guest);
            int nights = (int) ((r.getCheckOut().getTime() - r.getCheckIn().getTime()) / (1000 * 60 * 60 * 24));
            List<Service> selected = convertSelectedServices(individualServiceList);
            double base = getBaseCharge(r.getRoomType()) * nights;
            if (r.isLateCheckout()) base += 25;

            double service = selected.stream().mapToDouble(Service::getPrice).sum();
            double tax = (base + service) * 0.12;
            double discount = (base + service + tax) * (Double.parseDouble(individualDiscountField.getText()) / 100);
            double total = base + service + tax - discount;

            Bill bill = new Bill(guest, r.getRoomType(), nights, selected, base, tax, discount, total);
            if (new BillDAO().addBill(bill)) {
                PopupUtil.showSuccess(this, "Bill added successfully!");
                loadBills();
            }
        } catch (Exception ex) {
            PopupUtil.showError(this, "Error: " + ex.getMessage());
        }
    }

    private void handleGroupBill(ActionEvent e) {
        try {
            String group = (String) groupBox.getSelectedItem();
            if (new BillDAO().isBillAlreadyGenerated(group)) {
                PopupUtil.showError(this, "Bill already generated for this group!");
                return;
            }

            List<Reservation> reservations = new ReservationDAO().getReservationsByGroup(group);
            List<Service> services = convertSelectedServices(groupServiceList);

            double baseTotal = 0;
            int totalNights = 0;

            for (Reservation r : reservations) {
                int nights = (int) ((r.getCheckOut().getTime() - r.getCheckIn().getTime()) / (1000 * 60 * 60 * 24));
                double base = getBaseCharge(r.getRoomType()) * nights;
                if (r.isLateCheckout()) base += 25;
                baseTotal += base;
                totalNights += nights;
            }

            double service = services.stream().mapToDouble(Service::getPrice).sum();
            double tax = (baseTotal + service) * 0.12;
            double discount = (baseTotal + service + tax) * (Double.parseDouble(groupDiscountField.getText()) / 100);
            double total = baseTotal + service + tax - discount;

            Bill bill = new Bill(group, "Group", totalNights, services, baseTotal, tax, discount, total);
            if (new BillDAO().addBill(bill)) {
                PopupUtil.showSuccess(this, "Group bill added successfully!");
                loadBills();
            }
        } catch (Exception ex) {
            PopupUtil.showError(this, "Error: " + ex.getMessage());
        }
    }

    private void handleSplitBill(ActionEvent e) {
        String group = (String) groupBox.getSelectedItem();
        if (group == null) return;
        List<Reservation> reservations = new ReservationDAO().getReservationsByGroup(group);
        int numGuests = reservations.size();
        if (numGuests == 0) {
            PopupUtil.showError(this, "No guests found in this group.");
            return;
        }

        List<Service> services = convertSelectedServices(groupServiceList);

        double baseTotal = 0;
        for (Reservation r : reservations) {
            int nights = (int) ((r.getCheckOut().getTime() - r.getCheckIn().getTime()) / (1000 * 60 * 60 * 24));
            double base = getBaseCharge(r.getRoomType()) * nights;
            if (r.isLateCheckout()) base += 25;
            baseTotal += base;
        }

        double service = services.stream().mapToDouble(Service::getPrice).sum();
        double tax = (baseTotal + service) * 0.12;
        double discount = (baseTotal + service + tax) * (Double.parseDouble(groupDiscountField.getText()) / 100);
        double total = baseTotal + service + tax - discount;

        double perPerson = total / numGuests;

        StringBuilder breakdown = new StringBuilder("<html><b>Split Bill for Group: " + group + "</b><br><br>");
        breakdown.append("Members: ").append(numGuests).append("<br>");
        breakdown.append("Total Bill: $").append(String.format("%.2f", total)).append("<br>");
        breakdown.append("Each Pays: <b>$").append(String.format("%.2f", perPerson)).append("</b></html>");

        JOptionPane.showMessageDialog(this, new JLabel(breakdown.toString()), "Split Bill", JOptionPane.INFORMATION_MESSAGE);
    }

    private List<Service> convertSelectedServices(JList<String> list) {
        List<Service> selected = new ArrayList<>();
        for (String name : list.getSelectedValuesList()) {
            selected.add(ServiceDAO.getServiceByName(name));
        }
        return selected;
    }

    private double getBaseCharge(String roomType) {
        double multiplier = isPeakSeason() ? 1.2 : 1.0;
        return switch (roomType) {
            case "Deluxe" -> 120 * multiplier;
            case "Suite" -> 180 * multiplier;
            case "Executive" -> 250 * multiplier;
            default -> 80 * multiplier;
        };
    }

    private boolean isPeakSeason() {
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        return month == 7 || month == 8 || month == 12;
    }

    private JPanel createBasePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        panel.setBackground(Color.WHITE);
        return panel;
    }

    private JPanel makeField(String label, JComponent input) {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBackground(Color.WHITE);
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        p.add(l, BorderLayout.NORTH);
        p.add(input, BorderLayout.CENTER);
        p.setMaximumSize(new Dimension(300, 70));
        return p;
    }

    private void switchPanel(JPanel parent, String name) {
        ((CardLayout) parent.getLayout()).show(parent, name);
    }

    private void loadBills() {
        tableModel.setRowCount(0);
        for (Bill b : new BillDAO().getAllBills()) {
            StringBuilder sb = new StringBuilder();
            for (Service s : b.getServices()) sb.append(s.getServiceName()).append(", ");
            if (!b.getServices().isEmpty()) sb.setLength(sb.length() - 2);

            tableModel.addRow(new Object[]{
                    b.getId(), b.getGuestName(), b.getRoomType(), b.getNights(), sb.toString(),
                    "$" + String.format("%.2f", b.getTotalAmount())
            });
        }
    }
}
