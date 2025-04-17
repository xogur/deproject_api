package com.example.deproject_api.repository;

import com.example.deproject_api.domain.AlertSetting;
import com.example.deproject_api.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlertSettingRepository extends JpaRepository<AlertSetting, Long> {
    Optional<AlertSetting> findByUser(User user);
}