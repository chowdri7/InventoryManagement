package com.application.ui.panel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.application.inventory.Dao.ProductDAO;
import com.application.inventory.Pojo.Product;
import com.application.ui.MainFrame;
import com.application.ui.form.ProductForm;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ProductPanel extends JPanel {
    private JTable productTable;
    private DefaultTableModel tableModel;

    public ProductPanel() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton addButton = MainFrame.createStyledButton("Add Product");
        JButton refreshButton = MainFrame.createStyledButton("Refresh");

        topPanel.add(addButton);
        topPanel.add(refreshButton);

        add(topPanel, BorderLayout.NORTH);

        // Define model without ID column
        tableModel = new DefaultTableModel(new String[]{"Name", "Category", "Price", "SKU", "Stock", "Reorder"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Disable editing
            }
        };

        productTable = new JTable(tableModel);
        productTable.setRowHeight(24);
        productTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        productTable.setSelectionBackground(new Color(220, 240, 255));

        // Header styling
        JTableHeader header = productTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(30, 144, 255));
        header.setForeground(Color.WHITE);

        // Alternate row colors
        productTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            private final Color evenColor = new Color(245, 245, 245);

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? evenColor : Color.WHITE);
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(productTable);
        add(scrollPane, BorderLayout.CENTER);

        refreshButton.addActionListener(e -> refreshProductTable());
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
            String sku = (String) tableModel.getValueAt(row, 3); // SKU column

            try {
                List<Product> products = ProductDAO.getAllProducts();
                for (Product p : products) {
                    if (p.getSku().equals(sku)) {
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
            String sku = (String) tableModel.getValueAt(row, 3);
            int confirm = JOptionPane.showConfirmDialog(this, "Delete product?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    List<Product> products = ProductDAO.getAllProducts();
                    for (Product p : products) {
                        if (p.getSku().equals(sku)) {
                            ProductDAO.deleteProduct(p.getProductId());
                            refreshProductTable();
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
