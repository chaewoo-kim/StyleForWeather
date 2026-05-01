package com.chaewookim.backend.repository;

import com.chaewookim.backend.entity.WeatherStyle;
import com.chaewookim.backend.enums.WeatherCondition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WeatherStyleRepository extends JpaRepository<WeatherStyle, Long> {

    Optional<WeatherStyle> findFirstByMinTempLessThanEqualAndMaxTempGreaterThanEqualAndWeatherCondition(
            Integer minTemp, Integer maxTemp, WeatherCondition weatherCondition);
}
