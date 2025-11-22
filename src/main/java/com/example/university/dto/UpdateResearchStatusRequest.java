package com.example.university.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request body cho API giảng viên cập nhật trạng thái / kết quả NCKH.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateResearchStatusRequest {

    @NotBlank
    private String maSv;       // mã sinh viên

    @NotBlank
    private String maKy;       // mã kỳ (vd: 2025-1)

    @NotBlank
    private String trangThai;  // trạng thái mới (vd: CHO_DUYET, DANG_LAM, HOAN_THANH,...)

    private String ketQua;     // nhận xét / kết quả (optional)

    public String getMaSv() { return maSv; }
    public void setMaSv(String maSv) { this.maSv = maSv != null ? maSv.trim() : null; }

    public String getMaKy() { return maKy; }
    public void setMaKy(String maKy) { this.maKy = maKy != null ? maKy.trim() : null; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai != null ? trangThai.trim() : null; }

    public String getKetQua() { return ketQua; }
    public void setKetQua(String ketQua) { this.ketQua = ketQua != null ? ketQua.trim() : null; }
}
