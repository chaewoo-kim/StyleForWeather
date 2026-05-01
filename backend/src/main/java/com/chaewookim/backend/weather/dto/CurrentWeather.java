package com.chaewookim.backend.weather.dto;

import com.chaewookim.backend.enums.WeatherCondition;

public record CurrentWeather(
        String locationName,
        double temperature,
        WeatherCondition condition
) {
}
