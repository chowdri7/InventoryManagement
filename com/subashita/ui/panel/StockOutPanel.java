package com.subashita.ui.panel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.subashita.inventory.Dao.StockOutDAO;
import com.subashita.inventory.Pojo.StockOut;
import com.subashita.ui.form.StockOutForm;

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
        topPanel.add(addButton);
        add(topPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"ID", "Product ID", "Quantity", "Date", "Remarks"}, 0);
        stockTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(stockTable);
        add(scrollPane, BorderLayout.CENTER);

        addButton.addActionListener(e -> openStockOutForm());

        refreshStockOutTable();
    }

    public void refreshStockOutTable() {
        try {
            List<StockOut> stockOuts = StockOutDAO.getAllStockOuts();
            tableModel.setRowCount(0);
            for (StockOut so : stockOuts) {
                tableModel.addRow(new Object[]{
                    so.getStockOutId(),
                    so.getProductId(),
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

