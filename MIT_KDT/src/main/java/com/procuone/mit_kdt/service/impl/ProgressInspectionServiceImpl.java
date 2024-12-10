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
                                                                 Pageable pageable) {
        Page<ProgressInspection> inspections = progressInspectionRepository.searchByFilters(
                productCodeQuery, productNameQuery, procurementPlanCodeQuery, dateStart, dateEnd, pageable);

        // 리드타임 계산과 DTO 변환
        return inspections.map(this::calculateExpectedArrivalDateAndConvertToDTO);
    }

    // 리드타임 계산 및 DTO 변환
    private ProgressInspectionDTO calculateExpectedArrivalDateAndConvertToDTO(ProgressInspection entity) {
        ProgressInspectionDTO dto = entityToDto(entity);

        PurchaseOrder purchaseOrder = entity.getPurchaseOrder();
        if (purchaseOrder != null) {
            String businessId = purchaseOrder.getBusinessId();
            String productCode = purchaseOrder.getProductCode();

            // Contract 레포지토리 사용
            Contract contract = contractRepository.findContractByBusinessIdAndProductCode(businessId, productCode);

            if (contract != null) {
                int leadTime = contract.getLeadTime(); // 리드타임 (일 단위)
                int productionQty = contract.getProductionQty(); // 리드타임당 생산량

                Long totalQuantity = entity.getTotalQuantity(); // 발주된 총 수량
                LocalDate orderDate = entity.getOrderDate(); // 발주일

                if (totalQuantity != null && productionQty > 0 && leadTime > 0) {
                    // 필요한 리드타임 계산
                    int requiredLeadTimes = (int) Math.ceil((double) totalQuantity / productionQty);

                    // 입고 예정일 계산
                    LocalDate expectedArrivalDate = orderDate.plusDays((long) (requiredLeadTimes - 1) * leadTime);
                    dto.setExpectedArrivalDate(expectedArrivalDate); // LocalDate로 설정
                } else {
                    dto.setExpectedArrivalDate(null); // 계산 불가 시 null 처리
                }
            } else {
                dto.setExpectedArrivalDate(null); // 계약 정보가 없으면 null
            }
        } else {
            dto.setExpectedArrivalDate(null); // 발주서 정보가 없으면 null
        }

        return dto;
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
                .orderDate(entity.getOrderDate())
                .expectedArrivalDate(null) // expectedArrivalDate는 calculateExpectedArrivalDateAndConvertToDTO에서 계산
                .build();
    }

}
