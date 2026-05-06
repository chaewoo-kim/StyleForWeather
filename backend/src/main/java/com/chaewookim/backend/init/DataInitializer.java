package com.chaewookim.backend.init;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Component
@Profile("!prod")
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final ClothesRepository clothesRepository;
    private final WeatherStyleRepository weatherStyleRepository;
    private final StyleClothesRepository styleClothesRepository;

    public DataInitializer(ClothesRepository clothesRepository,
                           WeatherStyleRepository weatherStyleRepository,
                           StyleClothesRepository styleClothesRepository) {
        this.clothesRepository = clothesRepository;
        this.weatherStyleRepository = weatherStyleRepository;
        this.styleClothesRepository = styleClothesRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (clothesRepository.count() > 0) {
            log.info("[DataInitializer] 데이터가 이미 존재하여 초기화 건너뜀");
            return;
        }

        Map<String, Clothes> c = saveClothes();
        Map<String, WeatherStyle> w = saveWeatherStyles();
        linkStyleClothes(w, c);

        log.info("[DataInitializer] 초기 데이터 삽입 완료: clothes={}, weatherStyles={}, styleClothes={}",
                clothesRepository.count(), weatherStyleRepository.count(), styleClothesRepository.count());
    }

    private Map<String, Clothes> saveClothes() {
        Map<String, Clothes> map = new HashMap<>();

        addClothes(map, "히트텍", "heattech", Category.INNER, Gender.UNISEX, Style.CASUAL);
        addClothes(map, "흰색 반팔티", "tshirt-white", Category.TOP, Gender.UNISEX, Style.CASUAL);
        addClothes(map, "긴팔 셔츠", "shirt-long", Category.TOP, Gender.UNISEX, Style.CASUAL);
        addClothes(map, "후디", "hoodie", Category.TOP, Gender.UNISEX, Style.CASUAL);
        addClothes(map, "니트", "knit", Category.TOP, Gender.UNISEX, Style.CASUAL);
        addClothes(map, "린넨 셔츠", "shirt-linen", Category.TOP, Gender.UNISEX, Style.CASUAL);
        addClothes(map, "피케 셔츠", "shirt-pique", Category.TOP, Gender.UNISEX, Style.CASUAL);
        addClothes(map, "원피스", "dress", Category.TOP, Gender.FEMALE, Style.CASUAL);
        addClothes(map, "정장 셔츠", "shirt-formal", Category.TOP, Gender.UNISEX, Style.FORMAL);

        addClothes(map, "청바지", "jeans", Category.BOTTOM, Gender.UNISEX, Style.CASUAL);
        addClothes(map, "면바지", "pants-cotton", Category.BOTTOM, Gender.UNISEX, Style.CASUAL);
        addClothes(map, "면 반바지", "shorts", Category.BOTTOM, Gender.UNISEX, Style.CASUAL);
        addClothes(map, "플리츠 스커트", "skirt-pleated", Category.BOTTOM, Gender.FEMALE, Style.CASUAL);
        addClothes(map, "정장 바지", "pants-formal", Category.BOTTOM, Gender.UNISEX, Style.FORMAL);

        addClothes(map, "패딩 점퍼", "padding", Category.OUTER, Gender.UNISEX, Style.CASUAL);
        addClothes(map, "코트", "coat", Category.OUTER, Gender.UNISEX, Style.CASUAL);
        addClothes(map, "트렌치코트", "trenchcoat", Category.OUTER, Gender.UNISEX, Style.CASUAL);
        addClothes(map, "야상", "field-jacket", Category.OUTER, Gender.UNISEX, Style.CASUAL);
        addClothes(map, "카디건", "cardigan", Category.OUTER, Gender.UNISEX, Style.CASUAL);
        addClothes(map, "우비", "raincoat", Category.OUTER, Gender.UNISEX, Style.CASUAL);

        addClothes(map, "운동화", "sneakers", Category.SHOES, Gender.UNISEX, Style.CASUAL);
        addClothes(map, "구두", "dress-shoes", Category.SHOES, Gender.UNISEX, Style.FORMAL);

        addClothes(map, "발열 양말", "socks-thermal", Category.SOCKS, Gender.UNISEX, Style.CASUAL);

        addClothes(map, "우산", "umbrella", Category.ACCESSORY, Gender.UNISEX, Style.CASUAL);
        addClothes(map, "캡모자", "cap", Category.ACCESSORY, Gender.UNISEX, Style.CASUAL);
        addClothes(map, "비니", "beanie", Category.ACCESSORY, Gender.UNISEX, Style.CASUAL);
        addClothes(map, "목도리", "scarf", Category.ACCESSORY, Gender.UNISEX, Style.CASUAL);
        addClothes(map, "장갑", "gloves", Category.ACCESSORY, Gender.UNISEX, Style.CASUAL);

        return map;
    }

    private void addClothes(Map<String, Clothes> map, String name, String slug, Category category, Gender gender, Style style) {
        Clothes saved = clothesRepository.save(Clothes.builder()
                .name(name)
                .category(category)
                .gender(gender)
                .style(style)
                .imageUrl("/images/clothes/" + slug + ".svg")
                .build());
        map.put(name, saved);
    }

    private Map<String, WeatherStyle> saveWeatherStyles() {
        Map<String, WeatherStyle> map = new HashMap<>();
        map.put("VERY_COLD",   saveStyle(-50, 4,  WeatherCondition.CLEAR));
        map.put("COLD",        saveStyle(5,   8,  WeatherCondition.CLEAR));
        map.put("CHILLY",      saveStyle(9,   16, WeatherCondition.CLEAR));
        map.put("MILD",        saveStyle(17,  22, WeatherCondition.CLEAR));
        map.put("WARM",        saveStyle(23,  27, WeatherCondition.CLEAR));
        map.put("HOT",         saveStyle(28,  50, WeatherCondition.CLEAR));
        map.put("MILD_RAIN",   saveStyle(17,  22, WeatherCondition.RAIN));
        return map;
    }

    private WeatherStyle saveStyle(int min, int max, WeatherCondition condition) {
        return weatherStyleRepository.save(WeatherStyle.builder()
                .minTemp(min).maxTemp(max).weatherCondition(condition).build());
    }

    private void linkStyleClothes(Map<String, WeatherStyle> w, Map<String, Clothes> c) {
        // 매우 추움 (≤4°C): 두꺼운 레이어 풀세트
        link(w.get("VERY_COLD"), c.get("히트텍"), 0);
        link(w.get("VERY_COLD"), c.get("니트"), 1);
        link(w.get("VERY_COLD"), c.get("패딩 점퍼"), 2);
        link(w.get("VERY_COLD"), c.get("청바지"), 3);
        link(w.get("VERY_COLD"), c.get("운동화"), 4);
        link(w.get("VERY_COLD"), c.get("발열 양말"), 5);
        link(w.get("VERY_COLD"), c.get("목도리"), 6);
        link(w.get("VERY_COLD"), c.get("장갑"), 7);
        link(w.get("VERY_COLD"), c.get("비니"), 8);

        // 추움 (5~8°C): 코트 + 니트
        link(w.get("COLD"), c.get("히트텍"), 0);
        link(w.get("COLD"), c.get("니트"), 1);
        link(w.get("COLD"), c.get("코트"), 2);
        link(w.get("COLD"), c.get("청바지"), 3);
        link(w.get("COLD"), c.get("운동화"), 4);
        link(w.get("COLD"), c.get("목도리"), 5);

        // 쌀쌀 (9~16°C): 트렌치/야상 + 가디건
        link(w.get("CHILLY"), c.get("긴팔 셔츠"), 0);
        link(w.get("CHILLY"), c.get("카디건"), 1);
        link(w.get("CHILLY"), c.get("트렌치코트"), 2);
        link(w.get("CHILLY"), c.get("야상"), 3);
        link(w.get("CHILLY"), c.get("청바지"), 4);
        link(w.get("CHILLY"), c.get("면바지"), 5);
        link(w.get("CHILLY"), c.get("운동화"), 6);

        // 적당 (17~22°C, CLEAR): 셔츠 + 청바지
        link(w.get("MILD"), c.get("긴팔 셔츠"), 0);
        link(w.get("MILD"), c.get("피케 셔츠"), 1);
        link(w.get("MILD"), c.get("후디"), 2);
        link(w.get("MILD"), c.get("청바지"), 3);
        link(w.get("MILD"), c.get("면바지"), 4);
        link(w.get("MILD"), c.get("플리츠 스커트"), 5);
        link(w.get("MILD"), c.get("운동화"), 6);
        link(w.get("MILD"), c.get("캡모자"), 7);

        // 적당 (17~22°C, CLEAR) FORMAL: 정장 한 벌
        link(w.get("MILD"), c.get("정장 셔츠"), 10);
        link(w.get("MILD"), c.get("정장 바지"), 11);
        link(w.get("MILD"), c.get("구두"), 12);

        // 적당 (17~22°C, RAIN): MILD + 우산/우비
        link(w.get("MILD_RAIN"), c.get("긴팔 셔츠"), 0);
        link(w.get("MILD_RAIN"), c.get("우비"), 1);
        link(w.get("MILD_RAIN"), c.get("청바지"), 2);
        link(w.get("MILD_RAIN"), c.get("운동화"), 3);
        link(w.get("MILD_RAIN"), c.get("우산"), 4);

        // 따뜻 (23~27°C): 반팔
        link(w.get("WARM"), c.get("흰색 반팔티"), 0);
        link(w.get("WARM"), c.get("피케 셔츠"), 1);
        link(w.get("WARM"), c.get("린넨 셔츠"), 2);
        link(w.get("WARM"), c.get("면바지"), 3);
        link(w.get("WARM"), c.get("플리츠 스커트"), 4);
        link(w.get("WARM"), c.get("원피스"), 5);
        link(w.get("WARM"), c.get("운동화"), 6);
        link(w.get("WARM"), c.get("캡모자"), 7);

        // 더움 (≥28°C): 반팔 + 반바지
        link(w.get("HOT"), c.get("흰색 반팔티"), 0);
        link(w.get("HOT"), c.get("린넨 셔츠"), 1);
        link(w.get("HOT"), c.get("면 반바지"), 2);
        link(w.get("HOT"), c.get("원피스"), 3);
        link(w.get("HOT"), c.get("운동화"), 4);
        link(w.get("HOT"), c.get("캡모자"), 5);
    }

    private void link(WeatherStyle ws, Clothes clothes, int priority) {
        styleClothesRepository.save(StyleClothes.builder()
                .weatherStyle(ws)
                .clothes(clothes)
                .priority(priority)
                .build());
    }
}
