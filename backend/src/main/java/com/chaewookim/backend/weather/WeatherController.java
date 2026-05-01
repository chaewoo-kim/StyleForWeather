package com.chaewookim.backend.weather;

import com.chaewookim.backend.weather.dto.CurrentWeather;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping(path = "/weather", params = {"lat", "lon"})
    public CurrentWeather byCoordinates(@RequestParam double lat, @RequestParam double lon) {
        return weatherService.getByCoordinates(lat, lon);
    }

    @GetMapping(path = "/weather", params = "city")
    public CurrentWeather byCity(@RequestParam String city) {
        return weatherService.getByCity(city);
    }
}
