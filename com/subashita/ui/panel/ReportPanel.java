package com.subashita.ui.panel;

import com.subashita.inventory.Dao.ReportDAO;
import com.subashita.inventory.Dao.ProductDAO;
import com.subashita.inventory.Pojo.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.data.category.DefaultCategoryDataset;

public class ReportPanel extends JPanel {
    private JTable reportTable;
    private DefaultTableModel tableModel;
    private List<Product> allProducts;
    private Set<Integer> selectedProductIds = new HashSet<>();

    private JRadioButton dailyRadio;
    private JRadioButton monthlyRadio;
    private JTextField dateField;
    private JTextField yearField;
    private JTextField monthField;

    public ReportPanel() {
        setLayout(new BorderLayout());

        // Top panel for filters
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        dailyRadio = new JRadioButton("Daily");
        monthlyRadio = new JRadioButton("Monthly");
        ButtonGroup group = new ButtonGroup();
        group.add(dailyRadio);
        group.add(monthlyRadio);
        dailyRadio.setSelected(true);

        filterPanel.add(dailyRadio);
        filterPanel.add(monthlyRadio);

        dateField = new JTextField(8);
        yearField = new JTextField(4);
        monthField = new JTextField(2);

        filterPanel.add(new JLabel("Date (yyyy-mm-dd):"));
        filterPanel.add(dateField);
        filterPanel.add(new JLabel("Year:"));
        filterPanel.add(yearField);
        filterPanel.add(new JLabel("Month:"));
        filterPanel.add(monthField);

        JButton selectProductButton = new JButton("Select Products");
        filterPanel.add(selectProductButton);

        JButton showButton = new JButton("Show Report");
        filterPanel.add(showButton);

        add(filterPanel, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(new String[]{"Date", "Product", "Quantity"}, 0);
        reportTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(reportTable);
        add(scrollPane, BorderLayout.CENTER);

        // Load products
        try {
            allProducts = ProductDAO.getAllProducts();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        selectProductButton.addActionListener(e -> showProductSelectionDialog());

        showButton.addActionListener(this::loadReport);

        // Disable fields initially
        updateInputFields();

        dailyRadio.addActionListener(e -> updateInputFields());
        monthlyRadio.addActionListener(e -> updateInputFields());
    }

    private void updateInputFields() {
        dateField.setEnabled(dailyRadio.isSelected());
        yearField.setEnabled(monthlyRadio.isSelected());
        monthField.setEnabled(monthlyRadio.isSelected());
    }

    private void showProductSelectionDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Select Products", true);
        dialog.setSize(350, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel checkboxPanel = new JPanel();
        checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));

        JCheckBox[] checkBoxes = new JCheckBox[allProducts.size()];

        for (int i = 0; i < allProducts.size(); i++) {
            checkBoxes[i] = new JCheckBox(allProducts.get(i).getName());
            if (selectedProductIds.contains(allProducts.get(i).getProductId())) {
                checkBoxes[i].setSelected(true);
            }
            checkboxPanel.add(checkBoxes[i]);
        }

        JScrollPane scrollPane = new JScrollPane(checkboxPanel);
        dialog.add(scrollPane, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton selectAllButton = new JButton("Select All");
        JButton clearAllButton = new JButton("Clear All");
        controlPanel.add(selectAllButton);
        controlPanel.add(clearAllButton);
        dialog.add(controlPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        selectAllButton.addActionListener(e -> Arrays.stream(checkBoxes).forEach(cb -> cb.setSelected(true)));
        clearAllButton.addActionListener(e -> Arrays.stream(checkBoxes).forEach(cb -> cb.setSelected(false)));

        okButton.addActionListener(e -> {
            selectedProductIds.clear();
            for (int i = 0; i < checkBoxes.length; i++) {
                if (checkBoxes[i].isSelected()) {
                    selectedProductIds.add(allProducts.get(i).getProductId());
                }
            }
            dialog.dispose();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void loadReport(ActionEvent e) {
        tableModel.setRowCount(0);

        try {
            List<Object[]> data;

            if (dailyRadio.isSelected()) {
                String date = dateField.getText();
                if (date.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter a date (yyyy-mm-dd).");
                    return;
                }
                data = ReportDAO.getDailyReportByDateWithProducts(date, selectedProductIds);
            } else {
                int year = Integer.parseInt(yearField.getText());
                int month = Integer.parseInt(monthField.getText());
                data = ReportDAO.getMonthlyReportByMonthWithProducts(year, month, selectedProductIds);
            }

            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            for (Object[] row : data) {
                String date = (String) row[0];
                String productName = (String) row[1];
                Integer qty = (Integer) row[2];

                tableModel.addRow(new Object[]{date, productName, qty});
                dataset.addValue(qty, productName, date);
            }

            JDialog chartDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Report Chart", true);
            chartDialog.setSize(800, 500);
            chartDialog.setLocationRelativeTo(this);

            String chartTitle = dailyRadio.isSelected() ? "Daily Report" : "Monthly Report";
            var chart = ChartFactory.createBarChart(chartTitle, "Date", "Quantity", dataset);
            chartDialog.add(new ChartPanel(chart));
            chartDialog.setVisible(true);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading report: " + ex.getMessage());
        }
    }
}
