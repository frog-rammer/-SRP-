package com.procuone.mit_kdt.service;

import com.procuone.mit_kdt.dto.ProgressInspectionDTO;
import com.procuone.mit_kdt.entity.ProgressInspection;
import com.procuone.mit_kdt.entity.PurchaseOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface ProgressInspectionService {

    Page<ProgressInspectionDTO> searchProgressInspections(
            String productCodeQuery, String productNameQuery, String procurementPlanCodeQuery, LocalDate dateStart,LocalDate dateEnd, Pageable pageable);
    // 엔티티 < - > DTO 간 변환 메서드
    public ProgressInspection dtoToEntity(ProgressInspectionDTO dto, PurchaseOrder purchaseOrder);
    public ProgressInspectionDTO entityToDto(ProgressInspection entity);
}
