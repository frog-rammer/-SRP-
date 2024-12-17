package com.procuone.mit_kdt.service;

import com.procuone.mit_kdt.dto.ProcumentPlanDTO;
import com.procuone.mit_kdt.entity.ProcurementPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
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

    // 조달 계획을 수정하는 메소드
    boolean updateProcurementPlan(String procurementPlanCode,
                                  int quantity,
                                  int procurementQuantity,
                                  String planStartDate,
                                  String planEndDate,
                                  String procurementEndDate);

    // 조달 계획을 삭제하는 메소드
    boolean deleteProcurementPlan(String procurementPlanCode);
}
