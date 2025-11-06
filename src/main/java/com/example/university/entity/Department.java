package com.example.university.entity;

// JDBC https://viblo.asia/p/tuot-tuon-tuot-ve-jdbc-gAm5yjzEKdb; https://www.geeksforgeeks.org/java/introduction-to-jdbc/
// JPA: https://topdev.vn/blog/tong-quan-ve-jpa-java-persistence-api/
// equal and hashcode https://viblo.asia/p/phuong-thuc-equals-hashcode-trong-java-tim-hieu-chi-tiet-K9Vy8XyaLQR#_phuong-thuc-equals-trong-java-la-gi-0
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Department {

    @EqualsAndHashCode.Include
    private String id;

    private String name;
    private String email;
    private String phone;
    private String office;
}

//CREATE TABLE IF NOT EXISTS khoa (
//        ma_khoa       VARCHAR(10)  PRIMARY KEY,
//ten_khoa      VARCHAR(100) NOT NULL,
//email         VARCHAR(100) UNIQUE,
//so_dien_thoai VARCHAR(20),
//van_phong     VARCHAR(100)
//);