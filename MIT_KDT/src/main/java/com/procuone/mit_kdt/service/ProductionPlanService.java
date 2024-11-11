package com.procuone.mit_kdt.service;

import com.procuone.mit_kdt.dto.ProductionPlanDTO;

import java.util.List;

public interface ProductionPlanService{
    void savePlan(ProductionPlanDTO planDTO); // 생산 계획 저장
    ProductionPlanDTO getPlanById(String planNum); // 특정 생산 계획 조회
    List<ProductionPlanDTO> getAllPlans(); // 모든 생산 계획 조회
    void deletePlan(String planNum); // 생산 계획 삭제
}
