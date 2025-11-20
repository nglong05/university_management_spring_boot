package com.example.university.repository;

import com.example.university.dto.AdminCatalog.*;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class AdminCatalogJdbcRepository {

    private final JdbcTemplate jdbc;

//====================================================================
//    KHOA
//====================================================================

    public int upsertDepartment(Dept d) {
        String sql = """
      INSERT INTO khoa(ma_khoa, ten_khoa, email, so_dien_thoai, van_phong)
      VALUES (?, ?, ?, ?, ?)
      ON DUPLICATE KEY UPDATE
        ten_khoa=VALUES(ten_khoa), email=VALUES(email),
        so_dien_thoai=VALUES(so_dien_thoai), van_phong=VALUES(van_phong)
      """;
        return jdbc.update(sql, d.maKhoa(), d.tenKhoa(), d.email(), d.soDienThoai(), d.vanPhong());
    }

    public int upsertDepartments(List<Dept> list) {
        String sql = """
      INSERT INTO khoa(ma_khoa, ten_khoa, email, so_dien_thoai, van_phong)
      VALUES (?, ?, ?, ?, ?)
      ON DUPLICATE KEY UPDATE
        ten_khoa=VALUES(ten_khoa), email=VALUES(email),
        so_dien_thoai=VALUES(so_dien_thoai), van_phong=VALUES(van_phong)
      """;
        int[] r = jdbc.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override public void setValues(PreparedStatement ps, int i) throws SQLException {
                Dept d = list.get(i);
                ps.setString(1, d.maKhoa());
                ps.setString(2, d.tenKhoa());
                ps.setString(3, d.email());
                ps.setString(4, d.soDienThoai());
                ps.setString(5, d.vanPhong());
            }
            @Override public int getBatchSize() { return list.size(); }
        });
        return countAffected(r);
    }


//====================================================================
//                        NGANH
//====================================================================

    public int upsertMajor(Major m) {
        String sql = """
      INSERT INTO nganh_hoc(ma_nganh_hoc, ten_nganh_hoc, ma_khoa)
      VALUES (?, ?, ?)
      ON DUPLICATE KEY UPDATE
        ma_nganh_hoc=VALUES(ma_nganh_hoc), ten_nganh_hoc=VALUES(ten_nganh_hoc),
        ma_khoa=VALUES(ma_khoa)
      """;
        return jdbc.update(sql, m.maNganh(), m.tenNganh(), m.maKhoa());
    }

    public int upsertMajor(List<Major> list) {
        String sql = """
      INSERT INTO nganh_hoc(ma_nganh_hoc, ten_nganh_hoc, ma_khoa)
      VALUES (?, ?, ?)
      ON DUPLICATE KEY UPDATE
        ma_nganh_hoc=VALUES(ma_nganh_hoc), ten_nganh_hoc=VALUES(ten_nganh_hoc),
        ma_khoa=VALUES(ma_khoa)
      """;
        int[] r = jdbc.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override public void setValues(PreparedStatement ps, int i) throws SQLException {
                Major m = list.get(i);
                ps.setString(1, m.maNganh());
                ps.setString(2, m.tenNganh());
                ps.setString(3, m.maKhoa());
            }
            @Override public int getBatchSize() { return list.size(); }
        });
        return countAffected(r);
    }

//====================================================================
//                        KY HOC
//====================================================================

    public int upsertSemester(Semester s) {
        String sql = """
      INSERT INTO ky_hoc(ma_ky, thong_tin)
      VALUES (?, ?)
      ON DUPLICATE KEY UPDATE
        thong_tin=VALUES(thong_tin)
      """;
        return jdbc.update(sql, s.maKy(), s.thongTin());
    }

    public int upsertSemesters(List<Semester> list) {
        String sql = """
      INSERT INTO ky_hoc(ma_ky, thong_tin)
      VALUES (?, ?)
      ON DUPLICATE KEY UPDATE
        thong_tin=VALUES(thong_tin)
      """;
        int[] r = jdbc.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override public void setValues(PreparedStatement ps, int i) throws SQLException {
                Semester s = list.get(i);
                ps.setString(1, s.maKy());
                ps.setString(2, s.thongTin());
            }
            @Override public int getBatchSize() { return list.size(); }
        });
        return countAffected(r);
    }

