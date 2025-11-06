package com.example.university.service.exception;

/** Không tìm thấy tài nguyên (SV, môn, kỳ …). */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) { super(message); }
}
