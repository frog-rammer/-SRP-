package com.procuone.mit_kdt.service.impl;

import com.procuone.mit_kdt.dto.InspectionDTO;
import com.procuone.mit_kdt.entity.Inspection;
import com.procuone.mit_kdt.repository.InspectionRepository;
import com.procuone.mit_kdt.service.InspectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class InspectionServiceImpl implements InspectionService {

    @Autowired
    private final InspectionRepository inspectionRepository;

    @Override
    public void saveInspection(InspectionDTO inspectionDto) {
        Inspection inspection = Inspection.builder()
                .deliveryOrder(inspectionDto.getDeliveryOrder())
                .inspectionDate(LocalDate.now())
                .productName(inspectionDto.getProductName())
                .quantity(inspectionDto.getQuantity())
                .defectiveQuantity(inspectionDto.getDefectiveQuantity())
                .inspectionStatus(inspectionDto.getDefectiveQuantity() > 0 ? "검수 완료(불량)" : "검수 완료")
                .build();

        inspectionRepository.save(inspection);
    }
}