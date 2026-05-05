package com.Nhom7.DACN_KTPM.repository;

import com.Nhom7.DACN_KTPM.entity.Color;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ColorRepository extends JpaRepository<Color, Long> {
    Optional<Color> findByColorCode(String colorCode);
    boolean existsByColorCode(String colorCode);
}