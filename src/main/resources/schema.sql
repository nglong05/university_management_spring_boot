-- SCHEMA
CREATE DATABASE IF NOT EXISTS universitydb;
USE universitydb;

-- Bảng Khoa
CREATE TABLE IF NOT EXISTS khoa (
    ma_khoa       VARCHAR(10)  PRIMARY KEY,
    ten_khoa      VARCHAR(100) NOT NULL,
    email         VARCHAR(100) UNIQUE,
    so_dien_thoai VARCHAR(20),
    van_phong     VARCHAR(100)
);

-- Bảng Sinh viên
CREATE TABLE IF NOT EXISTS sinh_vien (
    ma_sv         VARCHAR(15)  PRIMARY KEY,
    ho_ten        VARCHAR(120) NOT NULL,
    ngay_sinh     DATE         NOT NULL,
    gioi_tinh     ENUM('M','F','O') NOT NULL,
    dia_chi       VARCHAR(255),
    so_dien_thoai VARCHAR(20),
    email         VARCHAR(120) UNIQUE,
    ma_khoa       VARCHAR(10)  NOT NULL,
    CONSTRAINT fk_sv_khoa FOREIGN KEY (ma_khoa)
    REFERENCES khoa(ma_khoa) ON UPDATE CASCADE
);

-- Bảng Giảng viên
CREATE TABLE IF NOT EXISTS giang_vien (
    ma_gv         VARCHAR(15)  PRIMARY KEY,
    ho_ten        VARCHAR(120) NOT NULL,
    ngay_sinh     DATE,
    gioi_tinh     ENUM('M','F','O'),
    dia_chi       VARCHAR(255),
    so_dien_thoai VARCHAR(20),
    email         VARCHAR(120) UNIQUE,
    ma_khoa       VARCHAR(10)  NOT NULL,
    CONSTRAINT fk_gv_khoa FOREIGN KEY (ma_khoa)
    REFERENCES khoa(ma_khoa) ON UPDATE CASCADE
);

-- Bảng Kỳ học
CREATE TABLE IF NOT EXISTS ky_hoc (
    ma_ky        VARCHAR(12) PRIMARY KEY,
    thong_tin    VARCHAR(255) NOT NULL,
    bat_dau      DATE,
    ket_thuc     DATE
);

-- Bảng Môn học
CREATE TABLE IF NOT EXISTS mon_hoc (
    ma_mon       VARCHAR(15)  PRIMARY KEY,
    ten_mon      VARCHAR(150) NOT NULL,
    so_tin_chi   INT NOT NULL,
    ma_khoa      VARCHAR(10)  NOT NULL,
    CONSTRAINT fk_mh_khoa FOREIGN KEY (ma_khoa)
    REFERENCES khoa(ma_khoa) ON UPDATE CASCADE
);

-- Môn học - Kỳ học (môn nào mở ở kỳ nào)
CREATE TABLE IF NOT EXISTS monhoc_kyhoc (
    ma_mon VARCHAR(15) NOT NULL,
    ma_ky  VARCHAR(12) NOT NULL,
    PRIMARY KEY (ma_mon, ma_ky),

    CONSTRAINT fk_mhkh_mh FOREIGN KEY (ma_mon)
    REFERENCES mon_hoc(ma_mon) ON UPDATE CASCADE,

    CONSTRAINT fk_mhkh_kh FOREIGN KEY (ma_ky)
    REFERENCES ky_hoc(ma_ky) ON UPDATE CASCADE
);

-- Phân công giảng viên dạy môn theo kỳ
CREATE TABLE IF NOT EXISTS giangvien_monhockyhoc (
    ma_gv VARCHAR(15) NOT NULL,
    ma_mon VARCHAR(15) NOT NULL,
    ma_ky  VARCHAR(12) NOT NULL,
    PRIMARY KEY (ma_gv, ma_mon, ma_ky),

    CONSTRAINT fk_gvmhkh_gv FOREIGN KEY (ma_gv)
    REFERENCES giang_vien(ma_gv) ON UPDATE CASCADE,

    CONSTRAINT fk_gvmhkh_mhkh FOREIGN KEY (ma_mon, ma_ky)
    REFERENCES monhoc_kyhoc(ma_mon, ma_ky) ON UPDATE CASCADE
);

-- Kết quả học tập = Đăng ký học phần + điểm
CREATE TABLE IF NOT EXISTS ket_qua_hoc_tap (
    ma_sv   VARCHAR(15) NOT NULL,
    ma_mon  VARCHAR(15) NOT NULL,
    ma_ky   VARCHAR(12) NOT NULL,

    diem_qt DECIMAL(4,2) DEFAULT 0,
    diem_gk DECIMAL(4,2) DEFAULT 0,
    diem_ck DECIMAL(4,2) DEFAULT 0,

    -- công thức tính có thể thay đổi:
    diem_tong_ket DECIMAL(4,2) GENERATED ALWAYS AS
    (ROUND(diem_qt*0.3 + diem_gk*0.2 + diem_ck*0.5, 2)) STORED,
    PRIMARY KEY (ma_sv, ma_mon, ma_ky),

    CONSTRAINT fk_kq_sv  FOREIGN KEY (ma_sv)
    REFERENCES sinh_vien(ma_sv) ON UPDATE CASCADE,

    CONSTRAINT fk_kq_mh  FOREIGN KEY (ma_mon)
    REFERENCES mon_hoc(ma_mon) ON UPDATE CASCADE,

    CONSTRAINT fk_kq_kh  FOREIGN KEY (ma_ky)
    REFERENCES ky_hoc(ma_ky) ON UPDATE CASCADE
);