import requests

url = "http://localhost:8080"

admin_cre = {"username": "admin", "password": "admin"}
r = requests.post(url + "/api/auth/login", json=admin_cre)
accessToken = r.json().get("accessToken")
print(accessToken)

headers = {
    "Authorization": f"Bearer {accessToken}"  
}

sample_department = {
  "maKhoa": "ATTT",
  "tenKhoa": "An Toàn Thông Tin",
  "email": "ptit.attt@ptit.edu.vn",
  "soDienThoai": "0912876423",
  "vanPhong": "Phòng 101 - Tầng 10, Tòa A2, Cơ sở miền Bắc PTIT"
}
r = requests.post(url + "/api/admin/catalog/departments", headers=headers,json=sample_department)
print(r.text)


sample_major = {
  "maNganh": "RED",
  "tenNganh": "Attacker",
  "maKhoa": "ATTT"
}
r = requests.post(url + "/api/admin/catalog/majors", headers=headers, json=sample_major)
print(r.text)

sample_semester = {
  "maKy": "2025-2",
  "thongTin": "Học kỳ 2 năm 2025,2025-08-15,2025-12-30"
}
r = requests.post(url + "/api/admin/catalog/semesters", headers=headers, json=sample_semester)
print(r.text)

sample_c = {
  "maMon": "RED001",
  "tenMon": "Web Exploitation",
  "soTinChi": 3,
  "maKhoa": "ATTT"
}
r = requests.post(url + "/api/admin/catalog/courses", headers=headers, json=sample_c)
print(r.text)

sample_sv = {
  "maSinhVien": "B23DCAT174",
  "hoTen": "Nguyễn Văn Long",
  "ngaySinh": "2005-06-30",
  "gioiTinh": "M",
  "diaChi": "tổ 4 khu 1 phường Việt Hưng tỉnh Quảng Ninh",
  "soDienThoai": "0382217003",
  "email": "longnv.b23at174.com",
  "maKhoa": "ATTT",
  "maNganhHoc": "RED"
}
r = requests.post(url + "/api/admin/catalog/students", headers=headers, json=sample_sv)
print(r.text)

sample_user = {
    "username": "longnv",
    "password": "1234",
    "role": "STUDENT",
    "maSv": "B23DCAT174",
    "enabled": True
}

r = requests.post(url + "/api/admin/catalog/users", headers=headers, json=sample_user)
print(r.text)


sample_lt = {
  "maGiangVien": "GV01",
  "hoTen": "Liễu Như Yên",
  "ngaySinh": "2000-11-13",
  "gioiTinh": "F",
  "diaChi": "Số 15, ngách 2, ngõ 10, đường Nguyễn Trãi, phường Thanh Xuân Trung, quận Thanh Xuân",
  "soDienThoai": "09138742334",
  "email": "lnt.sample@lt.ptit.edu.vn",
  "maKhoa": "ATTT"
}

r = requests.post(url + "/api/admin/catalog/lecturers", headers=headers, json=sample_lt)
print(r.text)

sample_user2 = {
    "username": "yenln",
    "password": "1234",
    "role": "LECTURER",
    "maGv": "GV01",
    "enabled": True
}

r = requests.post(url + "/api/admin/catalog/users", headers=headers, json=sample_user2)
print(r.text)

sample_l_c_s = {
  "maGiangVien": "GV01",
  "maMon": "RED001",
  "maKy": "2025-2"
}

r = requests.post(url + "/api/admin/catalog/lecturer-course-semesters", headers=headers, json=sample_l_c_s)
print(r.text)

sample_study_res = {
  "maSinhVien": "B23DCAT174",
  "maMon": "RED001",
  "maKy": "2025-2",
  "maGiangVien": "GV01",
  "diemQuaTrinh": 10,
  "diemGiuaKy": 10,
  "diemCuoiKy": 10
}

r = requests.post(url + "/api/admin/catalog/study-results", headers=headers, json=sample_study_res)
print(r.text)

sample_rs = {
  "maSinhVien": "B23DCAT174",
  "maGiangVien": "GV01",
  "maKy": "2025-2",
  "moTa": "Hardenings on AWS security",
  "tenDeTai": "AWSSec",
  "trangThai": "accepted by me",
  "ngayDangKy": "2025-11-13",
  "ketQua": "PASS",
  "fileDinhKem": "http://localhost:8080"
}

r = requests.post(url + "/api/admin/catalog/research-projects", headers=headers, json=sample_rs)
print(r.text)