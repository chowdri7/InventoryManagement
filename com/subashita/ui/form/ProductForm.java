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

        // Fields
        nameField = new JTextField();
        categoryField = new JTextField();
        priceField = new JTextField();
        skuField = new JTextField();
        reorderField = new JTextField();
        stockField = new JTextField();
        vendorBox = new JComboBox<>();

        // Labels and fields
        addField(panel, gbc, "Name:", nameField, 0);
        addField(panel, gbc, "Category:", categoryField, 1);
        addField(panel, gbc, "Price:", priceField, 2);
        addField(panel, gbc, "SKU:", skuField, 3);
        addField(panel, gbc, "Reorder Level:", reorderField, 4);
        addField(panel, gbc, "Stock Quantity:", stockField, 5);
        addField(panel, gbc, "Vendor:", vendorBox, 6);

        JButton saveButton = createStyledButton("Save");
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        panel.add(saveButton, gbc);

        add(panel);

        // Load vendors
        try {
            vendors = VendorDAO.getAllVendors();
            for (Vendor v : vendors) {
                vendorBox.addItem(v.getName() + " (" + v.getCompanyName() + ")");
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

    private void addField(JPanel panel, GridBagConstraints gbc, String labelText, JComponent field, int y) {
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
