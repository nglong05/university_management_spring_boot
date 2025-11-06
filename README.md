Nguyen Van Long reports

6 steps of designing a database:

x.phan tich yeu cau: https://docs.google.com/document/d/1tKMUovY1yVVXCZ5H-p5NJbgDdx7cTUWDG1LEdmZJ9Ls
x.thiet ke muc khai niem: https://drive.google.com/file/d/1n2scHodDon_ts8Z6Kh7O_18sv4W4L-az/view
1.thiet ke muc logic: muc tieu la bien mo hinh khai niem => mo hinh quan he cu the (luoc do CSDL) aka i need to present the database into tables with key, vv. dong thoi lua chon he quan tri csdl (mysql)
2.cai tien luoc do: aka toi uu hoa logic da thiet ke, more on https://www.geeksforgeeks.org/dbms/normal-forms-in-dbms/. this problem will be reported more detailed
3.thiet ke csdl muc vat ly: this part is even more harder since it required the focus on real world thinking.
4.thiet ke an toan bao mat

------------------------------

# thiet ke muc logic

first thing first, i created a database `universitydb`

look at the `khoa` table. it consists of 5 fields

then the table `sinh_vien`. some specific fields: gioi_tinh set to the enum type; the foreign key fk_sv_khoa is set, on update cascade means if the value in the khoa table is changed, the value in the sinh_vien table is also updated

same goes with the `giang_vien` table


notice that `monhoc_kyhoc` is a relationship table:

- primary key (mamon maky) means 1 hoc ky chi co 1 mon hoc tuong ung
- create 2 foreign key: ma_mon -> mon_hoc; ma_ky -> ky_hoc (fk_mhk_mh and fk_mhk_kh)

same goes with the table `giangvien_monhoc`, notice that i dont link the giangvien table to the ky_hoc table but to the monhoc_kyhoc table

table `ket_qua_hoc_tap` 


university/
├─ src/main/java/com/example/university/
│   ├─ UniversityApplication.java
│   ├─ entity/
│   │   ├─ Department.java      (khoa)
│   │   ├─ Student.java
│   │   ├─ Lecturer.java
│   │   ├─ Semester.java
│   │   ├─ Course.java
│   │   ├─ key/StudyResultId.java
│   │   └─ StudyResult.java     (ket_qua_hoc_tap)
│   ├─ repository/
│   │   ├─ StudentRepository.java
│   │   ├─ StudyResultRepository.java
│   │   ├─ CourseRepository.java
│   │   └─ ViewsRepository.java (native view query)
│   ├─ dto/
│   │   ├─ TranscriptItemDTO.java
│   │   └─ GpaDTO.java
│   ├─ service/
│   │   └─ StudentService.java
│   └─ controller/
│       ├─ StudentController.java
│       └─ LecturerController.java
├─ src/main/resources/
│   ├─ application.properties
│   ├─ schema.sql
│   ├─ views_procs.sql
│   └─ data.sql
└─ pom.xml
