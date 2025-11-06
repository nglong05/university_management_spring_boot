package com.example.university.controller;

import com.example.university.dto.GpaDTO;
import com.example.university.dto.TranscriptItemDTO;
import com.example.university.entity.Student;
import com.example.university.service.StudentService;

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
}
