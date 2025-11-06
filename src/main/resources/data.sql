USE universitydb;

INSERT IGNORE  INTO khoa(ma_khoa, ten_khoa, email, so_dien_thoai, van_phong) VALUES
    ('CNTT','Công nghệ thông tin','cntt@univ.edu','024-123456','Toà A2-301'),
    ('KT','Kinh tế','kt@univ.edu','024-765432','Toà B1-201');

INSERT IGNORE  INTO sinh_vien(ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, ma_khoa) VALUES
    ('SV001','Nguyễn Văn A','2004-03-12','M','a.sv@univ.edu','CNTT'),
    ('SV002','Trần Thị B','2004-08-21','F','b.sv@univ.edu','CNTT');

INSERT IGNORE  INTO giang_vien(ma_gv, ho_ten, gioi_tinh, email, ma_khoa) VALUES
    ('GV001','Phạm Minh K','M','k.gv@univ.edu','CNTT'),
    ('GV002','Lê Hồng M','F','m.gv@univ.edu','CNTT');

INSERT IGNORE  INTO ky_hoc(ma_ky, thong_tin, bat_dau, ket_thuc) VALUES
    ('2024-1','Học kỳ 1 năm 2024','2024-01-15','2024-05-30'),
    ('2024-2','Học kỳ 2 năm 2024','2024-08-15','2024-12-20');

INSERT IGNORE  INTO mon_hoc(ma_mon, ten_mon, so_tin_chi, ma_khoa) VALUES
    ('CT101','Nhập môn Lập trình',3,'CNTT'),
    ('CT201','Cấu trúc dữ liệu',4,'CNTT'),
    ('CT202','Cơ sở dữ liệu',3,'CNTT');

INSERT IGNORE  INTO monhoc_kyhoc(ma_mon, ma_ky) VALUES
    ('CT101','2024-1'),('CT201','2024-1'),('CT202','2024-2');

INSERT IGNORE  INTO giangvien_monhockyhoc(ma_gv, ma_mon, ma_ky) VALUES
    ('GV001','CT101','2024-1'),
    ('GV001','CT201','2024-1'),
    ('GV002','CT202','2024-2');

-- SV đăng ký và nhập điểm minh hoạ
INSERT IGNORE  INTO ket_qua_hoc_tap(ma_sv, ma_mon, ma_ky, diem_qt, diem_gk, diem_ck) VALUES
    ('SV001','CT101','2024-1',8.0,7.5,8.5),
    ('SV001','CT201','2024-1',7.0,7.0,7.0),
    ('SV002','CT101','2024-1',9.0,8.0,8.0);
