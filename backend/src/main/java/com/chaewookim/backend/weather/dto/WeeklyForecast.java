package com.chaewookim.backend.weather.dto;

import java.util.List;

public record WeeklyForecast(
        String locationName,
        List<DailyForecastWithRecommendations> daily
) {
}
