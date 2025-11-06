USE universitydb;

-- Hàm quy đổi 10 -> 4 (có thể tinh chỉnh theo quy chế)
DROP FUNCTION IF EXISTS fn_diem10_to4;
CREATE FUNCTION fn_diem10_to4(d10 DECIMAL(4,2))
    RETURNS DECIMAL(3,2) DETERMINISTIC
    RETURN CASE
               WHEN d10 >= 8.5 THEN 4.0
               WHEN d10 >= 8.0 THEN 3.7
               WHEN d10 >= 7.0 THEN 3.0
               WHEN d10 >= 6.5 THEN 2.5
               WHEN d10 >= 5.5 THEN 2.0
               WHEN d10 >= 5.0 THEN 1.5
               WHEN d10 >= 4.0 THEN 1.0
               ELSE 0.0
        END;

-- Bảng điểm chi tiết (view)
CREATE OR REPLACE VIEW v_bang_diem_sinh_vien AS
SELECT
    kq.ma_sv, sv.ho_ten,
    kq.ma_ky, kh.thong_tin,
    kq.ma_mon, mh.ten_mon, mh.so_tin_chi,
    kq.diem_qt, kq.diem_gk, kq.diem_ck, kq.diem_tong_ket,
    fn_diem10_to4(kq.diem_tong_ket) AS diem_he4
FROM ket_qua_hoc_tap kq
         JOIN sinh_vien sv ON sv.ma_sv = kq.ma_sv
         JOIN mon_hoc   mh ON mh.ma_mon = kq.ma_mon
         JOIN ky_hoc    kh ON kh.ma_ky  = kq.ma_ky;

-- GPA theo kỳ (10 và 4)
CREATE OR REPLACE VIEW v_gpa_tung_ky AS
SELECT
    ma_sv,
    ma_ky,
    ROUND(SUM(diem_tong_ket * so_tin_chi) / NULLIF(SUM(so_tin_chi),0), 2) AS gpa10,
    ROUND(SUM(fn_diem10_to4(diem_tong_ket) * so_tin_chi) / NULLIF(SUM(so_tin_chi),0), 2) AS gpa4
FROM v_bang_diem_sinh_vien
GROUP BY ma_sv, ma_ky;

-- GPA tích luỹ toàn khoá
CREATE OR REPLACE VIEW v_gpa_tich_luy AS
SELECT
    ma_sv,
    ROUND(SUM(diem_tong_ket * so_tin_chi) / NULLIF(SUM(so_tin_chi),0), 2) AS gpa10,
    ROUND(SUM(fn_diem10_to4(diem_tong_ket) * so_tin_chi) / NULLIF(SUM(so_tin_chi),0), 2) AS gpa4
FROM v_bang_diem_sinh_vien
GROUP BY ma_sv;