//====================================================================
//                        MON HOC
//====================================================================

    public int upsertCourse(Course c) {
        String sql = """
      INSERT INTO mon_hoc(ma_mon, ten_mon, so_tin_chi, ma_khoa)
      VALUES (?, ?, ?, ?)
      ON DUPLICATE KEY UPDATE
        ten_mon=VALUES(ten_mon), so_tin_chi=VALUES(so_tin_chi), ma_khoa=VALUES(ma_khoa)
      """;
        return jdbc.update(sql, c.maMon(), c.tenMon(), c.soTinChi(), c.maKhoa());
    }

    public int upsertCourses(List<Course> list) {
        String sql = """
      INSERT INTO mon_hoc(ma_mon, ten_mon, so_tin_chi, ma_khoa)
      VALUES (?, ?, ?, ?)
      ON DUPLICATE KEY UPDATE
        ten_mon=VALUES(ten_mon), so_tin_chi=VALUES(so_tin_chi), ma_khoa=VALUES(ma_khoa)
      """;
        int[] r = jdbc.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override public void setValues(PreparedStatement ps, int i) throws SQLException {
                Course c = list.get(i);
                ps.setString(1, c.maMon());
                ps.setString(2, c.tenMon());
                ps.setInt(3, c.soTinChi());
                ps.setString(4, c.maKhoa());
            }
            @Override public int getBatchSize() { return list.size(); }
        });
        return countAffected(r);
    }

//====================================================================
//                        SINH VIEN
//====================================================================

    public int upsertStudent(Student s) {
        String sql = """
      INSERT INTO sinh_vien(ma_sv, ho_ten, ngay_sinh, gioi_tinh, dia_chi, so_dien_thoai, email, ma_khoa, ma_nganh_hoc)
      VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
      ON DUPLICATE KEY UPDATE
        ho_ten=VALUES(ho_ten),
        ngay_sinh=VALUES(ngay_sinh),
        gioi_tinh=VALUES(gioi_tinh),
        dia_chi=VALUES(dia_chi),
        so_dien_thoai=VALUES(so_dien_thoai),
        email=VALUES(email),
        ma_khoa=VALUES(ma_khoa),
        ma_nganh_hoc=VALUES(ma_nganh_hoc)
      """;
        return jdbc.update(sql, s.maSinhVien(), s.hoTen(), s.ngaySinh(), s.gioiTinh(), s.diaChi(),
                s.soDienThoai(), s.email(), s.maKhoa(), s.maNganhHoc());
    }

    public int upsertStudents(List<Student> list) {
//// Define valid department codes
//        ArrayList<String> validMaKhoa = new ArrayList<>(
//                Arrays.asList("CB", "CNTT", "DTVT", "KTĐT", "QTKD", "ATTT", "KT", "DPTT", "TTN")
//        );
//
//// Define valid major codes
//        ArrayList<String> validMaNganhHoc = new ArrayList<>(
//                Arrays.asList(
//                        "7520207", "7480107", "7520208",
//                        "7480202",
//                        "7480201_UDU", "7480201_VNH", "7480102", "7480201(CLC)", "7480201", "7480101",
//                        "7340101", "7340122",
//                        "7329001_GAM", "7320104", "7329001",
//                        "734030", "7340205", "7340301",
//                        "7520216", "7510301",
//                        "7480201"
//                )
//        );
//
//// Print students with invalid codes
//        for (Student s : list) {
//            if (!validMaKhoa.contains(s.maKhoa()) || !validMaNganhHoc.contains(s.maNganhHoc())) {
//                System.out.println("Warning: student " + s.maSinhVien() +
//                        " has invalid ma_khoa=" + s.maKhoa() +
//                        " or ma_nganh_hoc=" + s.maNganhHoc());
//            }
//        }

        String sql = """
      INSERT INTO sinh_vien(ma_sv, ho_ten, ngay_sinh, gioi_tinh, dia_chi, so_dien_thoai, email, ma_khoa, ma_nganh_hoc)
      VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
      ON DUPLICATE KEY UPDATE
        ho_ten=VALUES(ho_ten),
        ngay_sinh=VALUES(ngay_sinh),
        gioi_tinh=VALUES(gioi_tinh),
        dia_chi=VALUES(dia_chi),
        so_dien_thoai=VALUES(so_dien_thoai),
        email=VALUES(email),
        ma_khoa=VALUES(ma_khoa),
        ma_nganh_hoc=VALUES(ma_nganh_hoc)
      """;
        int[] r = jdbc.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override public void setValues(PreparedStatement ps, int i) throws SQLException {
                Student s = list.get(i);
                ps.setString(1, s.maSinhVien());
                ps.setString(2, s.hoTen());
                ps.setObject(3, s.ngaySinh());
                ps.setString(4, s.gioiTinh());
                ps.setString(5, s.diaChi());
                ps.setString(6, s.soDienThoai());
                ps.setString(7, s.email());
                ps.setString(8, s.maKhoa());
                ps.setString(9, s.maNganhHoc());
            }
            @Override public int getBatchSize() { return list.size(); }
        });
        return countAffected(r);
    }

