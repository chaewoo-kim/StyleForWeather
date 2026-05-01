package com.chaewookim.backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiIntegrationTest {

    private static final ParameterizedTypeReference<List<Map<String, Object>>> JSON_LIST =
            new ParameterizedTypeReference<>() {};

    @LocalServerPort
    int port;

    private RestClient client;

    @BeforeEach
    void setUp() {
        client = RestClient.create("http://localhost:" + port);
    }

    @Test
    @DisplayName("GET /api/clothes — 시드 옷 28개 반환")
    void allClothes() {
        List<Map<String, Object>> result = client.get().uri("/api/clothes")
                .retrieve().body(JSON_LIST);
        assertThat(result).hasSize(28);
    }

    @Test
    @DisplayName("GET /api/clothes/OUTER — OUTER 카테고리 6개 반환")
    void outerClothes() {
        List<Map<String, Object>> result = client.get().uri("/api/clothes/OUTER")
                .retrieve().body(JSON_LIST);
        assertThat(result).hasSize(6);
        assertThat(result).allSatisfy(item -> assertThat(item.get("category")).isEqualTo("OUTER"));
    }

    @Test
    @DisplayName("GET /api/recommend 매우 추움 — 9개 풀세트 priority 순")
    void recommend_veryCold() {
        List<Map<String, Object>> result = client.get()
                .uri("/api/recommend?temp=2&condition=CLEAR&gender=MALE&style=CASUAL")
                .retrieve().body(JSON_LIST);

        assertThat(result).hasSize(9);
        assertThat(result.get(0).get("name")).isEqualTo("히트텍");
        assertThat(result.get(0).get("priority")).isEqualTo(0);
        assertThat(result).extracting(m -> m.get("name"))
                .containsSubsequence("히트텍", "니트", "패딩 점퍼", "장갑", "비니");
    }

    @Test
    @DisplayName("GET /api/recommend RAIN — 우산/우비 포함")
    void recommend_rain() {
        List<Map<String, Object>> result = client.get()
                .uri("/api/recommend?temp=20&condition=RAIN&gender=FEMALE&style=CASUAL")
                .retrieve().body(JSON_LIST);

        assertThat(result).extracting(m -> m.get("name"))
                .contains("우산", "우비");
    }

    @Test
    @DisplayName("GET /api/recommend MALE 요청 → FEMALE 전용 옷 제외, UNISEX는 포함")
    void recommend_genderFilter() {
        List<Map<String, Object>> result = client.get()
                .uri("/api/recommend?temp=30&condition=CLEAR&gender=MALE&style=CASUAL")
                .retrieve().body(JSON_LIST);

        assertThat(result).extracting(m -> m.get("name")).doesNotContain("원피스");
        assertThat(result).extracting(m -> m.get("gender")).contains("UNISEX");
    }

    @Test
    @DisplayName("GET /api/recommend FORMAL — 정장 한 벌")
    void recommend_formal() {
        List<Map<String, Object>> result = client.get()
                .uri("/api/recommend?temp=20&condition=CLEAR&gender=MALE&style=FORMAL")
                .retrieve().body(JSON_LIST);

        assertThat(result).extracting(m -> m.get("name"))
                .containsExactly("정장 셔츠", "정장 바지", "구두");
    }

    @Test
    @DisplayName("GET /api/recommend — 매칭 WeatherStyle 없을 때 빈 배열")
    void recommend_noMatch() {
        List<Map<String, Object>> result = client.get()
                .uri("/api/recommend?temp=100&condition=CLEAR&gender=MALE&style=CASUAL")
                .retrieve().body(JSON_LIST);
        assertThat(result).isEmpty();
    }
}
