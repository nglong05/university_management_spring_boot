package com.example.university.entity;

import lombok.*;

@Data
public class Account {
    private String username;
    private String passwordHash;
    private String role;
    private String studentId;
    private String teacherId;
    private boolean enabled;
}

//CREATE TABLE IF NOT EXISTS users (
//        username       VARCHAR(64) PRIMARY KEY,
//password_hash  VARCHAR(120) NOT NULL,
//role           ENUM('STUDENT','LECTURER','ADMIN') NOT NULL,
//ma_sv          VARCHAR(15),
//ma_gv          VARCHAR(15),
//enabled        BOOLEAN NOT NULL DEFAULT TRUE,
//CONSTRAINT fk_acc_sv FOREIGN KEY (ma_sv) REFERENCES sinh_vien(ma_sv)
//ON UPDATE CASCADE ON DELETE SET NULL,
//CONSTRAINT fk_acc_gv FOREIGN KEY (ma_gv) REFERENCES giang_vien(ma_gv)
//ON UPDATE CASCADE ON DELETE SET NULL
//);