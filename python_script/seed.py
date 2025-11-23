import os
import requests
import random
from initial_seed import get_token
from generate_all_students import generate_all_students
from generate_all_lecturers import generate_all_lecturers
from generate_all_l_courses import generate_all_l_courses
from generate_all_s_results import generate_all_s_results

BASE_URL = os.getenv("API_BASE_URL", "http://localhost:8080")
headers = {"Authorization": f"Bearer {get_token()}", "Accept": "application/json"}
all_students = generate_all_students()
all_lecturers = generate_all_lecturers()

students_payload = []
lecturers_payload = []
users_payload = []
lecturers_courses_payload = generate_all_l_courses(all_lecturers, "data/mon_hoc.csv")
student_results = generate_all_s_results(all_students, lecturers_courses_payload, "data/mon_hoc.csv")

for student in all_students:
    students_payload.append({
        "maSinhVien": student["maSinhVien"],
        "hoTen": student["hoTen"],
        "ngaySinh": student["ngaySinh"],
        "gioiTinh": student["gioiTinh"],
        "diaChi": student["diaChi"],
        "soDienThoai": student["soDienThoai"],
        "email": student["email"],
        "maKhoa": student["maKhoa"],
        "maNganhHoc": student["maNganhHoc"]
    })

    users_payload.append({
        "username": student["maSinhVien"],
        "password": "123456",
        "role": "STUDENT",
        "maSv": student["maSinhVien"],
        "enabled": True
    })

for lecturer in all_lecturers:
    lecturers_payload.append({
        "maGiangVien": lecturer["maGiangVien"],
        "hoTen": lecturer["hoTen"],
        "ngaySinh": lecturer["ngaySinh"],
        "gioiTinh": lecturer["gioiTinh"],
        "diaChi": lecturer["diaChi"],
        "soDienThoai": lecturer["soDienThoai"],
        "email": lecturer["email"],
        "maKhoa": lecturer["maKhoa"]
    })

    users_payload.append({
        "username": lecturer["maGiangVien"],
        "password": "123456",
        "role": "LECTURER",
        "maGv": lecturer["maGiangVien"],
        "enabled": True
    })

try:
    r = requests.post(BASE_URL + "/api/admin/catalog/students/batch", headers=headers, json=students_payload)
    print("Students batch:", r.status_code, r.text)
except Exception as e:
    print("Exception sending students batch:", str(e))

try:
    r = requests.post(BASE_URL + "/api/admin/catalog/lecturers/batch", headers=headers, json=lecturers_payload)
    print("Lecturers batch:", r.status_code, r.text)
except Exception as e:
    print("Exception sending lecturers batch:", str(e))

try:
    r = requests.post(BASE_URL + "/api/admin/catalog/lecturer-course-semesters/batch", headers=headers, json=lecturers_courses_payload)
    print("courses batch:", r.status_code, r.text)
except Exception as e:
    print("Exception sending courses batch:", str(e))

try:
    r = requests.post(BASE_URL + "/api/admin/catalog/study-results/batch", headers=headers, json=student_results)
    print("results batch:", r.status_code, r.text)
except Exception as e:
    print("Exception sending results batch:", str(e))


try:
    print("[+] this gonna take a long time since we need to wait for the brcypt for thousand of users")
    r = requests.post(BASE_URL + "/api/admin/catalog/users/batch", headers=headers, json=users_payload)
    print("Users batch:", r.status_code, r.text)
except Exception as e:
    print("Exception sending users batch:", str(e))
