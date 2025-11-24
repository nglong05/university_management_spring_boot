import random
from data import *
from utils import *


def generate_all_students():

    entry_year = "23"
    all_students = []

    for dept in departments:
        dept_code = dept["ma_khoa"]
        num_students = random.randint(1800, 2000)
        print(f"generating {num_students} students for {dept['ten_khoa']} ({dept_code})")
        
        dept_students = []
        for i in range(num_students):
            ho = random.choice(ho_list)
            ten_dem = random.choice(ten_dem_list)
            ten = random.choice(ten_list)
            gender = "F" if ten_dem in ["Thị", "Thu", "Thùy"] else "M"
            dept_students.append({
                "ho": ho,
                "ten_dem": ten_dem,
                "ten": ten,
                "gender": gender,
                "full_name": f"{ho} {ten_dem} {ten}"
            })
        
        dept_students.sort(key=lambda x: remove_accents(x["ten"]))
        for idx, student in enumerate(dept_students, start=1):
            msv = generate_student_code(entry_year, dept_code, idx)
            email = generate_email(
                student["ho"], 
                student["ten_dem"], 
                student["ten"], 
                entry_year, 
                dept_code, 
                idx,
                region='b'  # miền bắc
            )
            
            major = random.choice(majors.get(dept_code, [dept_code]))        
            student_data = {
                "maSinhVien": msv,
                "hoTen": student["full_name"],
                "ngaySinh": generate_birth_date(entry_year),
                "gioiTinh": student["gender"],
                "diaChi": generate_address(),
                "soDienThoai": generate_phone(),
                "email": email,
                "maKhoa": dept_code,
                "maNganhHoc": major
            }
            all_students.append(student_data)    
    return all_students
