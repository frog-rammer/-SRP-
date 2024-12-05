package com.procuone.mit_kdt.service.impl;

import com.procuone.mit_kdt.dto.ContractDTO;
import com.procuone.mit_kdt.dto.InspectionDTO;
import com.procuone.mit_kdt.entity.*;
import com.procuone.mit_kdt.repository.*;
import com.procuone.mit_kdt.service.InspectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InspectionServiceImpl implements InspectionService {

    @Autowired
    private final InspectionRepository inspectionRepository;

    @Autowired
    private final InventoryRepository inventoryRepository;
    @Autowired
    private final DeliveryOrderRepository deliveryOrderRepository;

    @Autowired
    private final DefectRepository defectRepository;

    @Autowired
    private final ContractRepository contractRepository;

    @Override
    public List<InspectionDTO> getAllInspections() {
        // 모든 Inspection 데이터를 조회하여 DTO로 변환
        return inspectionRepository.findAll().stream()
                .map(this::convertToDTO) // InspectionMapper의 convertToDTO 메서드 사용
                .toList();
    }

    @Override
    public InspectionDTO getInspectionById(String inspectionId) {
        Inspection inspection = inspectionRepository.findById(inspectionId)
                .orElseThrow(() -> new RuntimeException("Inspection 데이터를 찾을 수 없습니다."));
        return convertToDTO(inspection);
    }

    @Override
    public ContractDTO getContractByInspection(InspectionDTO inspectionDTO) {
        // 계약 정보는 납품 코드나 품목 코드를 통해 가져옵니다.

        Contract contract = contractRepository.findContractByBusinessIdAndProductCode(inspectionDTO.getBusniessId(),inspectionDTO.getProductCode());
        // 계약 정보가 없을 경우 예외 처리
        if (contract == null) {
            throw new RuntimeException("No contract found for the given business ID and product code");
        }
        return ContractDTO.builder()
                .id(contract.getId())
                .businessId(contract.getCompany().getBusinessId())
                .productCode(contract.getItem().getProductCode())
                .comName(contract.getComName())
                .itemName(contract.getItemName())
                .accountInfo(contract.getAccountInfo())
                .unitCost(contract.getUnitCost())
                .leadTime(contract.getLeadTime())
                .contractDate(contract.getContractDate())
                .contractEndDate(contract.getContractEndDate())
                .productionQty(contract.getProductionQty())
                .contractStatus(contract.getContractStatus())
                .build();
    }

    @Override
    public void processInspection(InspectionDTO inspectionDTO) {
        // Inspection 데이터 조회
        Inspection inspection = inspectionRepository.findById(inspectionDTO.getInspectionCode())
                .orElseThrow(() -> new RuntimeException("Inspection 데이터를 찾을 수 없습니다."));

        // 상태 변경
        Long defectiveQuantity = inspectionDTO.getDefectiveQuantity();
        inspection.setDefectiveQuantity(defectiveQuantity);
        inspection.setInspectionStatus(defectiveQuantity > 0 ? "검수완료(불량)" : "검수완료");

        // 재고 처리
        if (defectiveQuantity == 0) {
            // 불량 수량이 없는 경우 전체 수량 추가
            inventoryRepository.addToInventory(inspection.getProductName(), inspection.getQuantity());
        } else {
            // 불량 수량 제외 후 재고에 추가
            inventoryRepository.addToInventory(inspection.getProductName(), inspection.getQuantity() - defectiveQuantity);

            // Defect 테이블에 불량 정보 저장
            Defect defect = Defect.builder()
                    .businessCode(inspection.getBusniessId()) // 비즈니스 코드
                    .defectiveQuantity(defectiveQuantity) // 불량 수량
                    .productCode(inspection.getProductCode()) // 제품 코드
                    .defectDate(LocalDate.now()) // 현재 날짜를 불량 발생 날짜로 설정
                    .build();

            defectRepository.save(defect); // Defect 데이터 저장
        }

        // 변경된 Inspection 저장
        inspectionRepository.save(inspection);
    }

    @Override
    public void saveInspection(InspectionDTO inspectionDto) {
        // DeliveryOrder 객체 조회 (DeliveryCode로 연관된 엔티티 가져오기)
        DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(inspectionDto.getDeliveryCode())
                .orElseThrow(() -> new RuntimeException("DeliveryOrder not found with code: " + inspectionDto.getDeliveryCode()));

        // DTO -> Entity 변환
        Inspection inspection = convertToEntity(inspectionDto, deliveryOrder);

        // 검수 상태와 날짜 설정 (기본값 처리)
        inspection.setInspectionDate(LocalDate.now()); // 현재 날짜 설정
        inspection.setInspectionStatus("검수중"); // 기본 상태

        // Inspection 엔티티 저장
        inspectionRepository.save(inspection);
    }

    // DTO -> Entity 변환
    @Override
    public Inspection convertToEntity(InspectionDTO dto, DeliveryOrder deliveryOrder) {
        return Inspection.builder()
                .inspectionCode(dto.getInspectionCode())
                .deliveryOrder(deliveryOrder) // DeliveryOrder 객체를 매핑
                .inspectionDate(dto.getInspectionDate())
                .productName(dto.getProductName())
                .productCode(dto.getProductCode())
                .quantity(dto.getQuantity())
                .defectiveQuantity(dto.getDefectiveQuantity())
                .busniessId(dto.getBusniessId())
                .deliveryDate(dto.getDeliveryDate())
                .inspectionStatus(dto.getInspectionStatus())
                .build();
    }

    // Entity -> DTO 변환
    @Override
    public InspectionDTO convertToDTO(Inspection entity) {
        return InspectionDTO.builder()
                .inspectionCode(entity.getInspectionCode())
                .deliveryCode(entity.getDeliveryOrder() != null ? entity.getDeliveryOrder().getDeliveryCode() : null)
                .inspectionDate(entity.getInspectionDate())
                .productName(entity.getProductName())
                .productCode(entity.getProductCode())
                .quantity(entity.getQuantity())
                .defectiveQuantity(entity.getDefectiveQuantity())
                .busniessId(entity.getBusniessId())
                .deliveryDate(entity.getDeliveryDate())
                .inspectionStatus(entity.getInspectionStatus())
                .build();
    }
}