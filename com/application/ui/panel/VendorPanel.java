package com.application.ui.panel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.application.inventory.Dao.VendorDAO;
import com.application.inventory.Pojo.Vendor;
import com.application.ui.form.VendorForm;

import javax.swing.table.DefaultTableCellRenderer;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class VendorPanel extends JPanel {
    private JTable vendorTable;
    private DefaultTableModel tableModel;

    public VendorPanel() {
        setLayout(new BorderLayout());

        // Top panel with Add button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("Add Vendor");
        styleButton(addButton);

        topPanel.add(addButton);
        add(topPanel, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(new String[]{"Name", "Company", "Email", "Phone", "City"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Non-editable
            }
        };
        vendorTable = new JTable(tableModel);
        vendorTable.setRowHeight(25);
        vendorTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JTableHeader header = vendorTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(30, 144, 255)); // Dodger blue
        header.setForeground(Color.WHITE);

        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        JScrollPane scrollPane = new JScrollPane(vendorTable);
        add(scrollPane, BorderLayout.CENTER);

        // Button listener
        addButton.addActionListener(e -> openVendorForm(null));

        // Right-click menu
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem editItem = new JMenuItem("Edit");
        JMenuItem deleteItem = new JMenuItem("Delete");
        popupMenu.add(editItem);
        popupMenu.add(deleteItem);

        vendorTable.setComponentPopupMenu(popupMenu);

        editItem.addActionListener(e -> editSelectedVendor());
        deleteItem.addActionListener(e -> deleteSelectedVendor());

        refreshVendorTable();
    }

    private void styleButton(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(new Color(30, 144, 255)); // Dodger blue
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public void refreshVendorTable() {
        try {
            List<Vendor> vendors = VendorDAO.getAllVendors();
            tableModel.setRowCount(0);
            for (Vendor v : vendors) {
                tableModel.addRow(new Object[]{
                    v.getName(),
                    v.getCompanyName(),
                    v.getEmail(),
                    v.getPhone(),
                    v.getCity()
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading vendors: " + ex.getMessage());
        }
    }

    private void openVendorForm(Vendor vendor) {
        VendorForm form = new VendorForm(vendor, this);
        form.setVisible(true);
    }

    private void editSelectedVendor() {
        int row = vendorTable.getSelectedRow();
        if (row >= 0) {
            String name = (String) tableModel.getValueAt(row, 0);
            try {
                List<Vendor> vendors = VendorDAO.getAllVendors();
                for (Vendor v : vendors) {
                    if (v.getName().equals(name)) {
                        openVendorForm(v);
                        break;
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void deleteSelectedVendor() {
        int row = vendorTable.getSelectedRow();
        if (row >= 0) {
            String name = (String) tableModel.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Delete vendor?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    List<Vendor> vendors = VendorDAO.getAllVendors();
                    for (Vendor v : vendors) {
                        if (v.getName().equals(name)) {
                            VendorDAO.deleteVendor(v.getVendorId());
                            refreshVendorTable();
                            break;
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