//====================================================================
//                        GIANG VIEN
//====================================================================

    public int upsertLecturer(Lecturer l) {
        String sql = """
      INSERT INTO giang_vien(ma_gv, ho_ten, ngay_sinh, gioi_tinh, dia_chi, so_dien_thoai, email, ma_khoa)
      VALUES (?, ?, ?, ?, ?, ?, ?, ?)
      ON DUPLICATE KEY UPDATE
        ho_ten=VALUES(ho_ten),
        ngay_sinh=VALUES(ngay_sinh),
        gioi_tinh=VALUES(gioi_tinh),
        dia_chi=VALUES(dia_chi),
        so_dien_thoai=VALUES(so_dien_thoai),
        email=VALUES(email),
        ma_khoa=VALUES(ma_khoa)
      """;
        return jdbc.update(sql, l.maGiangVien(), l.hoTen(), l.ngaySinh(), l.gioiTinh(), l.diaChi(),
                l.soDienThoai(), l.email(), l.maKhoa());
    }

    public int upsertLecturers(List<Lecturer> list) {
        String sql = """
      INSERT INTO giang_vien(ma_gv, ho_ten, ngay_sinh, gioi_tinh, dia_chi, so_dien_thoai, email, ma_khoa)
      VALUES (?, ?, ?, ?, ?, ?, ?, ?)
      ON DUPLICATE KEY UPDATE
        ho_ten=VALUES(ho_ten),
        ngay_sinh=VALUES(ngay_sinh),
        gioi_tinh=VALUES(gioi_tinh),
        dia_chi=VALUES(dia_chi),
        so_dien_thoai=VALUES(so_dien_thoai),
        email=VALUES(email),
        ma_khoa=VALUES(ma_khoa)
      """;
        int[] r = jdbc.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override public void setValues(PreparedStatement ps, int i) throws SQLException {
                Lecturer l = list.get(i);
                ps.setString(1, l.maGiangVien());
                ps.setString(2, l.hoTen());
                ps.setObject(3, l.ngaySinh());
                ps.setString(4, l.gioiTinh());
                ps.setString(5, l.diaChi());
                ps.setString(6, l.soDienThoai());
                ps.setString(7, l.email());
                ps.setString(8, l.maKhoa());
            }
            @Override public int getBatchSize() { return list.size(); }
        });
        return countAffected(r);
    }
