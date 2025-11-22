package com.example.university.repository;

import com.example.university.dto.ResearchProjectDTO;
import com.example.university.dto.ResearchRegistrationRequest;
import jakarta.annotation.Nullable;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ResearchJdbcRepository {

    private final DataSource dataSource;

    public ResearchJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private ResearchProjectDTO map(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("ngay_dang_ky");
        LocalDateTime registeredAt = ts != null ? ts.toLocalDateTime() : null;

        return new ResearchProjectDTO(
                rs.getString("ma_sv"),
                rs.getString("ten_sv"),
                rs.getString("ma_gv"),
                rs.getString("ten_gv"),
                rs.getString("ma_ky"),
                rs.getString("thong_tin_ky"),
                rs.getString("ten_de_tai"),
                rs.getString("mo_ta"),
                rs.getString("trang_thai"),
                registeredAt,
                rs.getString("ket_qua"),
                rs.getString("file_dinh_kem")
        );
    }

    /** Danh sách đề tài của 1 sinh viên (có thể lọc theo kỳ) */
    public List<ResearchProjectDTO> findByStudent(String studentId, @Nullable String semesterId) {
        String base = """
                SELECT nckh.ma_sv,
                       sv.ho_ten      AS ten_sv,
                       nckh.ma_gv,
                       gv.ho_ten      AS ten_gv,
                       nckh.ma_ky,
                       kh.thong_tin   AS thong_tin_ky,
                       nckh.ten_de_tai,
                       nckh.mo_ta,
                       nckh.trang_thai,
                       nckh.ngay_dang_ky,
                       nckh.ket_qua,
                       nckh.file_dinh_kem
                FROM nghien_cuu_khoa_hoc nckh
                JOIN sinh_vien sv ON sv.ma_sv = nckh.ma_sv
                JOIN giang_vien gv ON gv.ma_gv = nckh.ma_gv
                JOIN ky_hoc kh ON kh.ma_ky = nckh.ma_ky
                WHERE nckh.ma_sv = ?
                """;
        String order = " ORDER BY nckh.ngay_dang_ky DESC";
        String sql;
        if (semesterId == null || semesterId.isBlank()) {
            sql = base + order;
        } else {
            sql = base + " AND nckh.ma_ky = ?" + order;
        }

        List<ResearchProjectDTO> list = new ArrayList<>();
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, studentId);
            if (semesterId != null && !semesterId.isBlank()) {
                ps.setString(2, semesterId);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException("findByStudent(" + studentId + "," + semesterId + ")", e);
        }
        return list;
    }

    /** Kiểm tra đã tồn tại đề tài với (ma_sv, ma_gv, ma_ky) chưa */
    public boolean exists(String studentId, String lecturerId, String semesterId) {
        String sql = """
                SELECT 1 FROM nghien_cuu_khoa_hoc
                WHERE ma_sv = ? AND ma_gv = ? AND ma_ky = ?
                """;
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setString(2, lecturerId);
            ps.setString(3, semesterId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException("exists(" + studentId + "," + lecturerId + "," + semesterId + ")", e);
        }
    }

    /** Thêm mới 1 đăng ký đề tài NCKH */
    public int insert(String studentId,
                      ResearchRegistrationRequest req,
                      String defaultStatus) {
        String sql = """
                INSERT INTO nghien_cuu_khoa_hoc
                    (ma_sv, ma_gv, ma_ky, mo_ta, ten_de_tai, trang_thai, ket_qua, file_dinh_kem)
                VALUES (?, ?, ?, ?, ?, ?, NULL, ?)
                """;
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setString(2, req.getMaGv());
            ps.setString(3, req.getMaKy());
            ps.setString(4, req.getMoTa());
            ps.setString(5, req.getTenDeTai());
            ps.setString(6, defaultStatus);
            ps.setString(7, req.getFileDinhKem());
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException("insert research project", e);
        }
    }

    /** Đổi trạng thái đề tài (hủy, v.v.) */
    public int updateStatus(String studentId,
                            String lecturerId,
                            String semesterId,
                            String status) {
        String sql = """
                UPDATE nghien_cuu_khoa_hoc
                SET trang_thai = ?
                WHERE ma_sv = ? AND ma_gv = ? AND ma_ky = ?
                """;
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, studentId);
            ps.setString(3, lecturerId);
            ps.setString(4, semesterId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("updateStatus", e);
        }
    }
}
