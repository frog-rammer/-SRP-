package com.procuone.mit_kdt.repository;

import com.procuone.mit_kdt.entity.ProductionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProductionPlanRepository extends JpaRepository<ProductionPlan, String> {
    @Query("SELECT MAX(p.id) FROM ProductionPlan p")
    Optional<String> findMaxId();
}
