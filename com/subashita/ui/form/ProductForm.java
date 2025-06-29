package com.subashita.ui.form;

import javax.swing.*;

import com.subashita.inventory.Dao.ProductDAO;
import com.subashita.inventory.Dao.VendorDAO;
import com.subashita.inventory.Pojo.Product;
import com.subashita.inventory.Pojo.Vendor;
import com.subashita.ui.panel.ProductPanel;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ProductForm extends JFrame {
    private JTextField nameField, categoryField, priceField, skuField, reorderField, stockField;
    private JComboBox<String> vendorBox;
    private Product product;
    private ProductPanel parentPanel;
    private List<Vendor> vendors;

    public ProductForm(Product product, ProductPanel parent) {
        this.product = product;
        this.parentPanel = parent;

        setTitle(product == null ? "Add Product" : "Edit Product");
        setSize(400, 450);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(9, 2));

        add(new JLabel("Name:"));
        nameField = new JTextField();
        add(nameField);

        add(new JLabel("Category:"));
        categoryField = new JTextField();
        add(categoryField);

        add(new JLabel("Price:"));
        priceField = new JTextField();
        add(priceField);

        add(new JLabel("SKU:"));
        skuField = new JTextField();
        add(skuField);

        add(new JLabel("Reorder Level:"));
        reorderField = new JTextField();
        add(reorderField);

        add(new JLabel("Stock Quantity:"));
        stockField = new JTextField();
        add(stockField);

        add(new JLabel("Vendor:"));
        vendorBox = new JComboBox<>();
        add(vendorBox);

        JButton saveButton = new JButton("Save");
        add(saveButton);

        // Load vendors
        try {
            vendors = VendorDAO.getAllVendors();
            for (Vendor v : vendors) {
                vendorBox.addItem(v.getName() + " (ID: " + v.getVendorId() + ")");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        if (product != null) {
            nameField.setText(product.getName());
            categoryField.setText(product.getCategory());
            priceField.setText(String.valueOf(product.getPrice()));
            skuField.setText(product.getSku());
            reorderField.setText(String.valueOf(product.getReorderLevel()));
            stockField.setText(String.valueOf(product.getStockQuantity()));
            for (int i = 0; i < vendors.size(); i++) {
                if (vendors.get(i).getVendorId() == product.getVendorId()) {
                    vendorBox.setSelectedIndex(i);
                    break;
                }
            }
        }

        saveButton.addActionListener(e -> saveProduct());
    }

    private void saveProduct() {
        if (nameField.getText().trim().isEmpty() || priceField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and Price are required!");
            return;
        }

        try {
            if (product == null) {
                product = new Product();
            }
            product.setName(nameField.getText());
            product.setCategory(categoryField.getText());
            product.setPrice(Double.parseDouble(priceField.getText()));
            product.setSku(skuField.getText());
            product.setReorderLevel(Integer.parseInt(reorderField.getText()));
            product.setStockQuantity(Integer.parseInt(stockField.getText()));
            product.setVendorId(vendors.get(vendorBox.getSelectedIndex()).getVendorId());

            if (product.getProductId() == 0) {
                ProductDAO.addProduct(product);
            } else {
                ProductDAO.updateProduct(product);
            }

            parentPanel.refreshProductTable();
            dispose();
        } catch (SQLException | NumberFormatException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving product: " + ex.getMessage());
        }
    }
}

