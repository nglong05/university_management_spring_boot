package com.example.university.repository;

import com.example.university.dto.UpdateGradeRequest;
import com.example.university.dto.TranscriptItemDTO;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.*;
import java.sql.*;

@Repository
public class LecturerJdbcRepository {
    private DataSource dataSource;
    public LecturerJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // validate if one can grade this student
    public boolean canGrade(String id, String courseId, String semesterId) {
        String sql = """
                SELECT 1 FROM giangvien_monhockyhoc
                WHERE ma_gv = ? AND ma_mon = ? AND ma_ky = ?
                """;
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, id);
            ps.setString(2, courseId);
            ps.setString(3, semesterId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("canGrade(" + id + "," + courseId + "," + semesterId + ")", e);
        }
    }


    // update or insert a student's grade
    public int upsertGrade(UpdateGradeRequest req) {
        String sql = """
                    INSERT INTO ket_qua_hoc_tap (ma_sv, ma_mon, ma_ky, diem_qt, diem_gk, diem_ck)
                    VALUES (?,?,?,?,?,?)
                    ON DUPLICATE KEY UPDATE
                      diem_qt = COALESCE(VALUES(diem_qt), diem_qt),
                      diem_gk = COALESCE(VALUES(diem_gk), diem_gk),
                      diem_ck = COALESCE(VALUES(diem_ck), diem_ck)
                """;
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
        ) {
            ps.setString(1, req.getMaSv());
            ps.setString(2, req.getMaMon());
            ps.setString(3, req.getMaKy());
            ps.setBigDecimal(4, req.getDiemQt());
            ps.setBigDecimal(5, req.getDiemGk());
            ps.setBigDecimal(6, req.getDiemCk());
            return ps.executeUpdate(); // return 1 (insert) or 2 (update)
        } catch (SQLException e) {
            throw new RuntimeException("upsertGrade(" + req + ")", e);
        }
    }

        // xem bang diem
        public List<TranscriptItemDTO> classTranscript(String courseId, String semesterId) {
            String sql = """
              SELECT ma_sv, ma_mon, ten_mon, so_tin_chi, ma_ky, diem_qt, diem_gk, diem_ck, diem_tong_ket
              FROM v_bang_diem_sinh_vien
              WHERE ma_mon = ? AND ma_ky = ?
              ORDER BY ma_sv
              """;
            List<TranscriptItemDTO> list = new ArrayList<>();
            try (Connection con = dataSource.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, courseId);
                ps.setString(2, semesterId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        list.add(new TranscriptItemDTO(
                                rs.getString("ma_mon"),
                                rs.getString("ten_mon"),
                                rs.getInt("so_tin_chi"),
                                rs.getString("ma_ky"),
                                rs.getBigDecimal("diem_qt"),
                                rs.getBigDecimal("diem_gk"),
                                rs.getBigDecimal("diem_ck"),
                                rs.getBigDecimal("diem_tong_ket")
                        ));
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("classTranscript(" + courseId + "," + semesterId + ")", e);
            }
            return list;
    }
}
