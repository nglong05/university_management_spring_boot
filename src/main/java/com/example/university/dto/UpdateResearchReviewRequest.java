package com.example.university.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request để giảng viên nhận xét / đổi trạng thái đề tài NCKH của 1 SV
 */
@Data
public class UpdateResearchReviewRequest {

    @NotBlank
    private String maSv;       // khóa chính 1

    @NotBlank
    private String maKy;       // khóa chính 2

    @NotBlank
    private String trangThai;  // ví dụ: PENDING / APPROVED / REJECTED / DONE

    private String ketQua;     // nhận xét, kết luận
}
