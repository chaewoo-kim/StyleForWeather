package com.chaewookim.backend.forecast;

import com.chaewookim.backend.enums.Gender;
import com.chaewookim.backend.enums.Style;
import com.chaewookim.backend.recommend.RecommendService;
import com.chaewookim.backend.weather.WeatherService;
import com.chaewookim.backend.weather.dto.DailyForecast;
import com.chaewookim.backend.weather.dto.DailyForecastWithRecommendations;
import com.chaewookim.backend.weather.dto.ForecastBundle;
import com.chaewookim.backend.weather.dto.WeeklyForecast;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ForecastService {

    private final WeatherService weatherService;
    private final RecommendService recommendService;

    public ForecastService(WeatherService weatherService, RecommendService recommendService) {
        this.weatherService = weatherService;
        this.recommendService = recommendService;
    }

    public WeeklyForecast byCoordinates(double lat, double lon, Gender gender, Style style) {
        return assemble(weatherService.getForecastByCoordinates(lat, lon), gender, style);
    }

    public WeeklyForecast byCity(String city, Gender gender, Style style) {
        return assemble(weatherService.getForecastByCity(city), gender, style);
    }

    private WeeklyForecast assemble(ForecastBundle bundle, Gender gender, Style style) {
        List<DailyForecastWithRecommendations> withRecs = bundle.daily().stream()
                .map(d -> withRecommendations(d, gender, style))
                .toList();
        return new WeeklyForecast(bundle.locationName(), withRecs);
    }

    private DailyForecastWithRecommendations withRecommendations(DailyForecast d, Gender gender, Style style) {
        int temp = (int) Math.round(d.temperature());
        return new DailyForecastWithRecommendations(
                d.date(),
                d.temperature(),
                d.condition(),
                recommendService.recommend(temp, d.condition(), gender, style)
        );
    }
}
