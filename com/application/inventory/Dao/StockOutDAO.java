package com.application.inventory.Dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.application.inventory.Pojo.StockOut;
import com.application.inventory.Util.DBConnection;

public class StockOutDAO {

    public static void addStockOut(StockOut so) throws SQLException {
        String insertSql = "INSERT INTO stock_out (product_id, quantity, date, remarks) VALUES (?, ?, ?, ?)";
        String updateSql = "UPDATE products SET stock_quantity = stock_quantity - ? WHERE product_id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                 PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

                // Insert stock out
                insertStmt.setInt(1, so.getProductId());
                insertStmt.setInt(2, so.getQuantity());
                insertStmt.setString(3, so.getDate());
                insertStmt.setString(4, so.getRemarks());
                insertStmt.executeUpdate();

                // Update product stock
                updateStmt.setInt(1, so.getQuantity());
                updateStmt.setInt(2, so.getProductId());
                updateStmt.executeUpdate();

                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        }
    }

    public static List<StockOut> getAllStockOuts() throws SQLException {
        List<StockOut> list = new ArrayList<>();
        String sql = "SELECT s.stock_out_id, s.product_id, p.name as product_name, s.quantity, s.date, s.remarks "
                + "FROM stock_out s "
                + "JOIN products p ON s.product_id = p.product_id";
        try (Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                StockOut so = new StockOut();
                so.setStockOutId(rs.getInt("stock_out_id"));
                so.setProductId(rs.getInt("product_id"));
                so.setProductName(rs.getString("product_name")); // <-- you need to add this field to your Pojo if not present
                so.setQuantity(rs.getInt("quantity"));
                so.setDate(rs.getString("date"));
                so.setRemarks(rs.getString("remarks"));
                list.add(so);
            }
        }
        return list;
    }

}

