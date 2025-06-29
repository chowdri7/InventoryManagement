package com.subashita.inventory.Pojo;

public class StockOut {
    private int stockOutId;
    private int productId;
    private int quantity;
    private String date;
    private String remarks;
    private String productName;

    // Getters and setters

    public int getStockOutId() { return stockOutId; }
    public void setStockOutId(int stockOutId) { this.stockOutId = stockOutId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
}

