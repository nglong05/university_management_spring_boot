package com.example.university.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * Payload để SINH VIÊN đăng ký đề tài NCKH.
 *
 * JSON ví dụ:
 * {
 *   "maGv": "GV001",
 *   "maKy": "2025-1",
 *   "tenDeTai": "Ứng dụng Machine Learning...",
 *   "moTa": "Mô tả ngắn gọn ý tưởng, phạm vi",
 *   "fileDinhKem": "https://link-drive-hoac-github"
 * }
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResearchRegistrationRequest {

    @NotBlank(message = "maGv không được để trống")
    private String maGv;

    @NotBlank(message = "maKy không được để trống")
    private String maKy;

    @NotBlank(message = "tenDeTai không được để trống")
    private String tenDeTai;

    @NotBlank(message = "moTa không được để trống")
    private String moTa;

    /** Tuỳ chọn: link file trên Drive, GitHub, ... */
    private String fileDinhKem;
}
