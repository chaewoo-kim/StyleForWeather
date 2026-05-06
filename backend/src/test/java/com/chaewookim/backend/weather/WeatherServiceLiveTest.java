package com.chaewookim.backend.weather;

import com.chaewookim.backend.weather.dto.CurrentWeather;
import com.chaewookim.backend.weather.dto.ForecastBundle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "OPENWEATHERMAP_API_KEY", matches = ".+")
class WeatherServiceLiveTest {

    @Autowired
    WeatherService weatherService;

    @Test
    @DisplayName("실제 API: 서울 현재 날씨를 도메인 객체로 받는다")
    void getByCity_seoul() {
        CurrentWeather weather = weatherService.getByCity("Seoul");

        assertThat(weather).isNotNull();
        assertThat(weather.locationName()).isEqualTo("Seoul");
        assertThat(weather.temperature()).isBetween(-30.0, 50.0);
        assertThat(weather.condition()).isNotNull();

        System.out.printf("[LIVE] Seoul: %.1f°C, %s%n",
                weather.temperature(), weather.condition());
    }

    @Test
    @DisplayName("실제 API: 좌표(서울 시청)로 현재 날씨를 받는다")
    void getByCoordinates_seoulCityHall() {
        CurrentWeather weather = weatherService.getByCoordinates(37.5665, 126.9780);

        assertThat(weather).isNotNull();
        assertThat(weather.temperature()).isBetween(-30.0, 50.0);
        assertThat(weather.condition()).isNotNull();

        System.out.printf("[LIVE] (37.57, 126.98): %.1f°C, %s%n",
                weather.temperature(), weather.condition());
    }

    @Test
    @DisplayName("실제 API: 서울 5일 예보를 일별 정오 슬롯으로 받는다")
    void getForecast_seoul() {
        ForecastBundle bundle = weatherService.getForecastByCity("Seoul");

        assertThat(bundle.locationName()).isEqualTo("Seoul");
        assertThat(bundle.daily()).hasSizeBetween(4, 5);
        bundle.daily().forEach(d -> {
            assertThat(d.date()).matches("\\d{4}-\\d{2}-\\d{2}");
            assertThat(d.temperature()).isBetween(-30.0, 50.0);
            assertThat(d.condition()).isNotNull();
        });

        System.out.println("[LIVE FORECAST] Seoul:");
        bundle.daily().forEach(d ->
                System.out.printf("  %s: %.1f°C, %s%n", d.date(), d.temperature(), d.condition()));
    }
}
