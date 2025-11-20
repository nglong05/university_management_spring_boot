package com.example.university.ui;

import lombok.Data;

@Data
public class UiSession {
    private String username;
    private String role;
    private String studentId;
    private String lecturerId;
    private String jwt;

    public boolean isLoggedIn() {
        return jwt != null && !jwt.isBlank();
    }

    public boolean isStudent() {
        return "STUDENT".equalsIgnoreCase(role);
    }

    public boolean isLecturer() {
        return "LECTURER".equalsIgnoreCase(role);
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }
}
