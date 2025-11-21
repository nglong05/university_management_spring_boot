package com.example.university.controller;

import com.example.university.dto.ClassTranscriptItemDTO;
import com.example.university.dto.LecturerCourseDTO;
import com.example.university.dto.UpdateGradeRequest;
import com.example.university.service.LecturerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;


import com.example.university.security.AuthUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/lecturers")
@RequiredArgsConstructor
public class LecturerController {

    private final LecturerService service;

    @PutMapping("/me/grades")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<?> updateMyGrade(
            @AuthenticationPrincipal AuthUser me,
            @Valid @RequestBody UpdateGradeRequest req) throws SQLException {
        service.updateGrade(me.getLecturerId(), req);
        return ResponseEntity.ok().build();
    }

    // danh sách môn mà giảng viên dạy
    @GetMapping("/me/courses")
    @PreAuthorize("hasRole('LECTURER')")
    public List<LecturerCourseDTO> myCourses(@AuthenticationPrincipal AuthUser me) {
        return service.listMyCourses(me.getLecturerId());
    }

    // bảng điểm của một lớp (môn + kỳ)
    @GetMapping("/me/courses/{courseId}/semesters/{semesterId}/transcript")
    @PreAuthorize("hasRole('LECTURER')")
    public List<ClassTranscriptItemDTO> classTranscript(
            @AuthenticationPrincipal AuthUser me,
            @PathVariable String courseId,
            @PathVariable String semesterId
    ) {
        return service.getClassTranscript(me.getLecturerId(), courseId, semesterId);
    }
}