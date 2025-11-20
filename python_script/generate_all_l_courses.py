import csv
import random
from data import *
from utils import *

def generate_all_l_courses(all_lecturers, csv_course, csv_semester="data/ky_hoc.csv"):
    all_l_courses = []
    semesters = []

    with open(csv_semester, newline='', encoding="utf-8") as f:
        sem_reader = csv.DictReader(f)
        for row in sem_reader:
            semesters.append(row["ma_ky"])

    with open(csv_course, newline='', encoding="utf-8") as f:
        reader = csv.DictReader(f)
        for row in reader:
            ma_mon = row["ma_mon"]

            for lec in all_lecturers:
                if lec["maGiangVien"][2:4] == ma_mon[:2]:
                    all_l_courses.append({
                        "maGiangVien": lec["maGiangVien"],
                        "maMon": ma_mon,
                        "maKy": random.choice(semesters)
                    })

    return all_l_courses
