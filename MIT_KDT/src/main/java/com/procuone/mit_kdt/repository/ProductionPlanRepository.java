package com.procuone.mit_kdt.repository;

import com.procuone.mit_kdt.entity.ProductionPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductionPlanRepository extends JpaRepository<ProductionPlan, String> {
}
