package com.procuone.mit_kdt.service;

import com.procuone.mit_kdt.dto.ProcumentPlanDTO;
import com.procuone.mit_kdt.entity.ProcurementPlan;

public interface ProcurementPlanService {
    void registerProcurementPlan(ProcumentPlanDTO dto);
    long getRequiredProcurementQuantity(String productionPlanId);
    // DTO ↔ Entity 변환 메서드
    ProcurementPlan dtoToEntity(ProcumentPlanDTO dto);
    ProcumentPlanDTO entityToDto(ProcurementPlan entity);
}
