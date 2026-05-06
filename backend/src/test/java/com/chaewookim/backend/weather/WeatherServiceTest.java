package com.chaewookim.backend.weather;

import com.chaewookim.backend.enums.WeatherCondition;
import com.chaewookim.backend.weather.dto.CurrentWeather;
import com.chaewookim.backend.weather.dto.ForecastBundle;
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

    @Test
    @DisplayName("forecast 응답에서 일별 정오 슬롯이 추출된다")
    void getForecast_picksNoonSlotPerDay() {
        // UTC 기준 timestamps. timezone=0이라 dt 그대로 로컬 시각.
        // 2025-05-05 09:00 / 12:00 / 15:00, 2025-05-06 12:00
        String json = """
                {
                  "list": [
                    {"dt": 1746435600, "main": {"temp": 18.0, "feels_like": 18.0, "humidity": 50}, "weather": [{"id": 800, "main": "Clear", "description": "", "icon": ""}], "dt_txt": "2025-05-05 09:00:00"},
                    {"dt": 1746446400, "main": {"temp": 22.0, "feels_like": 22.0, "humidity": 55}, "weather": [{"id": 803, "main": "Clouds", "description": "", "icon": ""}], "dt_txt": "2025-05-05 12:00:00"},
                    {"dt": 1746457200, "main": {"temp": 24.0, "feels_like": 24.0, "humidity": 50}, "weather": [{"id": 800, "main": "Clear", "description": "", "icon": ""}], "dt_txt": "2025-05-05 15:00:00"},
                    {"dt": 1746532800, "main": {"temp": 19.0, "feels_like": 19.0, "humidity": 70}, "weather": [{"id": 500, "main": "Rain", "description": "", "icon": ""}], "dt_txt": "2025-05-06 12:00:00"}
                  ],
                  "city": {"name": "Seoul", "timezone": 0}
                }
                """;
        mockServer.expect(requestTo(containsString("/forecast")))
                .andExpect(requestTo(containsString("q=Seoul")))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        ForecastBundle bundle = service.getForecastByCity("Seoul");

        assertThat(bundle.locationName()).isEqualTo("Seoul");
        assertThat(bundle.daily()).hasSize(2);
        assertThat(bundle.daily().get(0).date()).isEqualTo("2025-05-05");
        assertThat(bundle.daily().get(0).temperature()).isEqualTo(22.0);
        assertThat(bundle.daily().get(0).condition()).isEqualTo(WeatherCondition.CLOUDS);
        assertThat(bundle.daily().get(1).date()).isEqualTo("2025-05-06");
        assertThat(bundle.daily().get(1).condition()).isEqualTo(WeatherCondition.RAIN);
    }

    @Test
    @DisplayName("forecast city.timezone offset이 적용되어 로컬 정오 슬롯이 선택된다")
    void getForecast_appliesTimezoneOffset() {
        // UTC 03:00 슬롯이 KST(timezone=32400) 기준 12:00이 되어 그 슬롯이 정답.
        // 2025-05-05 00:00 UTC, 03:00 UTC, 06:00 UTC
        String json = """
                {
                  "list": [
                    {"dt": 1746403200, "main": {"temp": 14.0, "feels_like": 14.0, "humidity": 60}, "weather": [{"id": 800, "main": "Clear", "description": "", "icon": ""}], "dt_txt": "2025-05-05 00:00:00"},
                    {"dt": 1746414000, "main": {"temp": 21.0, "feels_like": 21.0, "humidity": 55}, "weather": [{"id": 800, "main": "Clear", "description": "", "icon": ""}], "dt_txt": "2025-05-05 03:00:00"},
                    {"dt": 1746424800, "main": {"temp": 25.0, "feels_like": 25.0, "humidity": 50}, "weather": [{"id": 803, "main": "Clouds", "description": "", "icon": ""}], "dt_txt": "2025-05-05 06:00:00"}
                  ],
                  "city": {"name": "Seoul", "timezone": 32400}
                }
                """;
        mockServer.expect(requestTo(containsString("/forecast")))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        ForecastBundle bundle = service.getForecastByCoordinates(37.57, 126.98);

        assertThat(bundle.daily()).hasSize(1);
        assertThat(bundle.daily().get(0).date()).isEqualTo("2025-05-05");
        assertThat(bundle.daily().get(0).temperature()).isEqualTo(21.0);
        assertThat(bundle.daily().get(0).condition()).isEqualTo(WeatherCondition.CLEAR);
    }
}
