import os
import time
import requests
import csv
import random

BASE_URL = os.getenv("API_BASE_URL", "http://localhost:8080")
ADMIN = {
    "username": os.getenv("ADMIN_USER", "admin"),
    "password": os.getenv("ADMIN_PASS", "admin"),
}

def get_token():
    backoff = 2
    for attempt in range(1, 8):  # up to ~2+4+8+16+32+64+128=254s
        try:
            r = requests.post(BASE_URL + "/api/auth/login", json=ADMIN, timeout=10)
            if r.ok and r.json().get("accessToken"):
                return r.json().get("accessToken")
            else:
                print(f"[login] attempt {attempt} failed: {r.status_code} {r.text}")
        except Exception as e:
            print(f"[login] attempt {attempt} exception: {e}")
        time.sleep(backoff)
        backoff = min(backoff * 2, 60)
    raise SystemExit("Cannot obtain admin token; API not ready?")


def post_items(csv_path, url_path, transform):
    token = get_token()
    headers = {"Authorization": f"Bearer {token}"}

    with open(csv_path, newline='', encoding="utf-8") as f:
        reader = csv.DictReader(f)
        for row in reader:
            payload = transform(row)
            r = requests.post(BASE_URL + url_path, headers=headers, json=payload, timeout=15)
            print(f"[{random.random()}]", r.status_code)
            if r.status_code >= 300:
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
