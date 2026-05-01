package com.chaewookim.backend.weather;

import com.chaewookim.backend.enums.WeatherCondition;
import com.chaewookim.backend.weather.dto.CurrentWeather;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class WeatherServiceTest {

    private MockRestServiceServer mockServer;
    private WeatherService service;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        mockServer = MockRestServiceServer.bindTo(builder).build();
        OpenWeatherMapProperties props = new OpenWeatherMapProperties(
                "test-key",
                "https://api.openweathermap.org/data/2.5"
        );
        service = new WeatherService(props, builder);
    }

    @Test
    @DisplayName("도시명으로 조회 시 응답이 도메인 객체로 매핑된다")
    void getByCity_returnsMappedDomain() {
        String json = """
                {
                  "weather": [{"id": 800, "main": "Clear", "description": "맑음", "icon": "01d"}],
                  "main": {"temp": 22.5, "feels_like": 21.8, "humidity": 60},
                  "name": "Seoul"
                }
                """;
        mockServer.expect(requestTo(containsString("q=Seoul")))
                .andExpect(requestTo(containsString("units=metric")))
                .andExpect(requestTo(containsString("appid=test-key")))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        CurrentWeather weather = service.getByCity("Seoul");

        assertThat(weather.locationName()).isEqualTo("Seoul");
        assertThat(weather.temperature()).isEqualTo(22.5);
        assertThat(weather.condition()).isEqualTo(WeatherCondition.CLEAR);
        mockServer.verify();
    }

    @Test
    @DisplayName("좌표로 조회 시 lat/lon 쿼리 파라미터가 전달된다")
    void getByCoordinates_passesLatLon() {
        String json = """
                {
                  "weather": [{"id": 500, "main": "Rain", "description": "비", "icon": "10d"}],
                  "main": {"temp": 18.0, "feels_like": 17.0, "humidity": 80},
                  "name": "Busan"
                }
                """;
        mockServer.expect(requestTo(containsString("lat=35.18")))
                .andExpect(requestTo(containsString("lon=129.07")))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        CurrentWeather weather = service.getByCoordinates(35.18, 129.07);

        assertThat(weather.condition()).isEqualTo(WeatherCondition.RAIN);
        mockServer.verify();
    }

    @Test
    @DisplayName("WeatherCondition enum에 없는 값은 CLEAR로 fallback")
    void unknownCondition_fallsBackToClear() {
        String json = """
                {
                  "weather": [{"id": 711, "main": "Smoke", "description": "연무", "icon": "50d"}],
                  "main": {"temp": 10.0, "feels_like": 9.0, "humidity": 50},
                  "name": "Daegu"
                }
                """;
        mockServer.expect(requestTo(containsString("q=Daegu")))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        CurrentWeather weather = service.getByCity("Daegu");

        assertThat(weather.condition()).isEqualTo(WeatherCondition.CLEAR);
    }
}
