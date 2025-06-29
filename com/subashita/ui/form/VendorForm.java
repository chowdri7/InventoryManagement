package com.subashita.ui.form;

import javax.swing.*;

import com.subashita.inventory.Dao.VendorDAO;
import com.subashita.inventory.Pojo.Vendor;
import com.subashita.ui.panel.VendorPanel;

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
        setSize(400, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(7, 2));

        formPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Company:"));
        companyField = new JTextField();
        formPanel.add(companyField);

        formPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        formPanel.add(emailField);

        formPanel.add(new JLabel("Phone:"));
        phoneField = new JTextField();
        formPanel.add(phoneField);

        formPanel.add(new JLabel("City:"));
        cityField = new JTextField();
        formPanel.add(cityField);

        formPanel.add(new JLabel("Address:"));
        addressArea = new JTextArea(3, 20);
        formPanel.add(new JScrollPane(addressArea));

        add(formPanel, BorderLayout.CENTER);

        JButton saveButton = new JButton("Save");
        add(saveButton, BorderLayout.SOUTH);

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

