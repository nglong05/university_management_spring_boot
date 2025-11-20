package com.example.university.entity;

import java.time.LocalDate;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Student {
    private String id;
    private String fullName;
    private LocalDate dateOfBirth;
    private String gender;
    private String address;
    private String phone;
    private String email;
    private String departmentID;
    private String majorID;
}

//+---------------+-------------------+------+-----+---------+-------+
//| Field         | Type              | Null | Key | Default | Extra |
//+---------------+-------------------+------+-----+---------+-------+
//| ma_sv         | varchar(50)       | NO   | PRI | NULL    |       |
//| ho_ten        | varchar(100)      | NO   |     | NULL    |       |
//| ngay_sinh     | date              | NO   |     | NULL    |       |
//| gioi_tinh     | enum('M','F','O') | NO   |     | NULL    |       |
//| dia_chi       | varchar(500)      | YES  |     | NULL    |       |
//| so_dien_thoai | varchar(100)      | YES  |     | NULL    |       |
//| email         | varchar(100)      | YES  | UNI | NULL    |       |
//| ma_nganh_hoc  | varchar(50)       | NO   | MUL | NULL    |       |
//+---------------+-------------------+------+-----+---------+-------+
