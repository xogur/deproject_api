package com.example.deproject_api.repository;

import com.example.deproject_api.domain.MusinsaAccount;
import com.example.deproject_api.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MusinsaAccountRepository extends JpaRepository<MusinsaAccount, Long> {

    // ✅ 특정 사용자(User)에 연결된 무신사 계정 조회
    Optional<MusinsaAccount> findByUser(User user);

    // 필요 시 ID 중복 확인 등의 메서드도 작성 가능
    boolean existsByUser(User user);
}
