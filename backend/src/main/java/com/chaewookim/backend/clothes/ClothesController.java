package com.chaewookim.backend.clothes;

import com.chaewookim.backend.entity.Clothes;
import com.chaewookim.backend.enums.Category;
import com.chaewookim.backend.repository.ClothesRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/clothes")
public class ClothesController {

    private final ClothesRepository clothesRepository;

    public ClothesController(ClothesRepository clothesRepository) {
        this.clothesRepository = clothesRepository;
    }

    @GetMapping
    public List<Clothes> all() {
        return clothesRepository.findAll();
    }

    @GetMapping("/{category}")
    public List<Clothes> byCategory(@PathVariable Category category) {
        return clothesRepository.findByCategory(category);
    }
}
