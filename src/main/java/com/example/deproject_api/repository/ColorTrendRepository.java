// src/main/java/com/example/deproject_api/repository/ColorTrendRepository.java
package com.example.deproject_api.repository;

import com.example.deproject_api.domain.ColorTrend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ColorTrendRepository extends JpaRepository<ColorTrend, Long> {
}