package com.chaewookim.backend.weather.dto;

import com.chaewookim.backend.enums.WeatherCondition;
import com.chaewookim.backend.recommend.Recommendation;

import java.util.List;

public record DailyForecastWithRecommendations(
        String date,
        double temperature,
        WeatherCondition condition,
        List<Recommendation> recommendations
) {
}
