package com.example.university.entity;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Course {
    @EqualsAndHashCode.Include
    private String id;

    private String name;
    private Integer credits;
    private String departmentId;
}


//CREATE TABLE IF NOT EXISTS mon_hoc (
//ma_mon       VARCHAR(15)  PRIMARY KEY,
//ten_mon      VARCHAR(150) NOT NULL,
//so_tin_chi   INT NOT NULL,
//ma_khoa      VARCHAR(10)  NOT NULL,
//CONSTRAINT fk_mh_khoa FOREIGN KEY (ma_khoa)
//REFERENCES khoa(ma_khoa) ON UPDATE CASCADE
//);
