package com.chaewookim.backend.weather;

import com.chaewookim.backend.enums.WeatherCondition;
import com.chaewookim.backend.weather.dto.CurrentWeather;
import com.chaewookim.backend.weather.dto.OpenWeatherMapResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.function.Function;

@Service
public class WeatherService {

    private final OpenWeatherMapProperties properties;
    private final RestClient restClient;

    public WeatherService(OpenWeatherMapProperties properties, RestClient.Builder builder) {
        this.properties = properties;
        this.restClient = builder.baseUrl(properties.baseUrl()).build();
    }

    public CurrentWeather getByCoordinates(double lat, double lon) {
        return call(uri -> uri
                .path("/weather")
                .queryParam("lat", lat)
                .queryParam("lon", lon));
    }

    public CurrentWeather getByCity(String city) {
        return call(uri -> uri
                .path("/weather")
                .queryParam("q", city));
    }

    private CurrentWeather call(Function<org.springframework.web.util.UriBuilder,
            org.springframework.web.util.UriBuilder> uriCustomizer) {
        OpenWeatherMapResponse response = restClient.get()
                .uri(uriBuilder -> uriCustomizer.apply(uriBuilder)
                        .queryParam("units", "metric")
                        .queryParam("lang", "kr")
                        .queryParam("appid", properties.apiKey())
                        .build())
                .retrieve()
                .body(OpenWeatherMapResponse.class);
        return toDomain(response);
    }

    private CurrentWeather toDomain(OpenWeatherMapResponse response) {
        if (response == null || response.weather() == null || response.weather().isEmpty()) {
            throw new IllegalStateException("OpenWeatherMap response is empty");
        }
        return new CurrentWeather(
                response.name(),
                response.main().temp(),
                mapCondition(response.weather().get(0).main())
        );
    }

    private WeatherCondition mapCondition(String openWeatherMain) {
        return Arrays.stream(WeatherCondition.values())
                .filter(c -> c.name().equalsIgnoreCase(openWeatherMain))
                .findFirst()
                .orElse(WeatherCondition.CLEAR);
    }
}
