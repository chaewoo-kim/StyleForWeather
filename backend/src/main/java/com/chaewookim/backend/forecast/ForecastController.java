package com.chaewookim.backend.forecast;

import com.chaewookim.backend.enums.Gender;
import com.chaewookim.backend.enums.Style;
import com.chaewookim.backend.weather.dto.WeeklyForecast;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ForecastController {

    private final ForecastService forecastService;

    public ForecastController(ForecastService forecastService) {
        this.forecastService = forecastService;
    }

    @GetMapping(path = "/forecast", params = {"lat", "lon"})
    public WeeklyForecast byCoordinates(@RequestParam double lat,
                                        @RequestParam double lon,
                                        @RequestParam Gender gender,
                                        @RequestParam Style style) {
        return forecastService.byCoordinates(lat, lon, gender, style);
    }

    @GetMapping(path = "/forecast", params = "city")
    public WeeklyForecast byCity(@RequestParam String city,
                                 @RequestParam Gender gender,
                                 @RequestParam Style style) {
        return forecastService.byCity(city, gender, style);
    }
}
