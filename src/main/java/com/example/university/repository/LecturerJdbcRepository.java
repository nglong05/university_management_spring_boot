package com.example.university.repository;

import com.example.university.dto.UpdateGradeRequest;
import com.example.university.dto.TranscriptItemDTO;
import com.example.university.dto.ClassTranscriptItemDTO;
import com.example.university.dto.LecturerCourseDTO;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.*;
import java.sql.*;

@Repository
public class LecturerJdbcRepository {
    private final DataSource dataSource;
    public LecturerJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // validate if one can grade this student
    public boolean canGrade(String id, String courseId, String semesterId) {
        String sql = "SELECT 1 FROM giangvien_monhoc WHERE ma_gv = ? AND ma_mon = ? AND ma_ky = ?";
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
                PreparedStatement ps = con.prepareStatement(sql)
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
    public List<LecturerCourseDTO> listMyCourses(String lecturerId) {
        String sql = """
        SELECT gvm.ma_mon, mh.ten_mon, mh.so_tin_chi, gvm.ma_ky
        FROM giangvien_monhoc gvm
        JOIN mon_hoc mh ON mh.ma_mon = gvm.ma_mon
        WHERE gvm.ma_gv = ?
        ORDER BY gvm.ma_ky, gvm.ma_mon
        """;
        List<LecturerCourseDTO> list = new ArrayList<>();
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, lecturerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new LecturerCourseDTO(
                            rs.getString("ma_mon"),
                            rs.getString("ten_mon"),
                            rs.getInt("so_tin_chi"),
                            rs.getString("ma_ky")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("listMyCourses(" + lecturerId + ")", e);
        }
        return list;
    }

    public List<ClassTranscriptItemDTO> classTranscript(String courseId, String semesterId) {
        String sql = """
      SELECT
        kq.ma_sv,
        sv.ho_ten,
        kq.ma_mon,
        mh.ten_mon,
        mh.so_tin_chi,
        kq.ma_ky,
        kq.diem_qt,
        kq.diem_gk,
        kq.diem_ck,
        kq.diem_tong_ket
      FROM ket_qua_hoc_tap kq
      JOIN sinh_vien sv ON sv.ma_sv = kq.ma_sv
      JOIN mon_hoc   mh ON mh.ma_mon = kq.ma_mon
      WHERE kq.ma_mon = ? AND kq.ma_ky = ?
      ORDER BY kq.ma_sv
      """;
        List<ClassTranscriptItemDTO> list = new ArrayList<>();
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, courseId);
            ps.setString(2, semesterId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new ClassTranscriptItemDTO(
                            rs.getString("ma_sv"),
                            rs.getString("ho_ten"),
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
