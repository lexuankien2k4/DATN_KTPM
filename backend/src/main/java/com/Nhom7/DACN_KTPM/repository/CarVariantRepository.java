package com.Nhom7.DACN_KTPM.repository;

import com.Nhom7.DACN_KTPM.entity.CarVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface CarVariantRepository extends JpaRepository<CarVariant, Long> {


    List<CarVariant> findByCarModelIdAndIsActiveTrueOrderByNameAsc(Long modelId);

    List<CarVariant> findByCarModelIdOrderByNameAsc(Long modelId);

    boolean existsByNameAndCarModelId(String name, Long modelId);

    boolean existsByNameAndCarModelIdAndIdNot(String name, Long modelId, Long id);
}