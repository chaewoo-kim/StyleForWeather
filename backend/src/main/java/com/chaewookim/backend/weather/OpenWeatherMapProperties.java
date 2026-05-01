package com.chaewookim.backend.weather;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "openweathermap")
public record OpenWeatherMapProperties(String apiKey, String baseUrl) {
}