//====================================================================
//                        GIANG VIEN MON HOC KY HOC
//====================================================================

    public int upsertLecturerCourseSemester(LecturerCourseSemester lcs) {
        String sql = """
      INSERT INTO giangvien_monhoc(ma_gv, ma_mon, ma_ky)
      VALUES (?, ?, ?)
      ON DUPLICATE KEY UPDATE
        ma_gv=VALUES(ma_gv),
        ma_mon=VALUES(ma_mon),
        ma_ky=VALUES(ma_ky)
      """;
        return jdbc.update(sql, lcs.maGiangVien(), lcs.maMon(), lcs.maKy());
    }

    public int upsertLecturerCourseSemesters(List<LecturerCourseSemester> list) {
        String sql = """
      INSERT INTO giangvien_monhoc(ma_gv, ma_mon, ma_ky)
      VALUES (?, ?, ?)
      ON DUPLICATE KEY UPDATE
        ma_gv=VALUES(ma_gv),
        ma_mon=VALUES(ma_mon),
        ma_ky=VALUES(ma_ky)
      """;
        int[] r = jdbc.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override public void setValues(PreparedStatement ps, int i) throws SQLException {
                LecturerCourseSemester lcs = list.get(i);
                ps.setString(1, lcs.maGiangVien());
                ps.setString(2, lcs.maMon());
                ps.setString(3, lcs.maKy());
            }
            @Override public int getBatchSize() { return list.size(); }
        });
        return countAffected(r);
    }
//====================================================================
//                        KET QUA HOC TAP
//====================================================================
    public int upsertStudyResult(StudyResult sr) {
        String sql = """
      INSERT INTO ket_qua_hoc_tap(ma_sv, ma_mon, ma_ky, ma_gv, diem_qt, diem_gk, diem_ck)
      VALUES (?, ?, ?, ?, ?, ?, ?)
      ON DUPLICATE KEY UPDATE
        ma_gv=VALUES(ma_gv),
        diem_qt=VALUES(diem_qt),
        diem_gk=VALUES(diem_gk),
        diem_ck=VALUES(diem_ck)
      """;
        return jdbc.update(sql, sr.maSinhVien(), sr.maMon(), sr.maKy(), sr.maGiangVien(),
                sr.diemQuaTrinh(), sr.diemGiuaKy(), sr.diemCuoiKy());
    }

    public int upsertStudyResults(List<StudyResult> list) {
        String sql = """
      INSERT INTO ket_qua_hoc_tap(ma_sv, ma_mon, ma_ky, ma_gv, diem_qt, diem_gk, diem_ck)
      VALUES (?, ?, ?, ?, ?, ?, ?)
      ON DUPLICATE KEY UPDATE
        ma_gv=VALUES(ma_gv),
        diem_qt=VALUES(diem_qt),
        diem_gk=VALUES(diem_gk),
        diem_ck=VALUES(diem_ck)
      """;
        int[] r = jdbc.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override public void setValues(PreparedStatement ps, int i) throws SQLException {
                StudyResult sr = list.get(i);
                ps.setString(1, sr.maSinhVien());
                ps.setString(2, sr.maMon());
                ps.setString(3, sr.maKy());
                ps.setString(4, sr.maGiangVien());
                ps.setBigDecimal(5, sr.diemQuaTrinh());
                ps.setBigDecimal(6, sr.diemGiuaKy());
                ps.setBigDecimal(7, sr.diemCuoiKy());
            }
            @Override public int getBatchSize() { return list.size(); }
        });
        return countAffected(r);
    }

