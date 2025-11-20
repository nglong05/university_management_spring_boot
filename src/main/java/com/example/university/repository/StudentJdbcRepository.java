package com.example.university.repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import com.example.university.dto.GpaDTO;
import com.example.university.dto.TranscriptItemDTO;
import com.example.university.entity.Student;
import jakarta.annotation.Nullable;
import org.springframework.stereotype.Repository;

@Repository
public class StudentJdbcRepository {

    private final DataSource dataSource;
    public StudentJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // this is a helper that turn ResultSet to POJO
    private Student mapStudent(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setId(rs.getString("ma_sv"));
        s.setFullName(rs.getString("ho_ten"));
        Date d = rs.getDate("ngay_sinh");
        s.setDateOfBirth(d != null ? d.toLocalDate() : null);
        s.setGender(rs.getString("gioi_tinh"));
        s.setAddress(rs.getString("dia_chi"));
        s.setPhone(rs.getString("so_dien_thoai"));
        s.setEmail(rs.getString("email"));
        s.setDepartmentID(rs.getString("ma_khoa"));
        s.setMajorID(rs.getString("ma_nganh_hoc"));
        return s;
    }


    // queries
    public Optional<Student> findById(String id) throws SQLException {
        String sql = "SELECT * FROM sinh_vien WHERE ma_sv = ?";
        try (
            Connection con = dataSource.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, id); // use 1-indexing
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Student s = mapStudent(rs);
                    return Optional.of(s);
                } else {
                    return Optional.empty();
                }
            }
        } // i can make a catch runtimeexception here for good habit but nah
    }

    // truy van toan bo bang diem cua mot hoc sinh theo ky hoac tat ca cac ky
    public List<TranscriptItemDTO> transcript(String studentId, @Nullable String semesterId) {
        String base = "SELECT * FROM v_bang_diem_sinh_vien ";
        String allSemester = "WHERE ma_sv = ?";
        String oneSemester = "WHERE ma_sv = ? AND ma_ky = ?";
        String order = " ORDER BY ma_sv, ma_mon";
        String sql;
        if (semesterId == null || semesterId.isBlank()) {
            sql = base + allSemester + order;
        } else {
            sql = base + oneSemester + order;
        }

        List<TranscriptItemDTO> list = new ArrayList<>();
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, studentId);
            if (semesterId != null) ps.setString(2, semesterId);
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
            throw new RuntimeException(e);
        }
        return list;
    }


    // GPA theo ky hoac tichluy
    public Optional<GpaDTO> gpa(String studentId, @Nullable String semesterId) {
        String sql;
        if (semesterId == null || semesterId.isBlank()) {
            sql = "SELECT gpa10, gpa4 FROM v_gpa_tich_luy WHERE ma_sv = ?";
        } else {
            sql = "SELECT gpa10, gpa4 FROM v_gpa_tung_ky WHERE ma_sv = ? AND ma_ky = ?";
        }

        try (
                Connection con = dataSource.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, studentId);
            if (semesterId != null) ps.setString(2, semesterId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(
                        new GpaDTO(studentId, semesterId, rs.getDouble("gpa10"), rs.getDouble("gpa4"))
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("gpa(" + studentId + "," + semesterId + ")", e);
        }
    }


    //danh sach sinh  vien cua mot hoc phan (where mon= and ky=)
    public List<Student> ListStudentOfCourse(String courseId, String semesterId) {
        List<Student> list = new ArrayList<>();
        String sql = """
                SELECT sv.*
                FROM ket_qua_hoc_tap kq
                JOIN sinh_vien sv ON sv.ma_sv = kq.ma_sv
                WHERE kq.ma_mon = ? AND kq.ma_ky = ?
                ORDER BY sv.ma_sv
                """;
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, courseId);
            ps.setString(2, semesterId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapStudent(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("listStudentsOfCourse(" + courseId + "," + semesterId + ")", e);
        }
        return list;
    }

    public int insertStudent(Connection con, com.example.university.entity.Student s) throws SQLException {
        String sql = """
    INSERT INTO sinh_vien(ma_sv, ho_ten, ngay_sinh, gioi_tinh, dia_chi, so_dien_thoai, email, ma_khoa)
    VALUES (?,?,?,?,?,?,?,?)
  """;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getId());
            ps.setString(2, s.getFullName());
            ps.setDate(3, java.sql.Date.valueOf(s.getDateOfBirth()));
            ps.setString(4, s.getGender());
            ps.setString(5, s.getAddress());
            ps.setString(6, s.getPhone());
            ps.setString(7, s.getEmail());
            ps.setString(8, s.getDepartmentID());
            ps.setString(9, s.getMajorID());
            return ps.executeUpdate();
        }
    }

}