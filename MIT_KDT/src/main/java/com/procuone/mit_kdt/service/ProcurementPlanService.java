package com.procuone.mit_kdt.service;

import com.procuone.mit_kdt.dto.ProcumentPlanDTO;
import com.procuone.mit_kdt.entity.ProcurementPlan;

import java.util.List;

public interface ProcurementPlanService {
    ProcumentPlanDTO registerProcurementPlan(ProcumentPlanDTO dto);
    long getRequiredProcurementQuantity(String productionPlanId);
    // DTO ↔ Entity 변환 메서드
    ProcurementPlan dtoToEntity(ProcumentPlanDTO dto);
    ProcumentPlanDTO entityToDto(ProcurementPlan entity);

    String search(ProcumentPlanDTO procumentPlanDTO);

    List<ProcurementPlan> getAllProcurementPlans(); // 모든 조달 계획 조회

    // 조달계획 조회
    ProcurementPlan getProcurementPlanByCode(String procurementPlanCode);

    // 조달계획 업데이트
    void updateProcurementPlan(ProcurementPlan procurementPlan);
}
