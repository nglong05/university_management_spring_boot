package com.example.university.service;

import com.example.university.dto.AdminCatalog.*;
import com.example.university.repository.AdminCatalogJdbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Tầng nghiệp vụ: điều phối repository, có thể thêm validate/ràng buộc bổ sung nếu cần.
 * Để đơn giản, hiện giữ mỏng; đã validate cơ bản ở DTO (JSR-380).
 */
@Service
@RequiredArgsConstructor
public class AdminCatalogService {

    private final AdminCatalogJdbcRepository repo;
    private final org.springframework.security.crypto.password.PasswordEncoder encoder;

    /* --- KHOA --- */
    @Transactional
    public int upsertDepartment(Dept d) { return repo.upsertDepartment(d); }
    @Transactional
    public int upsertDepartments(List<Dept> list) { return repo.upsertDepartments(list); }

    /* --- NGÀNH HỌC --- */
    @Transactional
    public int upsertMajor(Major m) { return repo.upsertMajor(m); }
    @Transactional
    public int upsertMajors(List<Major> list) { return repo.upsertMajor(list); }

    /* --- KỲ HỌC --- */
    @Transactional
    public int upsertSemester(Semester s) { return repo.upsertSemester(s); }
    @Transactional
    public int upsertSemesters(List<Semester> list) { return repo.upsertSemesters(list); }

    /* --- MÔN HỌC --- */
    @Transactional
    public int upsertCourse(Course c) { return repo.upsertCourse(c); }
    @Transactional
    public int upsertCourses(List<Course> list) { return repo.upsertCourses(list); }

    /* --- SINH VIÊN --- */
    @Transactional
    public int upsertStudent(Student s) { return repo.upsertStudent(s); }
    @Transactional
    public int upsertStudents(List<Student> list) { return repo.upsertStudents(list); }

    /* --- GIẢNG VIÊN --- */
    @Transactional
    public int upsertLecturer(Lecturer l) { return repo.upsertLecturer(l); }
    @Transactional
    public int upsertLecturers(List<Lecturer> list) { return repo.upsertLecturers(list); }

    /* --- GIẢNG VIÊN - MÔN - KỲ HỌC --- */
    @Transactional
    public int upsertLecturerCourseSemester(LecturerCourseSemester lcs) { return repo.upsertLecturerCourseSemester(lcs); }
    @Transactional
    public int upsertLecturerCourseSemesters(List<LecturerCourseSemester> list) { return repo.upsertLecturerCourseSemesters(list); }

    /* --- KẾT QUẢ HỌC TẬP --- */
    @Transactional
    public int upsertStudyResult(StudyResult sr) { return repo.upsertStudyResult(sr); }
    @Transactional
    public int upsertStudyResults(List<StudyResult> list) { return repo.upsertStudyResults(list); }

    /* --- NGHIÊN CỨU KHOA HỌC --- */
    @Transactional
    public int upsertResearchProject(ResearchProject rp) { return repo.upsertResearchProject(rp); }
    @Transactional
    public int upsertResearchProjects(List<ResearchProject> list) { return repo.upsertResearchProjects(list); }

    /* --- USERS --- */
    @Transactional
    public int createUser(CreateUser u) {
        validateRoleBinding(u.role(), u.maSv(), u.maGv());
        String hash = encoder.encode(u.password()); // -> {bcrypt}$2a$10...
        return repo.upsertUsers(new Users(
                u.username(), hash, u.role(), u.maSv(), u.maGv(), u.enabled()
        ));
    }

    @Transactional
    public int createUsers(List<CreateUser> list) {
        List<Users> hashed = list.stream().map(u -> {
            validateRoleBinding(u.role(), u.maSv(), u.maGv());
            return new Users(
                    u.username(), encoder.encode(u.password()),
                    u.role(), u.maSv(), u.maGv(), u.enabled()
            );
        }).toList();
        return repo.upsertUserss(hashed);
    }

    private static void validateRoleBinding(String role, String maSv, String maGv) {
        switch (role) {
            case "STUDENT"  -> { if (maSv == null || ! (maGv == null)) throw new IllegalArgumentException("role=STUDENT yêu cầu maSv != null và maGv == null"); }
            case "LECTURER" -> { if (maGv == null || ! (maSv == null)) throw new IllegalArgumentException("role=LECTURER yêu cầu maGv != null và maSv == null"); }
            case "ADMIN"    -> { if (maSv != null || maGv != null) throw new IllegalArgumentException("role=ADMIN không gắn maSv/maGv"); }
            default -> throw new IllegalArgumentException("role không hợp lệ");
        }
    }
}
