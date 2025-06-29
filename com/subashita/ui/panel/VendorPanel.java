package com.subashita.ui.panel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.subashita.inventory.Dao.VendorDAO;
import com.subashita.inventory.Pojo.Vendor;
import com.subashita.ui.form.VendorForm;

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
        topPanel.add(addButton);
        add(topPanel, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Company", "Email", "Phone", "City"}, 0);
        vendorTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(vendorTable);
        add(scrollPane, BorderLayout.CENTER);

        // Button listeners
        addButton.addActionListener(e -> openVendorForm(null));

        // Right-click menu for Edit/Delete
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

    public void refreshVendorTable() {
        try {
            List<Vendor> vendors = VendorDAO.getAllVendors();
            tableModel.setRowCount(0);
            for (Vendor v : vendors) {
                tableModel.addRow(new Object[]{
                    v.getVendorId(),
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
            int id = (int) tableModel.getValueAt(row, 0);
            try {
                List<Vendor> vendors = VendorDAO.getAllVendors();
                for (Vendor v : vendors) {
                    if (v.getVendorId() == id) {
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
            int id = (int) tableModel.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Delete vendor?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    VendorDAO.deleteVendor(id);
                    refreshVendorTable();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}

