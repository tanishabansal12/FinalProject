package com.hotel.patterns;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class InventoryAlertService implements InventoryObserver {

    private Component parent;

    public InventoryAlertService(Component parent) {
        this.parent = parent;
    }

    @Override
    public void update(List<String> lowStockItems) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);

        JLabel icon = new JLabel(UIManager.getIcon("OptionPane.warningIcon"));
        panel.add(icon, BorderLayout.WEST);

        StringBuilder message = new StringBuilder("<html><div style='font-family:Segoe UI;font-size:13px;'>");
        message.append("<b>Low Stock Items:</b><ul>");
        for (String s : lowStockItems) {
            message.append("<li>").append(s).append("</li>");
        }
        message.append("</ul></div></html>");

        JLabel label = new JLabel(message.toString());
        panel.add(label, BorderLayout.CENTER);

        JButton okButton = new JButton("OK");
        okButton.setBackground(Color.WHITE);
        okButton.setForeground(Color.BLACK);
        okButton.setFocusPainted(false);
        okButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        okButton.addActionListener(e -> SwingUtilities.getWindowAncestor(okButton).dispose());

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(okButton);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), "Inventory Alert", true);
        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }
}
