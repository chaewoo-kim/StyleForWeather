package com.chaewookim.backend.entity;

import com.chaewookim.backend.enums.WeatherCondition;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "weather_style")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherStyle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "min_temp", nullable = false)
    private Integer minTemp;

    @Column(name = "max_temp", nullable = false)
    private Integer maxTemp;

    @Enumerated(EnumType.STRING)
    @Column(name = "weather_condition", nullable = false, length = 20)
    private WeatherCondition weatherCondition;
}
