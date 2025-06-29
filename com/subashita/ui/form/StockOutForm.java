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
        setSize(500, 220);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        productBox = new JComboBox<>();
        quantityField = new JTextField();
        dateField = new JTextField(LocalDate.now().toString());
        remarksField = new JTextField();

        // Labels and fields
        addField(panel, gbc, "Product:", productBox, 0);
        addField(panel, gbc, "Quantity:", quantityField, 1);
        addField(panel, gbc, "Date (yyyy-mm-dd):", dateField, 2);
        addField(panel, gbc, "Remarks:", remarksField, 3);

        JButton saveButton = createStyledButton("Save");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        panel.add(saveButton, gbc);

        add(panel);

        // Load products
        try {
            products = ProductDAO.getAllProducts();
            for (Product p : products) {
                productBox.addItem(p.getName() + " (" + p.getCategory() + ")");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        saveButton.addActionListener(e -> saveStockOut());
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
