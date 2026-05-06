package com.chaewookim.backend.weather;

import com.chaewookim.backend.enums.WeatherCondition;
import com.chaewookim.backend.weather.dto.CurrentWeather;
import com.chaewookim.backend.weather.dto.DailyForecast;
import com.chaewookim.backend.weather.dto.ForecastBundle;
import com.chaewookim.backend.weather.dto.OpenWeatherMapForecastResponse;
import com.chaewookim.backend.weather.dto.OpenWeatherMapResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class WeatherService {

    private static final int FORECAST_DAYS = 5;
    private static final int TARGET_HOUR = 12;

    private final OpenWeatherMapProperties properties;
    private final RestClient restClient;

    public WeatherService(OpenWeatherMapProperties properties, RestClient.Builder builder) {
        this.properties = properties;
        this.restClient = builder.baseUrl(properties.baseUrl()).build();
    }

    public CurrentWeather getByCoordinates(double lat, double lon) {
        return callCurrent(uri -> uri
                .path("/weather")
                .queryParam("lat", lat)
                .queryParam("lon", lon));
    }

    public CurrentWeather getByCity(String city) {
        return callCurrent(uri -> uri
                .path("/weather")
                .queryParam("q", city));
    }

    public ForecastBundle getForecastByCoordinates(double lat, double lon) {
        return callForecast(uri -> uri
                .path("/forecast")
                .queryParam("lat", lat)
                .queryParam("lon", lon));
    }

    public ForecastBundle getForecastByCity(String city) {
        return callForecast(uri -> uri
                .path("/forecast")
                .queryParam("q", city));
    }

    private CurrentWeather callCurrent(Function<UriBuilder, UriBuilder> uriCustomizer) {
        OpenWeatherMapResponse response = restClient.get()
                .uri(uriBuilder -> withCommonParams(uriCustomizer.apply(uriBuilder)).build())
                .retrieve()
                .body(OpenWeatherMapResponse.class);
        return toCurrentWeather(response);
    }

    private ForecastBundle callForecast(Function<UriBuilder, UriBuilder> uriCustomizer) {
        OpenWeatherMapForecastResponse response = restClient.get()
                .uri(uriBuilder -> withCommonParams(uriCustomizer.apply(uriBuilder)).build())
                .retrieve()
                .body(OpenWeatherMapForecastResponse.class);
        return toForecastBundle(response);
    }

    private UriBuilder withCommonParams(UriBuilder builder) {
        return builder
                .queryParam("units", "metric")
                .queryParam("lang", "kr")
                .queryParam("appid", properties.apiKey());
    }

    private CurrentWeather toCurrentWeather(OpenWeatherMapResponse response) {
        if (response == null || response.weather() == null || response.weather().isEmpty()) {
            throw new IllegalStateException("OpenWeatherMap response is empty");
        }
        return new CurrentWeather(
                response.name(),
                response.main().temp(),
                mapCondition(response.weather().get(0).main())
        );
    }

    private ForecastBundle toForecastBundle(OpenWeatherMapForecastResponse response) {
        if (response == null || response.list() == null || response.city() == null) {
            throw new IllegalStateException("OpenWeatherMap forecast response is empty");
        }
        int tzOffset = response.city().timezone();

        Map<LocalDate, List<OpenWeatherMapForecastResponse.Item>> byDate = response.list().stream()
                .collect(Collectors.groupingBy(
                        item -> toLocalDate(item.dt(), tzOffset),
                        LinkedHashMap::new,
                        Collectors.toList()));

        List<DailyForecast> daily = byDate.entrySet().stream()
                .map(entry -> pickNoon(entry.getKey(), entry.getValue(), tzOffset))
                .limit(FORECAST_DAYS)
                .toList();

        return new ForecastBundle(response.city().name(), daily);
    }

    private DailyForecast pickNoon(LocalDate date,
                                   List<OpenWeatherMapForecastResponse.Item> items,
                                   int tzOffset) {
        OpenWeatherMapForecastResponse.Item closest = items.stream()
                .min(Comparator.comparingInt(item -> Math.abs(toLocalHour(item.dt(), tzOffset) - TARGET_HOUR)))
                .orElseThrow();
        String main = closest.weather() != null && !closest.weather().isEmpty()
                ? closest.weather().get(0).main()
                : null;
        return new DailyForecast(
                date.toString(),
                closest.main().temp(),
                mapCondition(main)
        );
    }

    private static LocalDate toLocalDate(long dtSeconds, int tzOffset) {
        return LocalDateTime.ofEpochSecond(dtSeconds + tzOffset, 0, ZoneOffset.UTC).toLocalDate();
    }

    private static int toLocalHour(long dtSeconds, int tzOffset) {
        return LocalDateTime.ofEpochSecond(dtSeconds + tzOffset, 0, ZoneOffset.UTC).getHour();
    }

    private WeatherCondition mapCondition(String openWeatherMain) {
        if (openWeatherMain == null) return WeatherCondition.CLEAR;
        return Arrays.stream(WeatherCondition.values())
                .filter(c -> c.name().equalsIgnoreCase(openWeatherMain))
                .findFirst()
                .orElse(WeatherCondition.CLEAR);
    }
}
