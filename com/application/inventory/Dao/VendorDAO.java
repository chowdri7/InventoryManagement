package com.application.inventory.Dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.application.inventory.Pojo.Vendor;
import com.application.inventory.Util.DBConnection;

public class VendorDAO {

    public static void addVendor(Vendor vendor) throws SQLException {
        String sql = "INSERT INTO vendors (name, company_name, email, phone, address, city) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, vendor.getName());
            stmt.setString(2, vendor.getCompanyName());
            stmt.setString(3, vendor.getEmail());
            stmt.setString(4, vendor.getPhone());
            stmt.setString(5, vendor.getAddress());
            stmt.setString(6, vendor.getCity());
            stmt.executeUpdate();
        }
    }

    public static void updateVendor(Vendor vendor) throws SQLException {
        String sql = "UPDATE vendors SET name=?, company_name=?, email=?, phone=?, address=?, city=? WHERE vendor_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, vendor.getName());
            stmt.setString(2, vendor.getCompanyName());
            stmt.setString(3, vendor.getEmail());
            stmt.setString(4, vendor.getPhone());
            stmt.setString(5, vendor.getAddress());
            stmt.setString(6, vendor.getCity());
            stmt.setInt(7, vendor.getVendorId());
            stmt.executeUpdate();
        }
    }

    public static void deleteVendor(int vendorId) throws SQLException {
        String sql = "DELETE FROM vendors WHERE vendor_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, vendorId);
            stmt.executeUpdate();
        }
    }

    public static List<Vendor> getAllVendors() throws SQLException {
        List<Vendor> list = new ArrayList<>();
        String sql = "SELECT * FROM vendors";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Vendor v = new Vendor();
                v.setVendorId(rs.getInt("vendor_id"));
                v.setName(rs.getString("name"));
                v.setCompanyName(rs.getString("company_name"));
                v.setEmail(rs.getString("email"));
                v.setPhone(rs.getString("phone"));
                v.setAddress(rs.getString("address"));
                v.setCity(rs.getString("city"));
                list.add(v);
            }
        }
        return list;
    }
}

