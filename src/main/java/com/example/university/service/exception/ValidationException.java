package com.example.university.service.exception;

/** Lỗi do đầu vào không hợp lệ (tham số, định dạng, khoảng giá trị …). */
public class ValidationException extends RuntimeException {
    public ValidationException(String message) { super(message); }
}
