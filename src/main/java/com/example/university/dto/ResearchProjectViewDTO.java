package com.example.university.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

/**
 * Thông tin đề tài NCKH để giảng viên xem.
 * Map từ bảng nghien_cuu_khoa_hoc + join sinh_vien, ky_hoc.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ResearchProjectViewDTO(
        String studentId,      // ma_sv
        String studentName,    // sv.ho_ten
        String lecturerId,     // ma_gv
        String semesterId,     // ma_ky
        String semesterInfo,   // ky_hoc.thong_tin
        String title,          // ten_de_tai
        String description,    // mo_ta
        String status,         // trang_thai
        LocalDateTime registeredAt, // ngay_dang_ky
        String result,         // ket_qua (nhận xét/kết quả)
        String attachment      // file_dinh_kem
) {}
