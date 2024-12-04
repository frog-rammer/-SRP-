package com.procuone.mit_kdt.service.impl;

import com.procuone.mit_kdt.dto.InspectionDTO;
import com.procuone.mit_kdt.entity.DeliveryOrder;
import com.procuone.mit_kdt.entity.Inspection;
import com.procuone.mit_kdt.entity.PurchaseOrder;
import com.procuone.mit_kdt.repository.DeliveryOrderRepository;
import com.procuone.mit_kdt.repository.InspectionRepository;
import com.procuone.mit_kdt.repository.InventoryRepository;
import com.procuone.mit_kdt.repository.PurchaseOrderRepository;
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
    private final PurchaseOrderRepository purchaseOrderRepository;

    @Override
    public List<InspectionDTO> getAllInspections() {
        // 모든 Inspection 데이터를 조회하여 DTO로 변환
        return inspectionRepository.findAll().stream()
                .map(this::convertToDTO) // InspectionMapper의 convertToDTO 메서드 사용
                .toList();
    }

    @Override
    public void processInspection(InspectionDTO inspectionDTO) {
        // Inspection 데이터 업데이트
        Inspection inspection = inspectionRepository.findById(inspectionDTO.getInspectionCode())
                .orElseThrow(() -> new RuntimeException("Inspection 데이터를 찾을 수 없습니다."));

        Long defectiveQuantity = inspectionDTO.getDefectiveQuantity();
        inspection.setDefectiveQuantity(defectiveQuantity);
        inspection.setInspectionStatus(defectiveQuantity > 0 ? "검수 완료(불량)" : "검수 완료");
        // Inventory 처리
        if (defectiveQuantity == 0) {
            // 모든 수량 추가
            inventoryRepository.addToInventory(inspection.getProductName(), inspection.getQuantity());
        } else {
            // 불량 수량 제외하고 추가
            inventoryRepository.addToInventory(inspection.getProductName(), inspection.getQuantity() - defectiveQuantity);

            // PurchaseOrder 상태 긴급 처리
            PurchaseOrder purchaseOrder = inspection.getDeliveryOrder().getPurchaseOrder();
            purchaseOrder.setStatus("긴급");
            purchaseOrderRepository.save(purchaseOrder);
        }
        
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

    @Override
    public String generateInvoice(String inspectionId) {
        Inspection inspection = inspectionRepository.findById(inspectionId)
                .orElseThrow(() -> new RuntimeException("Inspection 데이터를 찾을 수 없습니다."));

        // 거래명세서 HTML 반환 (동적으로 생성)
        return """
        <html>
        <head><title>거래 명세서</title></head>
        <body>
            <h1>거래 명세서</h1>
            <p>발주 번호: %s</p>
            <p>품목명: %s</p>
            <p>수량: %d</p>
            <p>단가: %d</p>
            <p>총액: %d</p>
            <p>상태: %s</p>
        </body>
        </html>
        """.formatted(
                inspection.getDeliveryOrder().getPurchaseOrder().getPurchaseOrderCode(),
                inspection.getProductName(),
                inspection.getQuantity(),
                inspection.getDeliveryOrder().getPrice(),
                inspection.getQuantity() * inspection.getDeliveryOrder().getPrice(),
                inspection.getInspectionStatus()
        );
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