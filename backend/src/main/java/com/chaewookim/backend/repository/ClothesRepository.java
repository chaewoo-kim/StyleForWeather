package com.chaewookim.backend.repository;

import com.chaewookim.backend.entity.Clothes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClothesRepository extends JpaRepository<Clothes, Long> {
}
