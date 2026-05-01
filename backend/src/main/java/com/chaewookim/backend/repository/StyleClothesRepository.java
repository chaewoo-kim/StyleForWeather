package com.chaewookim.backend.repository;

import com.chaewookim.backend.entity.StyleClothes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StyleClothesRepository extends JpaRepository<StyleClothes, Long> {

    @Query("""
            SELECT sc FROM StyleClothes sc
            JOIN FETCH sc.clothes
            WHERE sc.weatherStyle.id = :weatherStyleId
            ORDER BY sc.priority ASC
            """)
    List<StyleClothes> findByWeatherStyleIdWithClothes(@Param("weatherStyleId") Long weatherStyleId);
}
