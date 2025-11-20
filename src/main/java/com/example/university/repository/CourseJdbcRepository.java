package com.example.university.repository;

import com.example.university.entity.Course;
import com.example.university.entity.Student;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class CourseJdbcRepository {

    private final DataSource dataSource;

    public CourseJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private Course mapCourse(ResultSet rs) throws SQLException {
        Course c = new Course();
        c.setId(rs.getString("ma_mon"));
        c.setName(rs.getString("ten_mon"));
        c.setCredits(rs.getInt("so_tin_chi"));
        c.setDepartmentId(rs.getString("ma_khoa"));
        return c;
    }

    public Optional<Course> findById(String id) {
        String sql = "SELECT ma_mon, ten_mon, so_tin_chi, ma_khoa FROM mon_hoc WHERE ma_mon = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapCourse(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("findById(" + id + ")", e);
        }
    }

    public List<Course> findByDepartment(String departmentId) {
        String sql = """
      SELECT ma_mon, ten_mon, so_tin_chi, ma_khoa
      FROM mon_hoc
      WHERE ma_khoa = ?
      ORDER BY ma_mon
      """;
        List<Course> list = new ArrayList<>();
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, departmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapCourse(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("findByDepartment(" + departmentId + ")", e);
        }
        return list;
    }

    /** Danh sách sinh viên theo môn + kỳ (cùng với giảng viên có thể lấy ở repo khác) */
    public List<Student> listStudents(String courseId, String semesterId) {
        String sql = """
      SELECT sv.*
      FROM ket_qua_hoc_tap kq
      JOIN sinh_vien sv ON sv.ma_sv = kq.ma_sv
      WHERE kq.ma_mon = ? AND kq.ma_ky = ?
      ORDER BY sv.ma_sv
      """;
        List<Student> list = new ArrayList<>();
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, courseId);
            ps.setString(2, semesterId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Student s = new Student();
                    s.setId(rs.getString("ma_sv"));
                    s.setFullName(rs.getString("ho_ten"));
                    Date d = rs.getDate("ngay_sinh");
                    s.setDateOfBirth(d != null ? d.toLocalDate() : null);
                    s.setGender(rs.getString("gioi_tinh"));
                    s.setAddress(rs.getString("dia_chi"));
                    s.setPhone(rs.getString("so_dien_thoai"));
                    s.setEmail(rs.getString("email"));
                    s.setMajorID(rs.getString("ma_nganh_hoc"));
                    list.add(s);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("listStudents(" + courseId + "," + semesterId + ")", e);
        }
        return list;
    }
}
