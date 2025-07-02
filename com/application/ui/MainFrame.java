package com.application.ui;

import javax.swing.*;

import com.application.ui.panel.*;

import java.awt.*;

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
        topPanel.setBackground(new Color(240, 248, 255)); // light background

        JButton vendorButton = createStyledButton("Vendors");
        JButton productButton = createStyledButton("Products");
        JButton stockOutButton = createStyledButton("Stock-out");
        JButton reportButton = createStyledButton("Reports");

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

    public static JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(new Color(30, 144, 255)); // Dodger blue
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
