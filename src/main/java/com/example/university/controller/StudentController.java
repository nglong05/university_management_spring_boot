package com.example.university.controller;

import com.example.university.dto.GpaDTO;
import com.example.university.dto.TranscriptItemDTO;
import com.example.university.entity.Student;
import com.example.university.service.StudentService;
import com.example.university.service.export.PdfExportService;

import com.example.university.security.AuthUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Validated
@Tag(name = "Student APIs", description = "Tra cứu hồ sơ, bảng điểm, GPA")
public class StudentController {

    private final StudentService studentService;
    private final PdfExportService pdfExportService;

    @GetMapping("/{id}")
    @Operation(summary = "Lấy hồ sơ sinh viên theo mã")
    public ResponseEntity<Student> profile(@PathVariable String id) throws SQLException {
        return studentService.getProfile(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/transcript")
    @Operation(summary = "Bảng điểm của sinh viên; thêm ?semester=YYYY-N để lọc theo kỳ")
    public List<TranscriptItemDTO> transcript(
            @PathVariable String id,
            @RequestParam(value = "semester", required = false) String semesterId) throws SQLException {
        return studentService.getTranscript(id, semesterId);
    }

    @GetMapping("/{id}/gpa")
    @Operation(summary = "GPA hệ 10 & 4; thêm ?semester=YYYY-N để xem GPA của kỳ")
    public ResponseEntity<GpaDTO> gpa(
            @PathVariable String id,
            @RequestParam(value = "semester", required = false) String semesterId) throws SQLException {
        return studentService.getGpa(id, semesterId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Student> myProfile(@AuthenticationPrincipal AuthUser me) throws SQLException{
        if (me.getStudentId() == null) return ResponseEntity.status(403).build();
        return studentService.getProfile(me.getStudentId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/me/transcript")
    @PreAuthorize("hasRole('STUDENT')")
    public List<TranscriptItemDTO> myTranscript(
            @AuthenticationPrincipal AuthUser me,
            @RequestParam(value="semester", required=false) String semesterId) throws SQLException {
        return studentService.getTranscript(me.getStudentId(), semesterId);
    }

    @GetMapping("/me/gpa")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<GpaDTO> myGpa(
            @AuthenticationPrincipal AuthUser me,
            @RequestParam(value="semester", required=false) String semesterId) throws SQLException {
        return studentService.getGpa(me.getStudentId(), semesterId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/transcript.pdf")
    @io.swagger.v3.oas.annotations.Operation(summary = "Xuất PDF bảng điểm (ADMIN)")
    public ResponseEntity<byte[]> transcriptPdfAdmin(
            @PathVariable String id,
            @RequestParam(value = "semester", required = false) String semesterId) throws SQLException {

        byte[] pdf = pdfExportService.transcriptPdf(id, semesterId);
        String filename = "transcript-" + id + (semesterId == null || semesterId.isBlank() ? "-all" : "-" + semesterId) + ".pdf";

        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/me/transcript.pdf")
    @PreAuthorize("hasRole('STUDENT')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Sinh viên tự xuất PDF bảng điểm")
    public ResponseEntity<byte[]> myTranscriptPdf(
            @AuthenticationPrincipal com.example.university.security.AuthUser me,
            @RequestParam(value = "semester", required = false) String semesterId) throws SQLException {

        byte[] pdf = pdfExportService.transcriptPdf(me.getStudentId(), semesterId);
        String filename = "transcript-" + me.getStudentId() + (semesterId == null || semesterId.isBlank() ? "-all" : "-" + semesterId) + ".pdf";

        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(pdf);
    }

}
