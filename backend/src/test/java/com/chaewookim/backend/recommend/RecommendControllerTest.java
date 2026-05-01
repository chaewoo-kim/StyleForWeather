package com.chaewookim.backend.recommend;

import com.chaewookim.backend.enums.Category;
import com.chaewookim.backend.enums.Gender;
import com.chaewookim.backend.enums.Style;
import com.chaewookim.backend.enums.WeatherCondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RecommendControllerTest {

    private MockMvc mockMvc;
    private RecommendService recommendService;

    @BeforeEach
    void setUp() {
        recommendService = mock(RecommendService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new RecommendController(recommendService)).build();
    }

    @Test
    @DisplayName("쿼리 파라미터가 enum으로 바인딩되고 결과가 JSON 배열로 반환")
    void recommend() throws Exception {
        when(recommendService.recommend(17, WeatherCondition.CLEAR, Gender.MALE, Style.CASUAL))
                .thenReturn(List.of(
                        new Recommendation(1L, "긴팔 셔츠", Category.TOP, null, Gender.MALE, Style.CASUAL, 0),
                        new Recommendation(2L, "청바지", Category.BOTTOM, null, Gender.UNISEX, Style.CASUAL, 1)
                ));

        mockMvc.perform(get("/api/recommend")
                        .param("temp", "17")
                        .param("condition", "CLEAR")
                        .param("gender", "MALE")
                        .param("style", "CASUAL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("긴팔 셔츠"))
                .andExpect(jsonPath("$[0].priority").value(0))
                .andExpect(jsonPath("$[1].name").value("청바지"))
                .andExpect(jsonPath("$[1].gender").value("UNISEX"));
    }

    @Test
    @DisplayName("매칭 결과 없을 때 빈 배열 반환")
    void recommend_empty() throws Exception {
        when(recommendService.recommend(30, WeatherCondition.RAIN, Gender.FEMALE, Style.FORMAL))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/recommend")
                        .param("temp", "30")
                        .param("condition", "RAIN")
                        .param("gender", "FEMALE")
                        .param("style", "FORMAL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
