package com.example.university.repository;

import org.springframework.stereotype.Repository;

import java.sql.*;

@Repository
public class DepartmentJdbcRepository {

    public boolean existsById(Connection con, String maKhoa) throws SQLException {
        String sql = "SELECT 1 FROM khoa WHERE ma_khoa = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maKhoa);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
