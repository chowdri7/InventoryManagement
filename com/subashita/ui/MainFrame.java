package com.subashita.ui;

import javax.swing.*;
import java.awt.*;

import com.subashita.ui.panel.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel cardPanel;

    public MainFrame() {
        setTitle("Retail Inventory Tracker");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        VendorPanel vendorPanel = new VendorPanel();
        ProductPanel productPanel = new ProductPanel();
        StockOutPanel stockOutPanel = new StockOutPanel();
        ReportPanel reportPanel = new ReportPanel();

        cardPanel.add(vendorPanel, "Vendor");
        cardPanel.add(productPanel, "Product");
        cardPanel.add(stockOutPanel, "StockOut");
        cardPanel.add(reportPanel, "Report");

        add(cardPanel, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton vendorButton = new JButton("Vendors");
        JButton productButton = new JButton("Products");
        JButton stockOutButton = new JButton("Stock-out");
        JButton reportButton = new JButton("Reports");

        topPanel.add(vendorButton);
        topPanel.add(productButton);
        topPanel.add(stockOutButton);
        topPanel.add(reportButton);

        add(topPanel, BorderLayout.NORTH);

        vendorButton.addActionListener(e -> cardLayout.show(cardPanel, "Vendor"));
        productButton.addActionListener(e -> cardLayout.show(cardPanel, "Product"));
        stockOutButton.addActionListener(e -> cardLayout.show(cardPanel, "StockOut"));
        reportButton.addActionListener(e -> cardLayout.show(cardPanel, "Report"));

        cardLayout.show(cardPanel, "Vendor");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
