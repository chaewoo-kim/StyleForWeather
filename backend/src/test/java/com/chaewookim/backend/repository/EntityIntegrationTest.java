package com.chaewookim.backend.repository;

import com.chaewookim.backend.entity.Clothes;
import com.chaewookim.backend.entity.StyleClothes;
import com.chaewookim.backend.entity.WeatherStyle;
import com.chaewookim.backend.enums.Category;
import com.chaewookim.backend.enums.Gender;
import com.chaewookim.backend.enums.Style;
import com.chaewookim.backend.enums.WeatherCondition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class EntityIntegrationTest {

    @Autowired
    private ClothesRepository clothesRepository;

    @Autowired
    private WeatherStyleRepository weatherStyleRepository;

    @Autowired
    private StyleClothesRepository styleClothesRepository;

    @Test
    @DisplayName("Clothes 저장 시 id 자동 할당")
    void saveClothes_assignsId() {
        Clothes saved = clothesRepository.save(Clothes.builder()
                .name("기본 티셔츠")
                .category(Category.TOP)
                .gender(Gender.UNISEX)
                .style(Style.CASUAL)
                .build());

        assertThat(saved.getId()).isNotNull();
        assertThat(clothesRepository.findById(saved.getId())).isPresent();
    }

    @Test
    @DisplayName("StyleClothes로 WeatherStyle과 Clothes 연결, findAll로 조회")
    void styleClothes_linksWeatherAndClothes() {
        Clothes padding = clothesRepository.save(Clothes.builder()
                .name("롱패딩")
                .category(Category.OUTER)
                .gender(Gender.MALE)
                .style(Style.CASUAL)
                .build());

        WeatherStyle winterSnow = weatherStyleRepository.save(WeatherStyle.builder()
                .minTemp(-10)
                .maxTemp(5)
                .weatherCondition(WeatherCondition.SNOW)
                .build());

        styleClothesRepository.save(StyleClothes.builder()
                .weatherStyle(winterSnow)
                .clothes(padding)
                .priority(1)
                .build());

        List<StyleClothes> all = styleClothesRepository.findAll();
        assertThat(all).hasSize(1);
        assertThat(all.get(0).getWeatherStyle().getId()).isEqualTo(winterSnow.getId());
        assertThat(all.get(0).getClothes().getName()).isEqualTo("롱패딩");
    }

    @Test
    @DisplayName("같은 (WeatherStyle, Clothes) 조합 중복 저장 시 유니크 제약 위반")
    void duplicateStyleClothes_violatesUniqueConstraint() {
        Clothes umbrella = clothesRepository.save(Clothes.builder()
                .name("우산")
                .category(Category.ACCESSORY)
                .gender(Gender.UNISEX)
                .style(Style.CASUAL)
                .build());

        WeatherStyle springRain = weatherStyleRepository.save(WeatherStyle.builder()
                .minTemp(15)
                .maxTemp(25)
                .weatherCondition(WeatherCondition.RAIN)
                .build());

        styleClothesRepository.saveAndFlush(StyleClothes.builder()
                .weatherStyle(springRain)
                .clothes(umbrella)
                .priority(0)
                .build());

        assertThatThrownBy(() ->
                styleClothesRepository.saveAndFlush(StyleClothes.builder()
                        .weatherStyle(springRain)
                        .clothes(umbrella)
                        .priority(1)
                        .build())
        ).isInstanceOf(DataIntegrityViolationException.class);
    }
}
