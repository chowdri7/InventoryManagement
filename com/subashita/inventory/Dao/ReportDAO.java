package com.subashita.inventory.Dao;

import com.subashita.inventory.Pojo.Product;
import com.subashita.inventory.Util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ReportDAO {

    public static List<Product> getAllProducts() throws SQLException {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT product_id, name FROM products";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Product p = new Product();
                p.setProductId(rs.getInt("product_id"));
                p.setName(rs.getString("name"));
                list.add(p);
            }
        }
        return list;
    }

    public static List<Object[]> getDailyReportByDate(String date, List<Integer> productIds) throws SQLException {
        List<Object[]> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT date, p.name, SUM(s.quantity) AS total_qty " +
                "FROM stock_out s JOIN products p ON s.product_id = p.product_id " +
                "WHERE s.date = ?");

        if (productIds != null && !productIds.isEmpty()) {
            sql.append(" AND s.product_id IN (")
                    .append("?,".repeat(productIds.size() - 1))
                    .append("?)");
        }

        sql.append(" GROUP BY s.date, p.name");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            ps.setString(1, date);
            for (int i = 0; i < productIds.size(); i++) {
                ps.setInt(i + 2, productIds.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                            rs.getString("date"),
                            rs.getString("name"),
                            rs.getInt("total_qty")
                    });
                }
            }
        }
        return list;
    }

    public static List<Object[]> getMonthlyReportByMonth(int year, int month, List<Integer> productIds) throws SQLException {
        List<Object[]> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT s.date, p.name, SUM(s.quantity) AS total_qty " +
                "FROM stock_out s JOIN products p ON s.product_id = p.product_id " +
                "WHERE YEAR(s.date) = ? AND MONTH(s.date) = ?");

        if (productIds != null && !productIds.isEmpty()) {
            sql.append(" AND s.product_id IN (")
                    .append("?,".repeat(productIds.size() - 1))
                    .append("?)");
        }

        sql.append(" GROUP BY s.date, p.name");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            ps.setInt(1, year);
            ps.setInt(2, month);
            for (int i = 0; i < productIds.size(); i++) {
                ps.setInt(i + 3, productIds.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                            rs.getString("date"),
                            rs.getString("name"),
                            rs.getInt("total_qty")
                    });
                }
            }
        }
        return list;
    }
    public static List<Object[]> getDailyReportByDateWithProducts(String date, Set<Integer> productIds) throws SQLException {
    List<Object[]> list = new ArrayList<>();
    Connection conn = DBConnection.getConnection();

    String sql = "SELECT s.date, p.name as product_name, SUM(s.quantity) as total_qty " +
                 "FROM stock_out s JOIN products p ON s.product_id = p.product_id " +
                 "WHERE s.date = ? " +
                 (productIds.isEmpty() ? "" : "AND s.product_id IN (" + String.join(",", Collections.nCopies(productIds.size(), "?")) + ") ") +
                 "GROUP BY s.date, p.name";

    PreparedStatement ps = conn.prepareStatement(sql);
    ps.setString(1, date);
    int index = 2;
    for (Integer pid : productIds) {
        ps.setInt(index++, pid);
    }

    ResultSet rs = ps.executeQuery();
    while (rs.next()) {
        list.add(new Object[]{
            rs.getString("date"),
            rs.getString("product_name"),
            rs.getInt("total_qty")
        });
    }
    conn.close();
    return list;
}

public static List<Object[]> getMonthlyReportByMonthWithProducts(int year, int month, Set<Integer> productIds) throws SQLException {
    List<Object[]> list = new ArrayList<>();
    Connection conn = DBConnection.getConnection();

    String sql = "SELECT s.date, p.name as product_name, SUM(s.quantity) as total_qty " +
                 "FROM stock_out s JOIN products p ON s.product_id = p.product_id " +
                 "WHERE YEAR(s.date) = ? AND MONTH(s.date) = ? " +
                 (productIds.isEmpty() ? "" : "AND s.product_id IN (" + String.join(",", Collections.nCopies(productIds.size(), "?")) + ") ") +
                 "GROUP BY s.date, p.name";

    PreparedStatement ps = conn.prepareStatement(sql);
    ps.setInt(1, year);
    ps.setInt(2, month);
    int index = 3;
    for (Integer pid : productIds) {
        ps.setInt(index++, pid);
    }

    ResultSet rs = ps.executeQuery();
    while (rs.next()) {
        list.add(new Object[]{
            rs.getString("date"),
            rs.getString("product_name"),
            rs.getInt("total_qty")
        });
    }
    conn.close();
    return list;
}

}
