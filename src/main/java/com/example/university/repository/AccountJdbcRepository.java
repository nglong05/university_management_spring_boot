package com.example.university.repository;

import com.example.university.entity.Account;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;

@Repository
public class AccountJdbcRepository {
    private final DataSource dataSource;

    public AccountJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Optional<Account> findByUsername(String username) {
        String sql = """
      SELECT username, password_hash, role, ma_sv, ma_gv, enabled
      FROM users WHERE username = ?
      """;
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                Account a = new Account();
                a.setUsername(rs.getString("username"));
                a.setPasswordHash(rs.getString("password_hash"));
                a.setRole(rs.getString("role"));
                a.setStudentId(rs.getString("ma_sv"));
                a.setTeacherId(rs.getString("ma_gv"));
                a.setEnabled(rs.getBoolean("enabled"));
                return Optional.of(a);
            }
        } catch (SQLException e) {
            throw new RuntimeException("findByUsername failed", e);
        }
    }
    // login logic
    public boolean existsUsername(Connection con, String username) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("SELECT 1 FROM users WHERE username=?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    public int insertStudentAccount(Connection con, String username, String passwordHash, String maSv) throws SQLException {
        String sql = "INSERT INTO users(username, password_hash, role, ma_sv, enabled) VALUES (?,?, 'STUDENT', ?, TRUE)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, passwordHash);
            ps.setString(3, maSv);
            return ps.executeUpdate();
        }
    }

    public int insertLecturerAccount(Connection con, String username, String passwordHash, String maGv) throws SQLException {
        String sql = "INSERT INTO users(username, password_hash, role, ma_gv, enabled) VALUES (?,?, 'LECTURER', ?, TRUE)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, passwordHash);
            ps.setString(3, maGv);
            return ps.executeUpdate();
        }
    }

}
