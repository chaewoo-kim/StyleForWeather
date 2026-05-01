package com.chaewookim.backend.weather;

import com.chaewookim.backend.enums.WeatherCondition;
import com.chaewookim.backend.weather.dto.CurrentWeather;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class WeatherControllerTest {

    private MockMvc mockMvc;
    private WeatherService weatherService;

    @BeforeEach
    void setUp() {
        weatherService = mock(WeatherService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new WeatherController(weatherService)).build();
    }

    @Test
    @DisplayName("city 쿼리로 호출하면 getByCity로 라우팅")
    void byCity() throws Exception {
        when(weatherService.getByCity("Seoul"))
                .thenReturn(new CurrentWeather("Seoul", 22.5, WeatherCondition.CLEAR));

        mockMvc.perform(get("/api/weather").param("city", "Seoul"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.locationName").value("Seoul"))
                .andExpect(jsonPath("$.temperature").value(22.5))
                .andExpect(jsonPath("$.condition").value("CLEAR"));
    }

    @Test
    @DisplayName("lat/lon 쿼리로 호출하면 getByCoordinates로 라우팅")
    void byCoordinates() throws Exception {
        when(weatherService.getByCoordinates(eq(35.18), eq(129.07)))
                .thenReturn(new CurrentWeather("Busan", 18.0, WeatherCondition.RAIN));

        mockMvc.perform(get("/api/weather").param("lat", "35.18").param("lon", "129.07"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.locationName").value("Busan"))
                .andExpect(jsonPath("$.condition").value("RAIN"));
    }
}
