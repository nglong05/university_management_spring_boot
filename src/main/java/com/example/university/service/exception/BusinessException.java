package com.example.university.service.exception;

/** Lỗi tổng quát nghiệp vụ (không thuộc dạng Validation/NotFound/Forbidden). */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) { super(message); }
}
