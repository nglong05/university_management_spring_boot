package com.example.university.entity;

import java.time.LocalDate;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Lecturer {
    @EqualsAndHashCode.Include
    private String id;

    private String fullName;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String addess;
    private String phone;
    private String email;
    private String departmentId;
}

//CREATE TABLE IF NOT EXISTS giang_vien (
//        ma_gv         VARCHAR(15)  PRIMARY KEY,
//ho_ten        VARCHAR(120) NOT NULL,
//ngay_sinh     DATE,
//gioi_tinh     ENUM('M','F','O'),
//dia_chi       VARCHAR(255),
//so_dien_thoai VARCHAR(20),
//email         VARCHAR(120) UNIQUE,
//ma_khoa       VARCHAR(10)  NOT NULL,
//CONSTRAINT fk_gv_khoa FOREIGN KEY (ma_khoa)
//REFERENCES khoa(ma_khoa) ON UPDATE CASCADE
//);