package com.example.university.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public final class AdminCatalog {

    public record Dept(
            @NotBlank String maKhoa,
            @NotBlank String tenKhoa,
            @Email @Size(max = 120) String email,
            @Size(max = 20) String soDienThoai,
            @Size(max = 100) String vanPhong
    ) {
    }

    public record Major(
            @NotBlank String maNganh,
            @NotBlank String tenNganh,
            @Size(max = 120) String maKhoa
    ) {
    }

    public record Semester(
            @NotBlank String maKy,
            @NotBlank String thongTin
    ) {
    }

    public record Course(
            @NotBlank String maMon,
            @NotBlank String tenMon,
            @NotNull @Min(1) @Max(10) Integer soTinChi,
            @NotBlank String maKhoa
    ) {
    }


    public record Student(
            @NotBlank String maSinhVien,
            @NotBlank String hoTen,
            @NotNull LocalDate ngaySinh,
            @Pattern(regexp = "M|F|O") String gioiTinh,
            String diaChi,
            String soDienThoai,
            @Email String email,
            @NotBlank String maKhoa,
            @NotBlank String maNganhHoc
    ) {
    }

    public record Lecturer(
            @NotBlank String maGiangVien,
            @NotBlank String hoTen,
            LocalDate ngaySinh,
            @Pattern(regexp = "M|F|O") String gioiTinh,
            String diaChi,
            String soDienThoai,
            @Email String email,
            @NotBlank String maKhoa
    ) {
    }


    public record LecturerCourseSemester(
            @NotBlank String maGiangVien,
            @NotBlank String maMon,
            @NotBlank String maKy
    ) {
    }

    public record StudyResult(
            @NotBlank String maSinhVien,
            @NotBlank String maMon,
            @NotBlank String maKy,
            @NotBlank String maGiangVien,
            @DecimalMin("0.0") @DecimalMax("10.0") BigDecimal diemQuaTrinh,
            @DecimalMin("0.0") @DecimalMax("10.0") BigDecimal diemGiuaKy,
            @DecimalMin("0.0") @DecimalMax("10.0") BigDecimal diemCuoiKy
    ) {
    }

    public record ResearchProject(
            @NotBlank String maSinhVien,
            @NotBlank String maGiangVien,
            @NotBlank String maKy,
            @NotBlank String moTa,
            @NotBlank String tenDeTai,
            @NotBlank String trangThai,
            LocalDate ngayDangKy,
            String ketQua,
            String fileDinhKem
    ) {
    }

    public record Users(
             @NotBlank String username,
             @NotBlank String passwordHash,          // HASHED
             @Pattern(regexp="STUDENT|LECTURER|ADMIN") @NotBlank String role,
             String maSv,
             String maGv,
             boolean enabled
    ) {
    }

    public record CreateUser(
            @NotBlank String username,
            @NotBlank String password,               // PLAINTEXT
            @Pattern(regexp="STUDENT|LECTURER|ADMIN") @NotBlank String role,
            String maSv,
            String maGv,
            boolean enabled
    ) {
    }

}
