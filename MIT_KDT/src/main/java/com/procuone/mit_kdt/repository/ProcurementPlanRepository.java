package com.procuone.mit_kdt.repository;

import com.procuone.mit_kdt.entity.ProcurementPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProcurementPlanRepository extends JpaRepository<ProcurementPlan, String> {
    // 필요 시 추가적인 커스텀 메서드 정의 가능
    List<ProcurementPlan> findByProductPlanCode(String productPlanCode);
}
