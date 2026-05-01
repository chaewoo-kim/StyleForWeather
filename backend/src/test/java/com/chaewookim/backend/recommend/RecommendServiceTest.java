package com.chaewookim.backend.recommend;

import com.chaewookim.backend.entity.Clothes;
import com.chaewookim.backend.entity.StyleClothes;
import com.chaewookim.backend.entity.WeatherStyle;
import com.chaewookim.backend.enums.Category;
import com.chaewookim.backend.enums.Gender;
import com.chaewookim.backend.enums.Style;
import com.chaewookim.backend.enums.WeatherCondition;
import com.chaewookim.backend.repository.ClothesRepository;
import com.chaewookim.backend.repository.StyleClothesRepository;
import com.chaewookim.backend.repository.WeatherStyleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(RecommendService.class)
class RecommendServiceTest {

    @Autowired
    private WeatherStyleRepository weatherStyleRepository;

    @Autowired
    private ClothesRepository clothesRepository;

    @Autowired
    private StyleClothesRepository styleClothesRepository;

    @Autowired
    private RecommendService recommendService;

    private WeatherStyle mildClear;

    @BeforeEach
    void setUp() {
        mildClear = weatherStyleRepository.save(WeatherStyle.builder()
                .minTemp(15).maxTemp(20)
                .weatherCondition(WeatherCondition.CLEAR)
                .build());
    }

    @Test
    @DisplayName("매칭된 옷을 priority 오름차순으로 반환")
    void recommend_returnsMatchedClothesByPriorityAsc() {
        Clothes shirt = saveClothes("긴팔 셔츠", Category.TOP, Gender.MALE, Style.CASUAL);
        Clothes jeans = saveClothes("청바지", Category.BOTTOM, Gender.MALE, Style.CASUAL);
        link(mildClear, jeans, 0);
        link(mildClear, shirt, 1);

        List<Recommendation> result = recommendService.recommend(
                17, WeatherCondition.CLEAR, Gender.MALE, Style.CASUAL);

        assertThat(result).extracting(Recommendation::name)
                .containsExactly("청바지", "긴팔 셔츠");
    }

    @Test
    @DisplayName("UNISEX 옷은 어떤 성별 요청에도 포함된다")
    void recommend_includesUnisex() {
        Clothes femaleSkirt = saveClothes("스커트", Category.BOTTOM, Gender.FEMALE, Style.CASUAL);
        Clothes unisexCap = saveClothes("캡모자", Category.ACCESSORY, Gender.UNISEX, Style.CASUAL);
        link(mildClear, femaleSkirt, 0);
        link(mildClear, unisexCap, 1);

        List<Recommendation> result = recommendService.recommend(
                17, WeatherCondition.CLEAR, Gender.MALE, Style.CASUAL);

        assertThat(result).extracting(Recommendation::name).containsExactly("캡모자");
    }

    @Test
    @DisplayName("스타일 불일치 옷은 제외된다")
    void recommend_filtersByStyle() {
        Clothes casual = saveClothes("후디", Category.TOP, Gender.UNISEX, Style.CASUAL);
        Clothes formal = saveClothes("셔츠", Category.TOP, Gender.UNISEX, Style.FORMAL);
        link(mildClear, casual, 0);
        link(mildClear, formal, 1);

        List<Recommendation> result = recommendService.recommend(
                17, WeatherCondition.CLEAR, Gender.MALE, Style.CASUAL);

        assertThat(result).extracting(Recommendation::name).containsExactly("후디");
    }

    @Test
    @DisplayName("기온 구간/날씨 조건에 매칭되는 WeatherStyle 없으면 빈 리스트")
    void recommend_returnsEmptyWhenNoMatch() {
        List<Recommendation> result = recommendService.recommend(
                30, WeatherCondition.RAIN, Gender.MALE, Style.CASUAL);

        assertThat(result).isEmpty();
    }

    private Clothes saveClothes(String name, Category category, Gender gender, Style style) {
        return clothesRepository.save(Clothes.builder()
                .name(name)
                .category(category)
                .gender(gender)
                .style(style)
                .build());
    }

    private void link(WeatherStyle ws, Clothes c, int priority) {
        styleClothesRepository.save(StyleClothes.builder()
                .weatherStyle(ws)
                .clothes(c)
                .priority(priority)
                .build());
    }
}
