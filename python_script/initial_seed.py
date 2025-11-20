import requests
import csv
import random

BASE_URL = "http://localhost:8080"
ADMIN = {"username": "admin", "password": "admin"}

def get_token():
    r = requests.post(BASE_URL + "/api/auth/login", json=ADMIN)
    token = r.json().get("accessToken")
    return token


def post_items(csv_path, url_path, transform):
    token = get_token()
    headers = {"Authorization": f"Bearer {token}"}

    with open(csv_path, newline='', encoding="utf-8") as f:
        reader = csv.DictReader(f)
        for row in reader:
            payload = transform(row)
            r = requests.post(BASE_URL + url_path, headers=headers, json=payload)
            print(f"[{random.random()}]",r.status_code)
            if (r.status_code != 200): # debug
                print(r.text, payload)


def seed_departments():
    def trans(row):
        return {
            "maKhoa": row.get("ma_khoa"),
            "tenKhoa": row.get("ten_khoa"),
            "email": row.get("email") if row.get("email") else None,
            "soDienThoai": row.get("so_dien_thoai"),
            "vanPhong": row.get("van_phong"),
        }
    post_items("data/khoa.csv", "/api/admin/catalog/departments", trans)


def seed_nganh():
    def trans(row):
        return {
            "maNganh": row.get("maNganh"),
            "tenNganh": row.get("tenNganh"),
            "maKhoa": row.get("maKhoa")
        }
    post_items("data/nganh.csv", "/api/admin/catalog/majors", trans)


def seed_kyhoc():
    def trans(row):
        return {
            "maKy": row.get("ma_ky"),
            "thongTin": row.get("thong_tin"),
            "ngayBatDau": row.get("bat_dau"),
            "ngayKetThuc": row.get("ket_thuc")
        }
    post_items("data/ky_hoc.csv", "/api/admin/catalog/semesters", trans)


def seed_monhoc():
    def trans(row):
        return {
            "maMon": row.get("ma_mon"),
            "tenMon": row.get("ten_mon"),
            "soTinChi": int(row.get("so_tin_chi")),
            "maKhoa": row.get("ma_khoa")
        }
    post_items("data/mon_hoc.csv", "/api/admin/catalog/courses", trans)


if __name__ == "__main__":
    seed_departments()
    seed_nganh()
    seed_kyhoc()
    seed_monhoc()
