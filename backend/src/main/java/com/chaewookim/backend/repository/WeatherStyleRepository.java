package com.chaewookim.backend.repository;

import com.chaewookim.backend.entity.WeatherStyle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeatherStyleRepository extends JpaRepository<WeatherStyle, Long> {
}
