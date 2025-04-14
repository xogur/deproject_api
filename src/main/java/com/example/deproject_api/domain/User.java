package com.example.deproject_api.domain;

import jakarta.persistence.*;
import lombok.*;

// JPA엔티티 임일 나타냄
@Entity
// Lombok이 자동으로 getter/setter 생성
@Getter
@Setter
//기본 생성자 생성, JPA는 기본적으로 생성자가 있어야 동작을 하기때문
@NoArgsConstructor
//모든 필드를 파라미터로 받는 생성자 생성, 원래는 this.name = name 이런식으로 해줘야 하는데 자동으로 매핑해줌
@AllArgsConstructor
//DB의 users테이블과 매핑
@Table(name = "users")
//빌더 패턴으로 객체 생성 가능하게 해줌, 밑에 코드처럼 가독성이 증가
//User user = User.builder()
//        .name("김코딩")
//        .email("kim@coding.com")
//        .password("abcd1234")
//        .build();
@Builder
public class User {
    // 기본키 지정
    @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY) : MySQL/PostgreSQL 등의 DB에서 자동 증가(AUTO_INCREMENT) 되도록 설정
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    //유니크 제약 조건 설정
    @Column(unique = true)
    private String email;

    private String password;
}