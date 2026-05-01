package com.chaewookim.backend.recommend;

import com.chaewookim.backend.enums.Gender;
import com.chaewookim.backend.enums.Style;
import com.chaewookim.backend.enums.WeatherCondition;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RecommendController {

    private final RecommendService recommendService;

    public RecommendController(RecommendService recommendService) {
        this.recommendService = recommendService;
    }

    @GetMapping("/recommend")
    public List<Recommendation> recommend(
            @RequestParam int temp,
            @RequestParam WeatherCondition condition,
            @RequestParam Gender gender,
            @RequestParam Style style) {
        return recommendService.recommend(temp, condition, gender, style);
    }
}
