package com.example.university.controller;

import com.example.university.dto.*;
import com.example.university.service.LecturerService;

import com.example.university.service.export.LecturerClassPdfService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
    private final LecturerClassPdfService classPdfService; // thêm



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
    @GetMapping("/me/classes/{courseId}/semesters/{semesterId}/pdf")
    @PreAuthorize("hasRole('LECTURER')")
    public void exportMyClassPdf(
            @AuthenticationPrincipal AuthUser me,
            @PathVariable String courseId,
            @PathVariable String semesterId,
            HttpServletResponse response
    ) throws IOException {

        if (me.getLecturerId() == null) {
            response.sendError(HttpStatus.FORBIDDEN.value(), "Tài khoản không gắn với giảng viên.");
            return;
        }

        String fileName = URLEncoder.encode(
                "bang_diem_" + courseId + "_" + semesterId + ".pdf",
                StandardCharsets.UTF_8
        );

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + fileName);

        classPdfService.exportClassPdf(
                me.getLecturerId(),
                courseId,
                semesterId,
                me.getLecturerId(),                 // hiện dùng mã GV làm label
                response.getOutputStream()
        );
        response.flushBuffer();
    }
    // ================== NGHIÊN CỨU KHOA HỌC ==================

    /**
     * Giảng viên xem danh sách đề tài NCKH sinh viên đăng ký với mình.
     * - semester: filter theo mã kỳ (optional)
     * - status: filter theo trạng thái (optional)
     */
    @GetMapping("/me/research-projects")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<?> myResearchProjects(
            @AuthenticationPrincipal AuthUser me,
            @RequestParam(value = "semester", required = false) String semesterId) {

        if (me.getLecturerId() == null) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(service.listMyResearchProjects(me.getLecturerId(), semesterId));
    }
    @GetMapping("/me")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<com.example.university.entity.Lecturer> myProfile(
            @AuthenticationPrincipal AuthUser me) {
        if (me.getLecturerId() == null) {
            return ResponseEntity.status(403).build();
        }
        return service.getProfile(me.getLecturerId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    /**
     * Giảng viên cập nhật trạng thái + nhận xét/ketQua cho 1 đề tài NCKH.
     * Body: { "maSv": "...", "maKy": "2025-1", "trangThai": "DANG_LAM", "ketQua": "Nhận xét..." }
     */
    @PutMapping("/me/research-projects/status")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<?> updateResearchStatus(
            @AuthenticationPrincipal AuthUser me,
            @Valid @RequestBody UpdateResearchStatusRequest req
    ) {
        service.updateResearchStatus(me.getLecturerId(), req);
        return ResponseEntity.ok().build();
    }
    @PutMapping("/me/research-projects/review")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<?> reviewResearch(
            @AuthenticationPrincipal AuthUser me,
            @Valid @RequestBody UpdateResearchReviewRequest req) {
        service.reviewResearch(me.getLecturerId(), req);
        return ResponseEntity.ok().build();
    }

}