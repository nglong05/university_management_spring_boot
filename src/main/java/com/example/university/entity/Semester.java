package com.example.university.entity;

import lombok.*;

import java.time.LocalDate;

public class Semester {
    @EqualsAndHashCode.Include
    private String id;

    private String info;
    private LocalDate startDate;
    private LocalDate endDate;
}

//CREATE TABLE IF NOT EXISTS ky_hoc (
//        ma_ky        VARCHAR(12) PRIMARY KEY,
//thong_tin    VARCHAR(255) NOT NULL,
//bat_dau      DATE,
//ket_thuc     DATE
//);