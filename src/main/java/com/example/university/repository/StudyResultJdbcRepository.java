package com.example.university.repository;

import com.example.university.entity.StudyResult;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class StudyResultJdbcRepository {
    private final DataSource dataSource;
    public StudyResultJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private StudyResult map(ResultSet rs) throws SQLException {
        StudyResult r = new StudyResult();
        r.setStudentId(rs.getString("ma_sv"));
        r.setCourseId(rs.getString("ma_mon"));
        r.setSemesterId(rs.getString("ma_ky"));
        r.setProcessScore(rs.getBigDecimal("diem_qt"));
        r.setMidtermScore(rs.getBigDecimal("diem_gk"));
        r.setFinalExamScore(rs.getBigDecimal("diem_ck"));
        r.setFinalScore(rs.getBigDecimal("diem_tong_ket")); // cột sinh ở DB
        return r;
    }

    public boolean exists(String maSv, String maMon, String maKy) {
        String sql = "SELECT 1 FROM ket_qua_hoc_tap WHERE ma_sv=? AND ma_mon=? AND ma_ky=?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maSv); ps.setString(2, maMon); ps.setString(3, maKy);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("exists", e);
        }
    }

    public Optional<StudyResult> findOne(String maSv, String maMon, String maKy) {
        String sql = """
      SELECT ma_sv, ma_mon, ma_ky, diem_qt, diem_gk, diem_ck, diem_tong_ket
      FROM ket_qua_hoc_tap
      WHERE ma_sv=? AND ma_mon=? AND ma_ky=?
      """;
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maSv); ps.setString(2, maMon); ps.setString(3, maKy);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("findOne", e);
        }
    }

    public List<StudyResult> listByStudent(String maSv) {
        String sql = """
      SELECT ma_sv, ma_mon, ma_ky, diem_qt, diem_gk, diem_ck, diem_tong_ket
      FROM ket_qua_hoc_tap
      WHERE ma_sv=?
      ORDER BY ma_ky, ma_mon
      """;
        List<StudyResult> list = new ArrayList<>();
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maSv);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("listByStudent", e);
        }
        return list;
    }

    public int insert(StudyResult r) {
        String sql = """
      INSERT INTO ket_qua_hoc_tap (ma_sv, ma_mon, ma_ky, diem_qt, diem_gk, diem_ck)
      VALUES (?,?,?,?,?,?)
      """;
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, r.getStudentId());
            ps.setString(2, r.getCourseId());
            ps.setString(3, r.getSemesterId());
            ps.setBigDecimal(4, r.getProcessScore());
            ps.setBigDecimal(5, r.getMidtermScore());
            ps.setBigDecimal(6, r.getFinalExamScore());
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("insert", e);
        }
    }

    /** Update từng phần: chỉ cập nhật cột có giá trị khác null */
    public int updatePartial(StudyResult r) {
        StringBuilder sb = new StringBuilder("UPDATE ket_qua_hoc_tap SET ");
        List<Object> args = new ArrayList<>();

        if (r.getProcessScore() != null) { sb.append("diem_qt=?, "); args.add(r.getProcessScore()); }
        if (r.getMidtermScore() != null) { sb.append("diem_gk=?, "); args.add(r.getMidtermScore()); }
        if (r.getFinalExamScore() != null) { sb.append("diem_ck=?, "); args.add(r.getFinalExamScore()); }

        if (args.isEmpty()) return 0; // không có gì để update

        // xoá dấu phẩy cuối
        sb.setLength(sb.length() - 2);
        sb.append(" WHERE ma_sv=? AND ma_mon=? AND ma_ky=?");

        args.add(r.getStudentId());
        args.add(r.getCourseId());
        args.add(r.getSemesterId());

        String sql = sb.toString();
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < args.size(); i++) {
                ps.setObject(i + 1, args.get(i));
            }
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("updatePartial", e);
        }
    }

    public int delete(String maSv, String maMon, String maKy) {
        String sql = "DELETE FROM ket_qua_hoc_tap WHERE ma_sv=? AND ma_mon=? AND ma_ky=?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maSv); ps.setString(2, maMon); ps.setString(3, maKy);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("delete", e);
        }
    }
}
