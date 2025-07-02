package com.application.ui.panel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.application.inventory.Dao.StockOutDAO;
import com.application.inventory.Pojo.StockOut;
import com.application.ui.form.StockOutForm;

import javax.swing.table.DefaultTableCellRenderer;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class StockOutPanel extends JPanel {
    private JTable stockTable;
    private DefaultTableModel tableModel;

    public StockOutPanel() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton addButton = new JButton("Record Stock-out");
        styleButton(addButton);

        topPanel.add(addButton);
        add(topPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"Product Name", "Quantity", "Date", "Remarks"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Non-editable
            }
        };

        stockTable = new JTable(tableModel);
        stockTable.setRowHeight(25);
        stockTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JTableHeader header = stockTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(30, 144, 255)); // Dodger blue (same as before)
        header.setForeground(Color.WHITE);

        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        JScrollPane scrollPane = new JScrollPane(stockTable);
        add(scrollPane, BorderLayout.CENTER);

        addButton.addActionListener(e -> openStockOutForm());

        refreshStockOutTable();
    }

    private void styleButton(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(new Color(30, 144, 255)); // Dodger blue
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public void refreshStockOutTable() {
        try {
            List<StockOut> stockOuts = StockOutDAO.getAllStockOuts();
            tableModel.setRowCount(0);
            for (StockOut so : stockOuts) {
                tableModel.addRow(new Object[]{
                    so.getProductName(),
                    so.getQuantity(),
                    so.getDate(),
                    so.getRemarks()
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void openStockOutForm() {
        StockOutForm form = new StockOutForm(this);
        form.setVisible(true);
    }
}
