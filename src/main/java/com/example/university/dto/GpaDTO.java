package com.example.university.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * GPA của SV. Nếu semesterId == null -> GPA tích lũy toàn khóa.
 *
 * JSON ví dụ (tích lũy):
 * { "studentId":"SV001", "semesterId":null, "gpa10":7.86, "gpa4":3.04 }
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record GpaDTO(
        String studentId,
        String semesterId, // có thể null nếu là GPA tích lũy
        Double gpa10,
        Double gpa4
) {}
