package com.example.university.dto;

import java.math.BigDecimal;

public record ClassTranscriptItemDTO(
        String studentId,        // ma_sv
        String studentName,      // ho_ten
        String courseId,         // ma_mon
        String courseName,       // ten_mon
        int    credits,          // so_tin_chi
        String semesterId,       // ma_ky
        BigDecimal processScore, // diem_qt
        BigDecimal midtermScore, // diem_gk
        BigDecimal finalExamScore,// diem_ck
        BigDecimal finalScore    // diem_tong_ket
) {}
