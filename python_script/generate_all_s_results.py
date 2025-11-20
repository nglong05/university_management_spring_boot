import csv
import random
from data import *
from utils import *

def generate_all_s_results(all_students, all_lecturers_courses, csv_course_path, csv_semester="data/ky_hoc.csv"):
    all_s_results = []

    # Load courses
    courses = []
    with open(csv_course_path, newline='', encoding="utf-8") as f:
        reader = csv.DictReader(f)
        for row in reader:
            courses.append(row)

    # Load semesters
    semesters = []
    with open(csv_semester, newline='', encoding="utf-8") as f:
        reader = csv.DictReader(f)
        for row in reader:
            semesters.append(row["ma_ky"])

    for s in all_students:
        maSinhVien = s["maSinhVien"]
        maKhoa = s["maKhoa"]

        # Random number of courses for the student
        courses_major_number = random.randint(2, 4)

        # Filter courses for student's major
        major_courses = [c for c in courses if c["ma_khoa"] == maKhoa]
        if not major_courses:
            continue

        selected_courses = random.sample(major_courses, min(courses_major_number, len(major_courses)))

        for course in selected_courses:
            maMon = course["ma_mon"]

            # Randomly pick a semester
            random.shuffle(semesters)
            for maKy in semesters:
                # Filter lecturers that actually teach this course in this semester
                lecturers_for_course = [
                    gv for gv in all_lecturers_courses
                    if gv["maMon"] == maMon and gv["maKy"] == maKy
                ]
                if lecturers_for_course:
                    gv_selected = random.choice(lecturers_for_course)
                    all_s_results.append({
                        "maSinhVien": maSinhVien,
                        "maMon": maMon,
                        "maKy": maKy,
                        "maGiangVien": gv_selected["maGiangVien"],
                        "diemQuaTrinh": random.randint(2, 10),
                        "diemGiuaKy": random.randint(2, 10),
                        "diemCuoiKy": random.randint(2, 10)
                    })
                    break  # Stop looking for semesters once a valid lecturer is found

    return all_s_results
