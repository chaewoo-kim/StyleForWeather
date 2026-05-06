package com.chaewookim.backend.weather.dto;

import com.chaewookim.backend.enums.WeatherCondition;

public record DailyForecast(
        String date,
        double temperature,
        WeatherCondition condition
) {
}
