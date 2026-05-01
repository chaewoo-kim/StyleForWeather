package com.chaewookim.backend.recommend;

import com.chaewookim.backend.enums.Category;
import com.chaewookim.backend.enums.Gender;
import com.chaewookim.backend.enums.Style;

public record Recommendation(
        Long clothesId,
        String name,
        Category category,
        String imageUrl,
        Gender gender,
        Style style,
        Integer priority
) {
}
