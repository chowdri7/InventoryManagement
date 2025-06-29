package com.subashita.ui.form;

import javax.swing.*;

import com.subashita.inventory.Dao.ProductDAO;
import com.subashita.inventory.Dao.StockOutDAO;
import com.subashita.inventory.Pojo.Product;
import com.subashita.inventory.Pojo.StockOut;
import com.subashita.ui.panel.StockOutPanel;

import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class StockOutForm extends JFrame {
    private JComboBox<String> productBox;
    private JTextField quantityField, dateField, remarksField;
    private List<Product> products;
    private StockOutPanel parentPanel;

    public StockOutForm(StockOutPanel parent) {
        this.parentPanel = parent;
        setTitle("Record Stock-out");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(5, 2));

        add(new JLabel("Product:"));
        productBox = new JComboBox<>();
        add(productBox);

        add(new JLabel("Quantity:"));
        quantityField = new JTextField();
        add(quantityField);

        add(new JLabel("Date (yyyy-mm-dd):"));
        dateField = new JTextField(LocalDate.now().toString());
        add(dateField);

        add(new JLabel("Remarks:"));
        remarksField = new JTextField();
        add(remarksField);

        JButton saveButton = new JButton("Save");
        add(saveButton);

        // Load products
        try {
            products = ProductDAO.getAllProducts();
            for (Product p : products) {
                productBox.addItem(p.getName() + " (ID: " + p.getProductId() + ")");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        saveButton.addActionListener(e -> saveStockOut());
    }

    private void saveStockOut() {
        try {
            int selectedIndex = productBox.getSelectedIndex();
            if (selectedIndex < 0) {
                JOptionPane.showMessageDialog(this, "Select a product!");
                return;
            }

            int quantity = Integer.parseInt(quantityField.getText());
            String date = dateField.getText();
            String remarks = remarksField.getText();
            Product product = products.get(selectedIndex);

            if (quantity <= 0 || quantity > product.getStockQuantity()) {
                JOptionPane.showMessageDialog(this, "Invalid quantity! Available stock: " + product.getStockQuantity());
                return;
            }

            StockOut so = new StockOut();
            so.setProductId(product.getProductId());
            so.setQuantity(quantity);
            so.setDate(date);
            so.setRemarks(remarks);

            StockOutDAO.addStockOut(so);

            // Check reorder alert
            Product updatedProduct = null;
            for (Product p : ProductDAO.getAllProducts()) {
                if (p.getProductId() == product.getProductId()) {
                    updatedProduct = p;
                    break;
                }
            }

            if (updatedProduct != null && updatedProduct.getStockQuantity() <= updatedProduct.getReorderLevel()) {
                JOptionPane.showMessageDialog(this, "Alert: Stock below reorder level!");
            }

            parentPanel.refreshStockOutTable();
            dispose();
        } catch (SQLException | NumberFormatException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}

