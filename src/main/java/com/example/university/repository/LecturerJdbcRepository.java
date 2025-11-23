package com.example.university.repository;

import com.example.university.dto.*;
import com.example.university.entity.Lecturer;
import jakarta.annotation.Nullable;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Date;
import java.time.LocalDateTime;
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

    public Optional<Lecturer> findById(String id) {
        String sql = """
        SELECT ma_gv, ho_ten, ngay_sinh, gioi_tinh, dia_chi, so_dien_thoai, email, ma_khoa
        FROM giang_vien
        WHERE ma_gv = ?
        """;
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                com.example.university.entity.Lecturer g = new com.example.university.entity.Lecturer();
                g.setId(rs.getString("ma_gv"));
                g.setFullName(rs.getString("ho_ten"));
                Date d = rs.getDate("ngay_sinh");
                if (d != null) g.setDateOfBirth(d.toLocalDate());
                String sex = rs.getString("gioi_tinh");
                g.setGender(sex != null ? com.example.university.entity.Gender.fromDb(sex) : null);
                g.setAddress(rs.getString("dia_chi"));
                g.setPhone(rs.getString("so_dien_thoai"));
                g.setEmail(rs.getString("email"));
                g.setDepartmentId(rs.getString("ma_khoa"));
                return Optional.of(g);
            }
        } catch (SQLException e) {
            throw new RuntimeException("findById(" + id + ")", e);
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

    public int insertStudentToClass(String studentId,
                                    String courseId,
                                    String semesterId,
                                    String lecturerId) {
        String sql = """
            INSERT INTO ket_qua_hoc_tap (ma_sv, ma_mon, ma_ky, ma_gv, diem_qt, diem_gk, diem_ck)
            VALUES (?,?,?,?,0,0,0)
            """;
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, studentId);
            ps.setString(2, courseId);
            ps.setString(3, semesterId);
            ps.setString(4, lecturerId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("insertStudentToClass(" + studentId + "," + courseId + "," + semesterId + ")", e);
        }
    }

    public int deleteStudentFromClass(String studentId, String courseId, String semesterId, String lecturerId) {
        String sql = """
            DELETE FROM ket_qua_hoc_tap
            WHERE ma_sv = ? AND ma_mon = ? AND ma_ky = ? AND ma_gv = ?
            """;
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, studentId);
            ps.setString(2, courseId);
            ps.setString(3, semesterId);
            ps.setString(4, lecturerId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("deleteStudentFromClass(" + studentId + "," + courseId + "," + semesterId + ")", e);
        }
    }
    // Danh sách NCKH của SV do 1 giảng viên hướng dẫn (lọc theo kỳ nếu có)
    public List<ResearchProjectDTO> listResearchByLecturer(String lecturerId,
                                                           @Nullable String semesterId) {
        String base = """
            SELECT n.ma_sv,
               sv.ho_ten       AS ten_sv,
               n.ma_gv,
               gv.ho_ten       AS ten_gv,
               n.ma_ky,
               kh.thong_tin,
               n.ten_de_tai,
               n.mo_ta,
               n.trang_thai,
               n.ngay_dang_ky,
               n.ket_qua,
               n.file_dinh_kem
            FROM nghien_cuu_khoa_hoc n
            JOIN sinh_vien sv ON sv.ma_sv = n.ma_sv
            JOIN giang_vien gv ON gv.ma_gv = n.ma_gv
            JOIN ky_hoc kh ON kh.ma_ky = n.ma_ky
            WHERE n.ma_gv = ?
            """;
        String order = " ORDER BY n.ma_ky, n.ma_sv";

        String sql;
        boolean filterSemester = (semesterId != null && !semesterId.isBlank());
        if (filterSemester) {
            sql = base + " AND n.ma_ky = ?" + order;
        } else {
            sql = base + order;
        }

        List<ResearchProjectDTO> list = new ArrayList<>();
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, lecturerId);
            if (filterSemester) ps.setString(2, semesterId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timestamp ts = rs.getTimestamp("ngay_dang_ky");
                    LocalDateTime registeredAt = ts != null ? ts.toLocalDateTime() : null;

                    list.add(new ResearchProjectDTO(
                            rs.getString("ma_sv"),
                            rs.getString("ten_sv"),
                            rs.getString("ma_gv"),
                            rs.getString("ten_gv"),
                            rs.getString("ma_ky"),
                            rs.getString("thong_tin"),
                            rs.getString("ten_de_tai"),
                            rs.getString("mo_ta"),
                            rs.getString("trang_thai"),
                            registeredAt,
                            rs.getString("ket_qua"),
                            rs.getString("file_dinh_kem")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("listResearchByLecturer(" + lecturerId + "," + semesterId + ")", e);
        }
        return list;
    }

    /**
     * Cập nhật trạng thái + kết quả/nhận xét đề tài.
     * Chỉ update nếu ma_gv trùng với lecturerId (đảm bảo chỉ chỉnh đề tài của mình).
     */
    public int updateResearchStatus(
            String lecturerId,
            String studentId,
            String semesterId,
            String status,
            String result
    ) {
        String sql = """
                UPDATE nghien_cuu_khoa_hoc
                SET trang_thai = ?, ket_qua = ?
                WHERE ma_gv = ? AND ma_sv = ? AND ma_ky = ?
                """;

        try (
                Connection con = dataSource.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, status);
            if (result != null) {
                ps.setString(2, result);
            } else {
                ps.setNull(2, Types.VARCHAR);
            }
            ps.setString(3, lecturerId);
            ps.setString(4, studentId);
            ps.setString(5, semesterId);

            return ps.executeUpdate(); // 0 nếu không có dòng nào phù hợp
        } catch (SQLException e) {
            throw new RuntimeException("updateResearchStatus(" + lecturerId + "," + studentId + "," + semesterId + ")", e);
        }
    }
    // Cập nhật trạng thái + kết quả/nhận xét đề tài NCKH
    public int updateResearchReview(String lecturerId, UpdateResearchReviewRequest req) {
        String sql = """
            UPDATE nghien_cuu_khoa_hoc
            SET trang_thai = ?, ket_qua = ?
            WHERE ma_sv = ? AND ma_gv = ? AND ma_ky = ?
            """;
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, req.getTrangThai());
            ps.setString(2, req.getKetQua());
            ps.setString(3, req.getMaSv());
            ps.setString(4, lecturerId);
            ps.setString(5, req.getMaKy());

            return ps.executeUpdate(); // 0 nếu không có bản ghi nào
        } catch (SQLException e) {
            throw new RuntimeException("updateResearchReview(" + lecturerId + "," + req + ")", e);
        }
    }

}
