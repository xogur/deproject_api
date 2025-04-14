package com.example.deproject_api.repository;

import com.example.deproject_api.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 이메일로 사용자 조회
    Optional<User> findByEmail(String email);

    // 이메일 중복 여부 확인 (회원가입용)
    boolean existsByEmail(String email);
}