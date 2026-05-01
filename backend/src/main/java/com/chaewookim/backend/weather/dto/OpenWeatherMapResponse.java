package com.chaewookim.backend.weather.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenWeatherMapResponse(
        List<Weather> weather,
        Main main,
        String name
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Weather(int id, String main, String description, String icon) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Main(
            double temp,
            @JsonProperty("feels_like") double feelsLike,
            int humidity
    ) {
    }
}
