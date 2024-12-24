package com.procuone.mit_kdt.service.impl;

import com.procuone.mit_kdt.dto.ProgressInspectionDTO;
import com.procuone.mit_kdt.entity.Contract;
import com.procuone.mit_kdt.entity.ProgressInspection;
import com.procuone.mit_kdt.entity.PurchaseOrder;
import com.procuone.mit_kdt.repository.ContractRepository;
import com.procuone.mit_kdt.repository.ProgressInspectionRepository;
import com.procuone.mit_kdt.service.ProgressInspectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class ProgressInspectionServiceImpl implements ProgressInspectionService {

    @Autowired
    private ProgressInspectionRepository progressInspectionRepository;

    @Autowired
    private ContractRepository contractRepository;


    // 진척 검수 조회
    @Override
    public Page<ProgressInspectionDTO> searchProgressInspections(String productCodeQuery,
                                                                 String productNameQuery,
                                                                 String procurementPlanCodeQuery,
                                                                 LocalDate dateStart,
                                                                 LocalDate dateEnd,
                                                                 String inspectionStatus,
                                                                 Pageable pageable) {
        Page<ProgressInspection> inspections = progressInspectionRepository.searchByFilters(
                productCodeQuery, productNameQuery, procurementPlanCodeQuery, dateStart, dateEnd, inspectionStatus, pageable);

        return inspections.map(this::entityToDto);
    }

    @Override
    public Page<ProgressInspectionDTO> getInspectionsByStatus(String businessId, String status1, String status2, Pageable pageable) {
        Page<ProgressInspection> inspections;

        if (status2 != null) {
            inspections = progressInspectionRepository.findByStatusIn(businessId, status1, status2, pageable);
        } else {
            inspections = progressInspectionRepository.findByStatus(businessId, status1, pageable);
        }

        return inspections.map(this::entityToDto);
    }

    @Override
    public Page<ProgressInspectionDTO> getInspectionsByCompleteStatus(String status, Pageable pageable) {
        Page<ProgressInspection> inspections;
        inspections = progressInspectionRepository.findByCompleteStatus(status, pageable);
        return inspections.map(this::entityToDto);
    }

    @Transactional
    @Override
    public void deleteProgressByProductCodeAndPurchaseOrderCodeAndBusinessId(String productCode, String purchaseOrderCode, String businessId) {
        progressInspectionRepository.deleteByProductCodeAndPurchaseOrderCodeAndBusinessId(productCode, purchaseOrderCode, businessId);
    }


    @Override
    public Page<ProgressInspectionDTO> getInspectionsByBusinessId(String businessId, Pageable pageable) {
        Page<ProgressInspection> inspections = progressInspectionRepository.findByBusinessId(businessId, pageable);
        // DTO 변환 및 반환
        return inspections.map(this::entityToDto);
    }

    @Override
    public void updateInspectedQuantity(String progressInspectionCode, Long newInspectionQuantity, String businessId) {
        // 기존 데이터 조회
        ProgressInspection inspection = progressInspectionRepository.findById(progressInspectionCode)
                .orElseThrow(() -> new IllegalArgumentException("검수 코드가 잘못되었습니다."));

        // 비즈니스 로직 검증
        if (!inspection.getBusinessId().equals(businessId)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }
        if (inspection.getInspectedQuantity() + newInspectionQuantity > inspection.getTotalQuantity()) {
            throw new IllegalArgumentException("검수 수량이 발주 수량을 초과할 수 없습니다.");
        }

        // 데이터 업데이트
        inspection.setInspectedQuantity(inspection.getInspectedQuantity() + newInspectionQuantity);
        inspection.setInspectionDate(LocalDate.now());
        if(inspection.getInspectedQuantity().equals(inspection.getTotalQuantity())) {
            inspection.setInspectionStatus("검수완료");
        }else{
            inspection.setInspectionStatus("검수진행중");
        }
        progressInspectionRepository.save(inspection);



    }

    @Override
    public Page<ProgressInspectionDTO> getAllInspectionsInspectedQuantityOverZero(Pageable pageable) {
        Page<ProgressInspection> inspections = progressInspectionRepository.findAllWithInspectedQuantityGreaterThanZero(pageable);
        // DTO 변환 및 반환
        return inspections.map(this::entityToDto);
    }

    // DTO -> Entity 변환
    @Override
    public ProgressInspection dtoToEntity(ProgressInspectionDTO dto, PurchaseOrder purchaseOrder) {
        return ProgressInspection.builder()
                .progressInspectionCode(dto.getProgressInspectionCode())
                .purchaseOrder(purchaseOrder) // 연관된 발주서 객체 설정
                .businessId(purchaseOrder.getBusinessId())
                .comName(dto.getComName())
                .productCode(dto.getProductCode())
                .productName(dto.getProductName())
                .totalQuantity(dto.getTotalQuantity())
                .expectedArrivalDate(dto.getExpectedArrivalDate())
                .inspectedQuantity(dto.getInspectedQuantity())
                .inspectionStatus(dto.getInspectionStatus())
                .inspectionDate(dto.getInspectionDate())
                .orderDate(dto.getOrderDate())
                .build();
    }

    // Entity -> DTO 변환
    @Override
    public ProgressInspectionDTO entityToDto(ProgressInspection entity) {
        return ProgressInspectionDTO.builder()
                .progressInspectionCode(entity.getProgressInspectionCode())
                .purchaseOrderCode(entity.getPurchaseOrder().getPurchaseOrderCode()) // 발주서 코드 가져오기
                .businessId(entity.getPurchaseOrder().getBusinessId())
                .comName(entity.getComName())
                .productCode(entity.getProductCode())
                .productName(entity.getProductName())
                .totalQuantity(entity.getTotalQuantity())
                .inspectedQuantity(entity.getInspectedQuantity())
                .inspectionStatus(entity.getInspectionStatus())
                .inspectionDate(entity.getInspectionDate())
                .expectedArrivalDate(entity.getExpectedArrivalDate())
                .orderDate(entity.getOrderDate())
                .build();
    }

}
