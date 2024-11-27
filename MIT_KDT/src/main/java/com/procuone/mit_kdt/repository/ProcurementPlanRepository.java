package com.procuone.mit_kdt.repository;

import com.procuone.mit_kdt.entity.ProcurementPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProcurementPlanRepository extends JpaRepository<ProcurementPlan, String> {

    // 제품명(productName) 또는 제품 코드(productCode)에 검색어가 포함된 데이터 조회
    Page<ProcurementPlan> findByProductNameContainingOrProductCodeContaining(String productName, String productCode, Pageable pageable);

    // 특정 상품 계획 코드(productPlanCode)로 데이터 조회
    List<ProcurementPlan> findByProductPlanCode(String productPlanCode);

    // 제품 계획 코드(productPlanCode)와 상품명(productName)으로 조건을 걸어 검색
    Page<ProcurementPlan> findByProductPlanCodeContainingOrProductNameContaining(String productPlanCode, String productName, Pageable pageable);
}
