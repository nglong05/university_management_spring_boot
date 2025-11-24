import random
from data import *
from utils import *

def generate_all_lecturers():

    all_lecturers = []

    for dept in departments:
        dept_code = dept["ma_khoa"]
        num_lecturers = random.randint(10, 15)
        print(f"generating {num_lecturers} lecturers for {dept['ten_khoa']} ({dept_code})")
        
        dept_lecturers = []
        for i in range(num_lecturers):
            ho = random.choice(ho_list)
            ten_dem = random.choice(ten_dem_list)
            ten = random.choice(ten_list)
            gender = "F" if ten_dem in ["Thị", "Thu", "Thùy"] else "M"
            dept_lecturers.append({
                "ho": ho,
                "ten_dem": ten_dem,
                "ten": ten,
                "gender": gender,
                "full_name": f"{ho} {ten_dem} {ten}"
            })
        
        dept_lecturers.sort(key=lambda x: remove_accents(x["ten"]))
        for idx, lecturer in enumerate(dept_lecturers, start=1):
            mgv = generate_lecturer_code(dept_code, idx)
            email = generate_lecturer_email(
                lecturer["ho"], 
                lecturer["ten_dem"], 
                lecturer["ten"], 
                dept_code, 
                idx
            )
            
            lecturer_data = {
                "maGiangVien": mgv,
                "hoTen": lecturer["full_name"],
                "ngaySinh": generate_birth_date(str(random.randint(0, 9)).zfill(2)),
                "gioiTinh": lecturer["gender"],
                "diaChi": generate_address(),
                "soDienThoai": generate_phone(),
                "email": email,
                "maKhoa": dept_code
            }
            all_lecturers.append(lecturer_data)    
    return all_lecturers
