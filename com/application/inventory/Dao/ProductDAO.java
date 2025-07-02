package com.application.inventory.Dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.application.inventory.Pojo.Product;
import com.application.inventory.Util.DBConnection;

public class ProductDAO {

    public static void addProduct(Product p) throws SQLException {
        String sql = "INSERT INTO products (name, category, price, sku, vendor_id, reorder_level, stock_quantity) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, p.getName());
            stmt.setString(2, p.getCategory());
            stmt.setDouble(3, p.getPrice());
            stmt.setString(4, p.getSku());
            stmt.setInt(5, p.getVendorId());
            stmt.setInt(6, p.getReorderLevel());
            stmt.setInt(7, p.getStockQuantity());
            stmt.executeUpdate();
        }
    }

    public static void updateProduct(Product p) throws SQLException {
        String sql = "UPDATE products SET name=?, category=?, price=?, sku=?, vendor_id=?, reorder_level=?, stock_quantity=? WHERE product_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, p.getName());
            stmt.setString(2, p.getCategory());
            stmt.setDouble(3, p.getPrice());
            stmt.setString(4, p.getSku());
            stmt.setInt(5, p.getVendorId());
            stmt.setInt(6, p.getReorderLevel());
            stmt.setInt(7, p.getStockQuantity());
            stmt.setInt(8, p.getProductId());
            stmt.executeUpdate();
        }
    }

    public static void deleteProduct(int productId) throws SQLException {
        String sql = "DELETE FROM products WHERE product_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            stmt.executeUpdate();
        }
    }

    public static List<Product> getAllProducts() throws SQLException {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Product p = new Product();
                p.setProductId(rs.getInt("product_id"));
                p.setName(rs.getString("name"));
                p.setCategory(rs.getString("category"));
                p.setPrice(rs.getDouble("price"));
                p.setSku(rs.getString("sku"));
                p.setVendorId(rs.getInt("vendor_id"));
                p.setReorderLevel(rs.getInt("reorder_level"));
                p.setStockQuantity(rs.getInt("stock_quantity"));
                list.add(p);
            }
        }
        return list;
    }
}

