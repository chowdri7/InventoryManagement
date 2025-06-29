package com.subashita.ui.panel;

import com.subashita.inventory.Dao.ReportDAO;
import com.subashita.inventory.Dao.ProductDAO;
import com.subashita.inventory.Pojo.Product;
import com.subashita.ui.MainFrame;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import org.jdatepicker.impl.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class ReportPanel extends JPanel {
    private JTable reportTable;
    private DefaultTableModel tableModel;
    private List<Product> allProducts;
    private Set<Integer> selectedProductIds = new HashSet<>();

    private JRadioButton dailyRadio;
    private JRadioButton monthlyRadio;
    private JDatePickerImpl datePicker;
    private JTextField yearField;
    private JTextField monthField;

    public ReportPanel() {
        setLayout(new BorderLayout());

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterPanel.setBackground(new Color(240, 245, 255));

        dailyRadio = createStyledRadioButton("Daily");
        monthlyRadio = createStyledRadioButton("Monthly");
        ButtonGroup group = new ButtonGroup();
        group.add(dailyRadio);
        group.add(monthlyRadio);
        dailyRadio.setSelected(true);

        // Date picker
        UtilDateModel model = new UtilDateModel();
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());

        yearField = createStyledTextField(4);
        monthField = createStyledTextField(2);

        JButton selectProductButton = MainFrame.createStyledButton("Select Products");
        JButton showButton = MainFrame.createStyledButton("Show Report");

        filterPanel.add(dailyRadio);
        filterPanel.add(monthlyRadio);
        filterPanel.add(new JLabel("Date:"));
        filterPanel.add(datePicker);
        filterPanel.add(new JLabel("Year:"));
        filterPanel.add(yearField);
        filterPanel.add(new JLabel("Month:"));
        filterPanel.add(monthField);
        filterPanel.add(selectProductButton);
        filterPanel.add(showButton);

        add(filterPanel, BorderLayout.NORTH);

        // Table setup
        tableModel = new DefaultTableModel(new String[]{"Date", "Product", "Quantity"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        reportTable = new JTable(tableModel);
        reportTable.setRowHeight(24);
        reportTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        reportTable.setSelectionBackground(new Color(220, 240, 255));

        JTableHeader header = reportTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(30, 144, 255));
        header.setForeground(Color.WHITE);

        reportTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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

        updateInputFields();
        dailyRadio.addActionListener(e -> updateInputFields());
        monthlyRadio.addActionListener(e -> updateInputFields());
    }

    private void updateInputFields() {
        boolean isDaily = dailyRadio.isSelected();
        datePicker.getComponent(0).setEnabled(isDaily);
        datePicker.getComponent(1).setEnabled(isDaily);
        yearField.setEnabled(!isDaily);
        monthField.setEnabled(!isDaily);
    }

    private void showProductSelectionDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Select Products", true);
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel checkboxPanel = new JPanel();
        checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));

        JCheckBox[] checkBoxes = new JCheckBox[allProducts.size()];
        for (int i = 0; i < allProducts.size(); i++) {
            checkBoxes[i] = new JCheckBox(allProducts.get(i).getName());
            checkBoxes[i].setFont(new Font("Segoe UI", Font.PLAIN, 14));
            if (selectedProductIds.contains(allProducts.get(i).getProductId())) {
                checkBoxes[i].setSelected(true);
            }
            checkboxPanel.add(checkBoxes[i]);
        }

        JScrollPane scrollPane = new JScrollPane(checkboxPanel);
        dialog.add(scrollPane, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton selectAllButton = MainFrame.createStyledButton("Select All");
        JButton clearAllButton = MainFrame.createStyledButton("Clear All");
        controlPanel.add(selectAllButton);
        controlPanel.add(clearAllButton);
        dialog.add(controlPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = MainFrame.createStyledButton("OK");
        JButton cancelButton = MainFrame.createStyledButton("Cancel");
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
                Date selectedDate = (Date) datePicker.getModel().getValue();
                if (selectedDate == null) {
                    JOptionPane.showMessageDialog(this, "Please select a date.");
                    return;
                }
                String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(selectedDate);
                data = ReportDAO.getDailyReportByDateWithProducts(dateStr, selectedProductIds);
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

            // Chart
            JDialog chartDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "📊 Report Chart", true);
            chartDialog.setSize(900, 550);
            chartDialog.setLocationRelativeTo(this);
            chartDialog.getContentPane().setBackground(Color.WHITE);

            String chartTitle = dailyRadio.isSelected() ? "Daily Report" : "Monthly Report";
            JFreeChart chart = ChartFactory.createBarChart(chartTitle, "Date", "Quantity", dataset);

            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setBackground(Color.WHITE);
            chartDialog.add(chartPanel);

            chartDialog.setVisible(true);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading report: " + ex.getMessage());
        }
    }

    private JRadioButton createStyledRadioButton(String text) {
        JRadioButton rb = new JRadioButton(text);
        rb.setFont(new Font("Segoe UI", Font.BOLD, 13));
        rb.setBackground(new Color(240, 245, 255));
        return rb;
    }

    private JTextField createStyledTextField(int columns) {
        JTextField tf = new JTextField(columns);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setBorder(new LineBorder(new Color(180, 180, 180)));
        return tf;
    }


    public class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {
        private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        @Override
        public Object stringToValue(String text) throws java.text.ParseException {
            return dateFormatter.parse(text);
        }

        @Override
        public String valueToString(Object value) throws java.text.ParseException {
            if (value != null) {
                Calendar cal = (Calendar) value;
                return dateFormatter.format(cal.getTime());
            }
            return "";
        }
    }
}
