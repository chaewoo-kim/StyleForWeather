package com.chaewookim.backend.recommend;

import com.chaewookim.backend.entity.Clothes;
import com.chaewookim.backend.entity.StyleClothes;
import com.chaewookim.backend.entity.WeatherStyle;
import com.chaewookim.backend.enums.Gender;
import com.chaewookim.backend.enums.Style;
import com.chaewookim.backend.enums.WeatherCondition;
import com.chaewookim.backend.repository.StyleClothesRepository;
import com.chaewookim.backend.repository.WeatherStyleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RecommendService {

    private final WeatherStyleRepository weatherStyleRepository;
    private final StyleClothesRepository styleClothesRepository;

    public RecommendService(WeatherStyleRepository weatherStyleRepository,
                            StyleClothesRepository styleClothesRepository) {
        this.weatherStyleRepository = weatherStyleRepository;
        this.styleClothesRepository = styleClothesRepository;
    }

    @Transactional(readOnly = true)
    public List<Recommendation> recommend(int temp, WeatherCondition condition, Gender gender, Style style) {
        Optional<WeatherStyle> match = weatherStyleRepository
                .findFirstByMinTempLessThanEqualAndMaxTempGreaterThanEqualAndWeatherCondition(temp, temp, condition);

        if (match.isEmpty()) {
            return List.of();
        }

        return styleClothesRepository.findByWeatherStyleIdWithClothes(match.get().getId()).stream()
                .filter(sc -> matchesGender(sc.getClothes().getGender(), gender))
                .filter(sc -> sc.getClothes().getStyle() == style)
                .map(RecommendService::toRecommendation)
                .toList();
    }

    private static boolean matchesGender(Gender clothesGender, Gender requested) {
        return clothesGender == requested || clothesGender == Gender.UNISEX;
    }

    private static Recommendation toRecommendation(StyleClothes sc) {
        Clothes c = sc.getClothes();
        return new Recommendation(
                c.getId(),
                c.getName(),
                c.getCategory(),
                c.getImageUrl(),
                c.getGender(),
                c.getStyle(),
                sc.getPriority()
        );
    }
}
