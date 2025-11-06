package com.example.university.controller;

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

@RestController
@RequestMapping("/api/lecturers")
@RequiredArgsConstructor
@Validated
@Tag(name = "Lecturer APIs", description = "Chức năng cho giảng viên")
public class LecturerController {

    private final LecturerService lecturerService;

    @PutMapping("/{lecturerId}/grades")
    @Operation(summary = "Nhập/cập nhật điểm cho SV (cần đúng phân công môn/kỳ)")
    public ResponseEntity<Void> updateGrade(
            @PathVariable("lecturerId") String lecturerId,
            @Valid @RequestBody UpdateGradeRequest request) throws SQLException {

        lecturerService.updateGrade(lecturerId, request);
        // Không trả body để đơn giản; 204 No Content là chuẩn cho cập nhật thành công không có nội dung trả về.
        return ResponseEntity.noContent().build();
    }
}
