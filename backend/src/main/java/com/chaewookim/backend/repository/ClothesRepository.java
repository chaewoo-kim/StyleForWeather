package com.chaewookim.backend.repository;

import com.chaewookim.backend.entity.Clothes;
import com.chaewookim.backend.enums.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClothesRepository extends JpaRepository<Clothes, Long> {

    List<Clothes> findByCategory(Category category);
}
