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
}