//====================================================================
//                        NGHIEN CUU KHOA HOC
//====================================================================

    public int upsertResearchProject(ResearchProject rp) {
        String sql = """
      INSERT INTO nghien_cuu_khoa_hoc(ma_sv, ma_gv, ma_ky, mo_ta, ten_de_tai, trang_thai, ngay_dang_ky, ket_qua, file_dinh_kem)
      VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
      ON DUPLICATE KEY UPDATE
        mo_ta=VALUES(mo_ta),
        ten_de_tai=VALUES(ten_de_tai),
        trang_thai=VALUES(trang_thai),
        ngay_dang_ky=VALUES(ngay_dang_ky),
        ket_qua=VALUES(ket_qua),
        file_dinh_kem=VALUES(file_dinh_kem)
      """;
        return jdbc.update(sql, rp.maSinhVien(), rp.maGiangVien(), rp.maKy(), rp.moTa(), rp.tenDeTai(),
                rp.trangThai(), rp.ngayDangKy(), rp.ketQua(), rp.fileDinhKem());
    }

    public int upsertResearchProjects(List<ResearchProject> list) {
        String sql = """
      INSERT INTO nghien_cuu_khoa_hoc(ma_sv, ma_gv, ma_ky, mo_ta, ten_de_tai, trang_thai, ngay_dang_ky, ket_qua, file_dinh_kem)
      VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
      ON DUPLICATE KEY UPDATE
        mo_ta=VALUES(mo_ta),
        ten_de_tai=VALUES(ten_de_tai),
        trang_thai=VALUES(trang_thai),
        ngay_dang_ky=VALUES(ngay_dang_ky),
        ket_qua=VALUES(ket_qua),
        file_dinh_kem=VALUES(file_dinh_kem)
      """;
        int[] r = jdbc.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override public void setValues(PreparedStatement ps, int i) throws SQLException {
                ResearchProject rp = list.get(i);
                ps.setString(1, rp.maSinhVien());
                ps.setString(2, rp.maGiangVien());
                ps.setString(3, rp.maKy());
                ps.setString(4, rp.moTa());
                ps.setString(5, rp.tenDeTai());
                ps.setString(6, rp.trangThai());
                ps.setObject(7, rp.ngayDangKy());
                ps.setString(8, rp.ketQua());
                ps.setString(9, rp.fileDinhKem());
            }
            @Override public int getBatchSize() { return list.size(); }
        });
        return countAffected(r);
    }

//====================================================================
//                        USERS
//====================================================================

    public int upsertUsers(Users u) {
        String sql = """
        INSERT INTO users(username, password_hash, role, ma_sv, ma_gv, enabled)
        VALUES (?, ?, ?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE
            password_hash = VALUES(password_hash),
            role = VALUES(role),
            ma_sv = VALUES(ma_sv),
            ma_gv = VALUES(ma_gv),
            enabled = VALUES(enabled)
        """;
        return jdbc.update(sql,
                u.username(),
                u.passwordHash(),
                u.role(),
                u.maSv(),
                u.maGv(),
                u.enabled());
    }

    public int upsertUserss(List<Users> list) {
        String sql = """
        INSERT INTO users(username, password_hash, role, ma_sv, ma_gv, enabled)
        VALUES (?, ?, ?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE
            password_hash = VALUES(password_hash),
            role = VALUES(role),
            ma_sv = VALUES(ma_sv),
            ma_gv = VALUES(ma_gv),
            enabled = VALUES(enabled)
        """;

        int[] r = jdbc.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Users u = list.get(i);
                ps.setString(1, u.username());
                ps.setString(2, u.passwordHash());
                ps.setString(3, u.role());
                ps.setString(4, u.maSv());
                ps.setString(5, u.maGv());
                ps.setBoolean(6, u.enabled());
            }

            @Override
            public int getBatchSize() {
                return list.size();
            }
        });

        return countAffected(r);
    }
    private static int countAffected(int[] a) {
        int ok = 0;
        for (int x : a) {
            // SUCCESS_NO_INFO (-2) coi như OK; x>0 coi là một bản ghi tác động
            if (x == java.sql.Statement.SUCCESS_NO_INFO || x > 0) ok++;
        }
        return ok;
    }
}
