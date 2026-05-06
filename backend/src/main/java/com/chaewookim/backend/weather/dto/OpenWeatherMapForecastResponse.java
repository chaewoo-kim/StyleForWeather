package com.chaewookim.backend.weather.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenWeatherMapForecastResponse(
        List<Item> list,
        City city
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Item(
            long dt,
            OpenWeatherMapResponse.Main main,
            List<OpenWeatherMapResponse.Weather> weather,
            @JsonProperty("dt_txt") String dtTxt
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record City(
            String name,
            int timezone
    ) {
    }
}
