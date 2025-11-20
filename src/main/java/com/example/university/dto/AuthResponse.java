package com.example.university.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class AuthResponse {
    private String tokenType;     // "Bearer"
    private String accessToken;
    private long   expiresInSec;
    private String username;
    private String role;          // STUDENT | LECTURER | ADMIN
    private String studentId;     // nếu là SV
    private String lecturerId;    // nếu là GV
}
