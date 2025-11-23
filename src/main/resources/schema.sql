# DROP TABLE IF EXISTS users;
#
# DROP TABLE IF EXISTS nghien_cuu_khoa_hoc;
# DROP TABLE IF EXISTS ket_qua_hoc_tap;
#
# DROP TABLE IF EXISTS giangvien_monhoc;
# DROP TABLE IF EXISTS giang_vien;
# DROP TABLE IF EXISTS sinh_vien;
#
# DROP TABLE IF EXISTS ky_hoc;
# DROP TABLE IF EXISTS mon_hoc;
# DROP TABLE IF EXISTS nganh_hoc;
# DROP TABLE IF EXISTS khoa;



--  ====================================================================================
--     UNIVERSITY PARTS
--  ====================================================================================

CREATE TABLE IF NOT EXISTS khoa (
    ma_khoa       VARCHAR(50)  PRIMARY KEY,
    ten_khoa      VARCHAR(100) NOT NULL UNIQUE,
    email         VARCHAR(100) UNIQUE,
    so_dien_thoai VARCHAR(100) UNIQUE,
    van_phong     VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS nganh_hoc (
    ma_nganh_hoc  VARCHAR(50) PRIMARY KEY,
    ten_nganh_hoc VARCHAR(100) NOT NULL,
    ma_khoa       VARCHAR(50) NOT NULL,
    CONSTRAINT fk_nganh_khoa FOREIGN KEY (ma_khoa)
    REFERENCES khoa(ma_khoa) ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS ky_hoc (
    ma_ky        VARCHAR(50) PRIMARY KEY,
    thong_tin    VARCHAR(500) NOT NULL
);

CREATE TABLE IF NOT EXISTS mon_hoc (
    ma_mon       VARCHAR(50)  PRIMARY KEY,
    ten_mon      VARCHAR(100) NOT NULL,
    so_tin_chi   INT NOT NULL,
    ma_khoa      VARCHAR(100)  NOT NULL,
    CONSTRAINT fk_mh_khoa FOREIGN KEY (ma_khoa)
    REFERENCES khoa(ma_khoa) ON UPDATE CASCADE
);

-- =====================================================================================
--          HUMAN PARTS
-- =====================================================================================


CREATE TABLE IF NOT EXISTS sinh_vien (
    ma_sv         VARCHAR(50)  PRIMARY KEY,
    ho_ten        VARCHAR(100) NOT NULL,
    ngay_sinh     DATE         NOT NULL,
    gioi_tinh     ENUM('M','F','O') NOT NULL,
    dia_chi       VARCHAR(500),
    so_dien_thoai VARCHAR(100),
    email         VARCHAR(100) UNIQUE,
    ma_khoa       VARCHAR(100) NOT NULL,
    ma_nganh_hoc  VARCHAR(50)  NOT NULL,
    CONSTRAINT fk_sv_khoa FOREIGN KEY (ma_khoa)
    REFERENCES khoa(ma_khoa) ON UPDATE CASCADE,
    CONSTRAINT fk_sv_nganh FOREIGN KEY (ma_nganh_hoc)
    REFERENCES nganh_hoc(ma_nganh_hoc) ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS giang_vien (
    ma_gv         VARCHAR(50)  PRIMARY KEY,
    ho_ten        VARCHAR(100) NOT NULL,
    ngay_sinh     DATE,
    gioi_tinh     ENUM('M','F','O'),
    dia_chi       VARCHAR(500),
    so_dien_thoai VARCHAR(100),
    email         VARCHAR(100) UNIQUE,
    ma_khoa       VARCHAR(50)  NOT NULL,
    CONSTRAINT fk_gv_khoa FOREIGN KEY (ma_khoa)
    REFERENCES khoa(ma_khoa) ON UPDATE CASCADE
);

-- ========================================================================================
--          BASIC M-M RELATIONSHIPS
-- ========================================================================================

CREATE TABLE IF NOT EXISTS giangvien_monhoc (
    ma_gv VARCHAR(50) NOT NULL,
    ma_mon VARCHAR(50) NOT NULL,
    ma_ky  VARCHAR(50) NOT NULL,

    PRIMARY KEY (ma_gv, ma_mon, ma_ky),

    CONSTRAINT fk_gvmhkh_gv FOREIGN KEY (ma_gv)
    REFERENCES giang_vien(ma_gv) ON UPDATE CASCADE,

    CONSTRAINT fk_gvmhkh_mn FOREIGN KEY (ma_mon)
    REFERENCES mon_hoc(ma_mon) ON UPDATE CASCADE,

    CONSTRAINT fk_gvmnkh_kh FOREIGN KEY (ma_ky)
    REFERENCES ky_hoc(ma_ky) ON UPDATE CASCADE
);

-- ===========================================================================================
--           KET QUA HOC TAP: 4 RELATIONSHIPS SINH VIEN - GIANG VIEN - MON HOC - KY HOC
-- ===========================================================================================

CREATE TABLE IF NOT EXISTS ket_qua_hoc_tap (
    ma_sv  VARCHAR(50) NOT NULL,
    ma_mon VARCHAR(50) NOT NULL,
    ma_ky  VARCHAR(50) NOT NULL,
    ma_gv  VARCHAR(50) NOT NULL,

    diem_qt DECIMAL(4,2) DEFAULT 0,
    diem_gk DECIMAL(4,2) DEFAULT 0,
    diem_ck DECIMAL(4,2) DEFAULT 0,

    -- cong thuc tinh diem
    diem_tong_ket DECIMAL(4,2) GENERATED ALWAYS AS
    (ROUND(diem_qt*0.3 + diem_gk*0.2 + diem_ck*0.5, 2)) STORED,
    PRIMARY KEY (ma_sv, ma_mon, ma_ky),

    -- foreign keys: ma sinh vien, ma mon, ma ky
    CONSTRAINT fk_kq_sv  FOREIGN KEY (ma_sv)
    REFERENCES sinh_vien(ma_sv) ON UPDATE CASCADE,

    CONSTRAINT fk_kq_mh  FOREIGN KEY (ma_mon)
    REFERENCES mon_hoc(ma_mon) ON UPDATE CASCADE,

    CONSTRAINT fk_kq_kh  FOREIGN KEY (ma_ky)
    REFERENCES ky_hoc(ma_ky) ON UPDATE CASCADE,

    -- ma giang vien: check valid
    CONSTRAINT fk_kq_gv_mh_kh FOREIGN KEY (ma_gv, ma_mon, ma_ky)
    REFERENCES giangvien_monhoc(ma_gv, ma_mon, ma_ky) ON UPDATE CASCADE
);

-- ====================================================================================
--         NGHIEN CUU KHOA HOC: 3 RELATIONSHIPS KY HOC - SINH VIEN - GIANG VIEN
-- ====================================================================================

CREATE TABLE IF NOT EXISTS nghien_cuu_khoa_hoc (
    ma_sv VARCHAR(50) NOT NULL,
    ma_gv VARCHAR(50) NOT NULL,
    ma_ky VARCHAR(50) NOT NULL,

    PRIMARY KEY (ma_sv, ma_gv, ma_ky),

    mo_ta         TEXT NOT NULL,
    ten_de_tai    VARCHAR(500) NOT NULL,
    trang_thai    VARCHAR(100) NOT NULL,
    ngay_dang_ky  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ket_qua       VARCHAR(100),
    file_dinh_kem TEXT,

    CONSTRAINT fk_nckh_sv FOREIGN KEY (ma_sv) REFERENCES sinh_vien(ma_sv) ON UPDATE CASCADE,
    CONSTRAINT fk_nckh_gv FOREIGN KEY (ma_gv) REFERENCES giang_vien(ma_gv) ON UPDATE CASCADE,
    CONSTRAINT fk_nckh_kh FOREIGN KEY (ma_ky) REFERENCES ky_hoc(ma_ky) ON UPDATE CASCADE
);

-- ====================================================================================
--                 BUSSINES LOGICS
-- ====================================================================================

CREATE TABLE IF NOT EXISTS users (
    username       VARCHAR(50) PRIMARY KEY,
    password_hash  VARCHAR(500) NOT NULL,
    role           ENUM('STUDENT','LECTURER','ADMIN') NOT NULL,
    ma_sv          VARCHAR(50),
    ma_gv          VARCHAR(50),
    enabled        BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_acc_sv FOREIGN KEY (ma_sv) REFERENCES sinh_vien(ma_sv)
    ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT fk_acc_gv FOREIGN KEY (ma_gv) REFERENCES giang_vien(ma_gv)
    ON UPDATE CASCADE ON DELETE SET NULL
);

-- ====================================================================================
--                                     VIEWS
-- ====================================================================================

-- Bảng điểm chi tiết cho sinh viên (JOIN môn học để lấy tên/TC)
CREATE OR REPLACE VIEW v_bang_diem_sinh_vien AS
SELECT
    kq.ma_sv,
    kq.ma_mon,
    mh.ten_mon,
    mh.so_tin_chi,
    kq.ma_ky,
    kq.diem_qt,
    kq.diem_gk,
    kq.diem_ck,
    kq.diem_tong_ket
FROM ket_qua_hoc_tap kq
JOIN mon_hoc mh ON mh.ma_mon = kq.ma_mon;

-- GPA theo từng kỳ
CREATE OR REPLACE VIEW v_gpa_tung_ky AS
SELECT
    kq.ma_sv,
    kq.ma_ky,
    ROUND(SUM(kq.diem_tong_ket * mh.so_tin_chi) / NULLIF(SUM(mh.so_tin_chi), 0), 2) AS gpa10,
    ROUND((SUM(kq.diem_tong_ket * mh.so_tin_chi) / NULLIF(SUM(mh.so_tin_chi), 0)) / 2.5, 2) AS gpa4
FROM ket_qua_hoc_tap kq
JOIN mon_hoc mh ON mh.ma_mon = kq.ma_mon
GROUP BY kq.ma_sv, kq.ma_ky;

-- GPA tích lũy toàn khóa
CREATE OR REPLACE VIEW v_gpa_tich_luy AS
SELECT
    kq.ma_sv,
    ROUND(SUM(kq.diem_tong_ket * mh.so_tin_chi) / NULLIF(SUM(mh.so_tin_chi), 0), 2) AS gpa10,
    ROUND((SUM(kq.diem_tong_ket * mh.so_tin_chi) / NULLIF(SUM(mh.so_tin_chi), 0)) / 2.5, 2) AS gpa4
FROM ket_qua_hoc_tap kq
JOIN mon_hoc mh ON mh.ma_mon = kq.ma_mon
GROUP BY kq.ma_sv;
