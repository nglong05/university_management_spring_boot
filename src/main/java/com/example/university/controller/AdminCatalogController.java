package com.example.university.controller;

import com.example.university.dto.AdminCatalog.*;
import com.example.university.service.AdminCatalogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/catalog")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasRole('ADMIN')")
public class AdminCatalogController {

    private final AdminCatalogService svc;

    /* --------- KHOA --------- */
    @PostMapping("/departments")
    public Map<String,Object> upsertDepartment(@Valid @RequestBody Dept body) {
        return Map.of("affected", svc.upsertDepartment(body));
    }

    @PostMapping("/departments/batch")
    public Map<String,Object> upsertDepartments(@Valid @RequestBody List<Dept> body) {
        return Map.of("affected", svc.upsertDepartments(body), "count", body.size());
    }

    /* --------- NGÀNH HỌC --------- */
    @PostMapping("/majors")
    public Map<String,Object> upsertMajor(@Valid @RequestBody Major body) {
        return Map.of("affected", svc.upsertMajor(body));
    }

    @PostMapping("/majors/batch")
    public Map<String,Object> upsertMajors(@Valid @RequestBody List<Major> body) {
        return Map.of("affected", svc.upsertMajors(body), "count", body.size());
    }

    /* --------- KỲ HỌC --------- */
    @PostMapping("/semesters")
    public Map<String,Object> upsertSemester(@Valid @RequestBody Semester body) {
        return Map.of("affected", svc.upsertSemester(body));
    }

    @PostMapping("/semesters/batch")
    public Map<String,Object> upsertSemesters(@Valid @RequestBody List<Semester> body) {
        return Map.of("affected", svc.upsertSemesters(body), "count", body.size());
    }

    /* --------- MÔN HỌC --------- */
    @PostMapping("/courses")
    public Map<String,Object> upsertCourse(@Valid @RequestBody Course body) {
        return Map.of("affected", svc.upsertCourse(body));
    }

    @PostMapping("/courses/batch")
    public Map<String,Object> upsertCourses(@Valid @RequestBody List<Course> body) {
        return Map.of("affected", svc.upsertCourses(body), "count", body.size());
    }

    /* --------- SINH VIÊN --------- */
    @PostMapping("/students")
    public Map<String,Object> upsertStudent(@Valid @RequestBody Student body) {
        return Map.of("affected", svc.upsertStudent(body));
    }

    @PostMapping("/students/batch")
    public Map<String,Object> upsertStudents(@Valid @RequestBody List<Student> body) {
        return Map.of("affected", svc.upsertStudents(body), "count", body.size());
    }

    /* --------- GIẢNG VIÊN --------- */
    @PostMapping("/lecturers")
    public Map<String,Object> upsertLecturer(@Valid @RequestBody Lecturer body) {
        return Map.of("affected", svc.upsertLecturer(body));
    }

    @PostMapping("/lecturers/batch")
    public Map<String,Object> upsertLecturers(@Valid @RequestBody List<Lecturer> body) {
        return Map.of("affected", svc.upsertLecturers(body), "count", body.size());
    }

    /* --------- GIẢNG VIÊN - MÔN - KỲ HỌC --------- */
    @PostMapping("/lecturer-course-semesters")
    public Map<String,Object> upsertLecturerCourseSemester(@Valid @RequestBody LecturerCourseSemester body) {
        return Map.of("affected", svc.upsertLecturerCourseSemester(body));
    }

    @PostMapping("/lecturer-course-semesters/batch")
    public Map<String,Object> upsertLecturerCourseSemesters(@Valid @RequestBody List<LecturerCourseSemester> body) {
        return Map.of("affected", svc.upsertLecturerCourseSemesters(body), "count", body.size());
    }

    /* --------- KẾT QUẢ HỌC TẬP --------- */
    @PostMapping("/study-results")
    public Map<String,Object> upsertStudyResult(@Valid @RequestBody StudyResult body) {
        return Map.of("affected", svc.upsertStudyResult(body));
    }

    @PostMapping("/study-results/batch")
    public Map<String,Object> upsertStudyResults(@Valid @RequestBody List<StudyResult> body) {
        return Map.of("affected", svc.upsertStudyResults(body), "count", body.size());
    }

    /* --------- NGHIÊN CỨU KHOA HỌC --------- */
    @PostMapping("/research-projects")
    public Map<String,Object> upsertResearchProject(@Valid @RequestBody ResearchProject body) {
        return Map.of("affected", svc.upsertResearchProject(body));
    }

    @PostMapping("/research-projects/batch")
    public Map<String,Object> upsertResearchProjects(@Valid @RequestBody List<ResearchProject> body) {
        return Map.of("affected", svc.upsertResearchProjects(body), "count", body.size());
    }

    /* --------- USERS --------- */
    @PostMapping("/users")
    public Map<String, Object> createUser(@Valid @RequestBody CreateUser body) {
        return Map.of("affected", svc.createUser(body));
    }

    @PostMapping("/users/batch")
    public Map<String, Object> createUsers(@Valid @RequestBody List<CreateUser> body) {
        return Map.of("affected", svc.createUsers(body), "count", body.size());
    }

    /* health check */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(Map.of("status","ok"));
    }
}
