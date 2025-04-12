package com.hotel.views;

import com.hotel.database.UserDAO;
import com.hotel.models.User;
import com.hotel.roles.UserRole;
import com.hotel.roles.UserRoleFactory;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;

    // Stores currently logged-in user
    public static User loggedInUser;

    public LoginView() {
        setTitle("Hotel Login");
        setSize(700, 450);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(new Color(245, 245, 245));
        setLayout(null);

        // Center panel
        JPanel loginPanel = new JPanel();
        loginPanel.setBounds(210, 90, 280, 290);
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setBackground(new Color(255, 255, 255, 230));
        loginPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        JLabel title = new JLabel("Horizon Hotel Login", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(33, 33, 33));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        usernameField = new JTextField();
        usernameField.setMaximumSize(new Dimension(240, 40));
        usernameField.setBorder(BorderFactory.createTitledBorder("Username"));

        passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(240, 40));
        passwordField.setBorder(BorderFactory.createTitledBorder("Password"));

        // Clean login button without icon
        JButton loginButton = new JButton("Login");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setBackground(Color.WHITE);
        loginButton.setForeground(new Color(70, 130, 180));
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createLineBorder(new Color(70, 130, 180), 2, true));
        loginButton.setPreferredSize(new Dimension(240, 40));
        loginButton.setMaximumSize(new Dimension(240, 40));
        loginButton.addActionListener(e -> handleLogin());

        loginPanel.add(title);
        loginPanel.add(Box.createVerticalStrut(15));
        loginPanel.add(usernameField);
        loginPanel.add(Box.createVerticalStrut(10));
        loginPanel.add(passwordField);
        loginPanel.add(Box.createVerticalStrut(20));
        loginPanel.add(loginButton);

        add(loginPanel);
        setVisible(true);
    }

    // Validates user credentials and launches dashboard
    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        loggedInUser = new UserDAO().login(username, password);
        User user = loggedInUser;

        if (user != null) {
            showSuccessPopup(user);
            dispose();
            UserRole role = UserRoleFactory.getRole(user);
            if (role != null) role.launchDashboard();
        } else {
            UIManager.put("OptionPane.messageFont", new Font("Segoe UI", Font.PLAIN, 15));
            UIManager.put("OptionPane.buttonFont", new Font("Segoe UI", Font.PLAIN, 13));
            showLoginFailedPopup();
        }
    }

    // Error popup
    private void showLoginFailedPopup() {
        JDialog dialog = new JDialog(this, "Login Failed", true);
        dialog.setUndecorated(true);
        dialog.setSize(380, 160);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel message = new JLabel("❌ Invalid username or password.");
        message.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        message.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton okButton = new JButton("OK");
        okButton.setBackground(Color.WHITE);
        okButton.setForeground(new Color(70, 130, 180));
        okButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        okButton.setFocusPainted(false);
        okButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        okButton.setBorder(BorderFactory.createLineBorder(new Color(70, 130, 180), 2, true));
        okButton.setMaximumSize(new Dimension(100, 35));
        okButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        okButton.addActionListener(e -> dialog.dispose());

        panel.add(message);
        panel.add(Box.createVerticalStrut(20));
        panel.add(okButton);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    // Success popup
    private void showSuccessPopup(User user) {
        JDialog dialog = new JDialog(this, "Login Successful", true);
        dialog.setUndecorated(true);
        dialog.setSize(400, 180);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel message = new JLabel("<html><div style='text-align:center;'>"
                + "✅ Welcome back, <b>" + user.getUsername() + "</b>!<br>"
                + "You're logged in as: <b>" + user.getRole() + "</b></div></html>");
        message.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        message.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton continueBtn = new JButton("➡ Continue to Dashboard");
        continueBtn.setBackground(Color.WHITE);
        continueBtn.setForeground(new Color(70, 130, 180));
        continueBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        continueBtn.setFocusPainted(false);
        continueBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        continueBtn.setBorder(BorderFactory.createLineBorder(new Color(70, 130, 180), 2, true));
        continueBtn.setMaximumSize(new Dimension(240, 40));
        continueBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        continueBtn.addActionListener(e -> dialog.dispose());

        panel.add(message);
        panel.add(Box.createVerticalStrut(20));
        panel.add(continueBtn);

        dialog.getContentPane().add(panel);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        new LoginView();
    }
}
