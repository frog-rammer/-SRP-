package com.procuone.mit_kdt.service;

import com.procuone.mit_kdt.dto.InspectionDTO;

import java.util.List;

public interface InspectionService {
    List<InspectionDTO> getAllInspections();

    void processInspection(InspectionDTO inspectionDTO);
    void saveInspection(InspectionDTO inspectionDto);
    String generateInvoice(String inspectionId);
}
