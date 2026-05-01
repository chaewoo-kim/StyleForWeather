package com.chaewookim.backend.clothes;

import com.chaewookim.backend.entity.Clothes;
import com.chaewookim.backend.enums.Category;
import com.chaewookim.backend.enums.Gender;
import com.chaewookim.backend.enums.Style;
import com.chaewookim.backend.repository.ClothesRepository;
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

class ClothesControllerTest {

    private MockMvc mockMvc;
    private ClothesRepository clothesRepository;

    @BeforeEach
    void setUp() {
        clothesRepository = mock(ClothesRepository.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new ClothesController(clothesRepository)).build();
    }

    @Test
    @DisplayName("/api/clothes 는 전체 옷 카탈로그 반환")
    void all() throws Exception {
        when(clothesRepository.findAll()).thenReturn(List.of(
                Clothes.builder().id(1L).name("후디").category(Category.TOP).gender(Gender.UNISEX).style(Style.CASUAL).build(),
                Clothes.builder().id(2L).name("청바지").category(Category.BOTTOM).gender(Gender.UNISEX).style(Style.CASUAL).build()
        ));

        mockMvc.perform(get("/api/clothes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("후디"));
    }

    @Test
    @DisplayName("/api/clothes/{category} 는 카테고리별 옷 반환 (PathVariable enum 바인딩)")
    void byCategory() throws Exception {
        when(clothesRepository.findByCategory(Category.OUTER)).thenReturn(List.of(
                Clothes.builder().id(3L).name("패딩").category(Category.OUTER).gender(Gender.UNISEX).style(Style.CASUAL).build()
        ));

        mockMvc.perform(get("/api/clothes/OUTER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("패딩"))
                .andExpect(jsonPath("$[0].category").value("OUTER"));
    }
}
