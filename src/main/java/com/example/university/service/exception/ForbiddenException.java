package com.example.university.service.exception;

/** Bị từ chối do không có quyền (ví dụ: GV không được phân công môn/kỳ này). */
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) { super(message); }
}
