package com.procuone.mit_kdt.service;

import com.procuone.mit_kdt.dto.ContractDTO;
import com.procuone.mit_kdt.dto.InspectionDTO;
import com.procuone.mit_kdt.entity.DeliveryOrder;
import com.procuone.mit_kdt.entity.Inspection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface InspectionService {
    List<InspectionDTO> getAllInspections();
    void processInspection(InspectionDTO inspectionDTO);
    void saveInspection(InspectionDTO inspectionDto);
    InspectionDTO getInspectionById(String inspectionId);
    ContractDTO getContractByInspection(InspectionDTO inspectionDTO);
    // 상태별로 페이지네이션된 InspectionDTO 가져오기
    Page<InspectionDTO> getInspectionsByStatus(String status, Pageable pageable);
    // DTO <-> Entity
    Inspection convertToEntity(InspectionDTO dto, DeliveryOrder deliveryOrder);
    InspectionDTO convertToDTO(Inspection entity);

}
