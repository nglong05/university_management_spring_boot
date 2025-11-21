package com.example.university.dto;

public record LecturerCourseDTO(
        String courseId,    // ma_mon
        String courseName,  // ten_mon
        int    credits,     // so_tin_chi
        String semesterId   // ma_ky
) {}
