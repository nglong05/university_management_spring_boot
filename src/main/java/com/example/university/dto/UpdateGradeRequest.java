package com.example.university.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Payload để GIẢNG VIÊN nhập/cập nhật điểm cho 1 SV ở 1 môn/kỳ.
 * Ràng buộc đủ 3 điểm để DB tính diem_tong_ket không bị NULL.
 *
 * JSON ví dụ:
 * {
 *   "maSv":"SV001", "maMon":"CT101", "maKy":"2024-1",
 *   "diemQt":8.0, "diemGk":7.5, "diemCk":8.5
 * }
 */
@JsonIgnoreProperties(ignoreUnknown = true) // bỏ qua field lạ trong JSON
public class UpdateGradeRequest {

    @NotBlank(message = "maSv không được để trống")
    private String maSv;

    @NotBlank(message = "maMon không được để trống")
    private String maMon;

    @NotBlank(message = "maKy không được để trống")
    private String maKy;

    @NotNull @DecimalMin("0.0") @DecimalMax("10.0")
    private BigDecimal diemQt;

    @NotNull @DecimalMin("0.0") @DecimalMax("10.0")
    private BigDecimal diemGk;

    @NotNull @DecimalMin("0.0") @DecimalMax("10.0")
    private BigDecimal diemCk;

    public UpdateGradeRequest() {}

    // getters/setters – đơn giản, có trim để tránh khoảng trắng thừa
    public String getMaSv() { return maSv; }
    public void setMaSv(String maSv) { this.maSv = maSv != null ? maSv.trim() : null; }

    public String getMaMon() { return maMon; }
    public void setMaMon(String maMon) { this.maMon = maMon != null ? maMon.trim() : null; }

    public String getMaKy() { return maKy; }
    public void setMaKy(String maKy) { this.maKy = maKy != null ? maKy.trim() : null; }

    public BigDecimal getDiemQt() { return diemQt; }
    public void setDiemQt(BigDecimal diemQt) { this.diemQt = diemQt; }

    public BigDecimal getDiemGk() { return diemGk; }
    public void setDiemGk(BigDecimal diemGk) { this.diemGk = diemGk; }

    public BigDecimal getDiemCk() { return diemCk; }
    public void setDiemCk(BigDecimal diemCk) { this.diemCk = diemCk; }
}
