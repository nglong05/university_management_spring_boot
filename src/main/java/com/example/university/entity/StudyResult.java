package com.example.university.entity;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class StudyResult {
    @EqualsAndHashCode.Include
    private String studentId;

    @EqualsAndHashCode.Include
    private String courseId;

    @EqualsAndHashCode.Include
    private String semesterId;

    private BigDecimal processScore;
    private BigDecimal midtermScore;
    private BigDecimal finalExamScore;

    private BigDecimal finalScore;
}
//CREATE TABLE IF NOT EXISTS ket_qua_hoc_tap (
//        ma_sv   VARCHAR(15) NOT NULL,
//ma_mon  VARCHAR(15) NOT NULL,
//ma_ky   VARCHAR(12) NOT NULL,
//
//diem_qt DECIMAL(4,2) DEFAULT 0,
//diem_gk DECIMAL(4,2) DEFAULT 0,
//diem_ck DECIMAL(4,2) DEFAULT 0,
//
//        -- công thức tính có thể thay đổi:
//diem_tong_ket DECIMAL(4,2) GENERATED ALWAYS AS
//    (ROUND(diem_qt*0.3 + diem_gk*0.2 + diem_ck*0.5, 2)) STORED,
//PRIMARY KEY (ma_sv, ma_mon, ma_ky),
//
//CONSTRAINT fk_kq_sv  FOREIGN KEY (ma_sv)
//REFERENCES sinh_vien(ma_sv) ON UPDATE CASCADE,
//
//CONSTRAINT fk_kq_mh  FOREIGN KEY (ma_mon)
//REFERENCES mon_hoc(ma_mon) ON UPDATE CASCADE,
//
//CONSTRAINT fk_kq_kh  FOREIGN KEY (ma_ky)
//REFERENCES ky_hoc(ma_ky) ON UPDATE CASCADE
//);