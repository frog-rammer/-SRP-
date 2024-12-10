package com.procuone.mit_kdt.service;

import com.procuone.mit_kdt.dto.ContractDTO;
import com.procuone.mit_kdt.dto.InspectionDTO;
import com.procuone.mit_kdt.entity.DeliveryOrder;
import com.procuone.mit_kdt.entity.Inspection;

import java.util.List;

public interface InspectionService {
    List<InspectionDTO> getAllInspections();
    void processInspection(InspectionDTO inspectionDTO);
    void saveInspection(InspectionDTO inspectionDto);
    // DTO <-> Entity
    Inspection convertToEntity(InspectionDTO dto, DeliveryOrder deliveryOrder);
    InspectionDTO convertToDTO(Inspection entity);
    InspectionDTO getInspectionById(String inspectionId);
    ContractDTO getContractByInspection(InspectionDTO inspectionDTO);
}
