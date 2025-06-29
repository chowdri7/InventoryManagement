package com.subashita.ui.panel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.subashita.inventory.Dao.ProductDAO;
import com.subashita.inventory.Pojo.Product;
import com.subashita.ui.form.ProductForm;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ProductPanel extends JPanel {
    private JTable productTable;
    private DefaultTableModel tableModel;

    public ProductPanel() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton addButton = new JButton("Add Product");
        JButton refreshButton = new JButton("Refresh");

        topPanel.add(addButton);
        topPanel.add(refreshButton);

        add(topPanel, BorderLayout.NORTH);

        refreshButton.addActionListener(e -> refreshProductTable());
        addButton.addActionListener(e -> openProductForm(null));



        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Category", "Price", "SKU", "Stock", "Reorder"}, 0);
        productTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(productTable);
        add(scrollPane, BorderLayout.CENTER);

        addButton.addActionListener(e -> openProductForm(null));

        // Right-click menu
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem editItem = new JMenuItem("Edit");
        JMenuItem deleteItem = new JMenuItem("Delete");
        popupMenu.add(editItem);
        popupMenu.add(deleteItem);

        productTable.setComponentPopupMenu(popupMenu);

        editItem.addActionListener(e -> editSelectedProduct());
        deleteItem.addActionListener(e -> deleteSelectedProduct());

        refreshProductTable();
    }

    public void refreshProductTable() {
        try {
            List<Product> products = ProductDAO.getAllProducts();
            tableModel.setRowCount(0);
            for (Product p : products) {
                tableModel.addRow(new Object[]{
                    p.getProductId(),
                    p.getName(),
                    p.getCategory(),
                    p.getPrice(),
                    p.getSku(),
                    p.getStockQuantity(),
                    p.getReorderLevel()
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void openProductForm(Product product) {
        ProductForm form = new ProductForm(product, this);
        form.setVisible(true);
    }

    private void editSelectedProduct() {
        int row = productTable.getSelectedRow();
        if (row >= 0) {
            int id = (int) tableModel.getValueAt(row, 0);
            try {
                List<Product> products = ProductDAO.getAllProducts();
                for (Product p : products) {
                    if (p.getProductId() == id) {
                        openProductForm(p);
                        break;
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void deleteSelectedProduct() {
        int row = productTable.getSelectedRow();
        if (row >= 0) {
            int id = (int) tableModel.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Delete product?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    ProductDAO.deleteProduct(id);
                    refreshProductTable();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}

