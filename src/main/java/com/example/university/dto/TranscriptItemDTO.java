package com.example.university.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;

/**
 * Một dòng bảng điểm của SV cho 1 môn ở 1 kỳ.
 * Thường map trực tiếp từ view v_bang_diem_sinh_vien (JDBC ResultSet).
 *
 * JSON ví dụ:
 * {
 *   "courseId":"CT101","courseName":"Nhập môn Lập trình","credits":3,
 *   "semesterId":"2024-1","processScore":8.0,"midtermScore":7.5,
 *   "finalExamScore":8.5,"finalScore":8.25
 * }
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TranscriptItemDTO(
        String courseId,           // mã môn
        String courseName,         // tên môn
        int    credits,            // số tín chỉ
        String semesterId,         // mã kỳ
        BigDecimal processScore,   // điểm quá trình
        BigDecimal midtermScore,   // điểm giữa kỳ
        BigDecimal finalExamScore, // điểm cuối kỳ
        BigDecimal finalScore      // điểm tổng kết (DB tính - generated column)
) {}
