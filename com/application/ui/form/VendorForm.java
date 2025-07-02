package com.application.ui.form;

import javax.swing.*;

import com.application.inventory.Dao.VendorDAO;
import com.application.inventory.Pojo.Vendor;
import com.application.ui.panel.VendorPanel;

import java.awt.*;
import java.sql.SQLException;

public class VendorForm extends JFrame {
    private JTextField nameField, companyField, emailField, phoneField, cityField;
    private JTextArea addressArea;
    private Vendor vendor;
    private VendorPanel parentPanel;

    public VendorForm(Vendor vendor, VendorPanel parent) {
        this.vendor = vendor;
        this.parentPanel = parent;

        setTitle(vendor == null ? "Add Vendor" : "Edit Vendor");
        setSize(450, 330);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        nameField = new JTextField();
        companyField = new JTextField();
        emailField = new JTextField();
        phoneField = new JTextField();
        cityField = new JTextField();
        addressArea = new JTextArea(3, 20);

        addField(panel, gbc, "Name:", nameField, 0);
        addField(panel, gbc, "Company:", companyField, 1);
        addField(panel, gbc, "Email:", emailField, 2);
        addField(panel, gbc, "Phone:", phoneField, 3);
        addField(panel, gbc, "City:", cityField, 4);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0.3;
        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(addressLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JScrollPane scrollPane = new JScrollPane(addressArea);
        scrollPane.setPreferredSize(new Dimension(200, 60));
        panel.add(scrollPane, gbc);

        JButton saveButton = createStyledButton("Save");
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        panel.add(saveButton, gbc);

        add(panel);

        if (vendor != null) {
            nameField.setText(vendor.getName());
            companyField.setText(vendor.getCompanyName());
            emailField.setText(vendor.getEmail());
            phoneField.setText(vendor.getPhone());
            cityField.setText(vendor.getCity());
            addressArea.setText(vendor.getAddress());
        }

        saveButton.addActionListener(e -> saveVendor());
    }

    private void addField(JPanel panel, GridBagConstraints gbc, String labelText, JTextField field, int y) {
        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.weightx = 0.3;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(field, gbc);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(new Color(30, 144, 255)); // Dodger blue
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void saveVendor() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name is required!");
            return;
        }

        try {
            if (vendor == null) {
                vendor = new Vendor();
            }
            vendor.setName(nameField.getText());
            vendor.setCompanyName(companyField.getText());
            vendor.setEmail(emailField.getText());
            vendor.setPhone(phoneField.getText());
            vendor.setCity(cityField.getText());
            vendor.setAddress(addressArea.getText());

            if (vendor.getVendorId() == 0) {
                VendorDAO.addVendor(vendor);
            } else {
                VendorDAO.updateVendor(vendor);
            }

            parentPanel.refreshVendorTable();
            dispose();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving vendor: " + ex.getMessage());
        }
    }
}
