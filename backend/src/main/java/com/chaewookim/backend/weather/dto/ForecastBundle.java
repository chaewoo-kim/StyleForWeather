package com.chaewookim.backend.weather.dto;

import java.util.List;

public record ForecastBundle(
        String locationName,
        List<DailyForecast> daily
) {
}
