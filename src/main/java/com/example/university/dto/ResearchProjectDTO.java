package com.example.university.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

/**
 * Một đề tài NCKH của sinh viên.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ResearchProjectDTO(
        String studentId,      // ma_sv
        String lecturerId,     // ma_gv
        String lecturerName,   // ho_ten gv
        String semesterId,     // ma_ky
        String semesterInfo,   // thong_tin ky_hoc
        String topicTitle,     // ten_de_tai
        String description,    // mo_ta
        String status,         // trang_thai
        LocalDateTime registeredAt, // ngay_dang_ky
        String result,         // ket_qua
        String attachment      // file_dinh_kem
) {}
