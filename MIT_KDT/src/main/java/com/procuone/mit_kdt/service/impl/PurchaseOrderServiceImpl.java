package com.procuone.mit_kdt.service.impl;

import com.procuone.mit_kdt.dto.ProcumentPlanDTO;
import com.procuone.mit_kdt.dto.PurchaseOrderDTO;
import com.procuone.mit_kdt.entity.BOM.BOMRelationship;
import com.procuone.mit_kdt.entity.BOM.PurchaseBOM;
import com.procuone.mit_kdt.entity.PurchaseOrder;
import com.procuone.mit_kdt.repository.BOMRelationshipRepository;
import com.procuone.mit_kdt.repository.PurchaseBOMRepository;
import com.procuone.mit_kdt.repository.PurchaseOrderRepository;
import com.procuone.mit_kdt.service.PurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;
    @Autowired
    private BOMRelationshipRepository BOMRelationshipRepository;
    @Autowired
    private PurchaseBOMRepository purchaseBOMRepository;

    @Override
    public void registerPurchaseOrder(ProcumentPlanDTO procurementPlanDTO) {
        // 1. 부모 품목(생산 품목)에 대한 하위 품목 가져오기
        List<BOMRelationship> childItems = BOMRelationshipRepository.findChildItemsByParentProductCode(procurementPlanDTO.getProductCode());

        // 2. 하위 품목들의 수량 계산 및 조달 계획에 따른 발주서 생성
        List<PurchaseOrderDTO> purchaseOrderList = new ArrayList<>();
        for (BOMRelationship relationship : childItems) {
            String childProductCode = relationship.getChildItem().getProductCode();
            long requiredQuantity = relationship.getQuantity() * procurementPlanDTO.getProcurementQuantity();

            // 3. P-BOM에서 각 하위 품목의 단일 사업자 ID 가져오기
            String businessId = purchaseBOMRepository.findBusinessIdByProductCode(childProductCode);
            if (businessId == null) {
                throw new IllegalStateException("사업자 ID를 찾을 수 없습니다: 하위 품목 코드 " + childProductCode);
            }

            PurchaseOrderDTO purchaseOrderDTO = new PurchaseOrderDTO();

            // 단가 및 총 결제 금액 계산 (단가는 예시로 지정, 실제 로직에 따라 수정)
            Long pricePerUnit = 100L; // 예시 단가 (필요하면 추가 조회 로직 구현)
            Long totalPrice = pricePerUnit * requiredQuantity;

            // 5. 발주 상태 기본값 설정
            purchaseOrderDTO.setStatus("발주서 자동 생성");

            // 6. 발주서 DTO 구성
            purchaseOrderDTO.setProductPlanCode(procurementPlanDTO.getProductPlanCode());
            purchaseOrderDTO.setProcurementPlanCode(procurementPlanDTO.getProcurementPlanCode());
            purchaseOrderDTO.setProductCode(childProductCode);
            purchaseOrderDTO.setBusinessId(businessId);
            purchaseOrderDTO.setProcurementEndDate(procurementPlanDTO.getProcurementEndDate());
            purchaseOrderDTO.setQuantity(requiredQuantity);
            purchaseOrderDTO.setPrice(totalPrice);
            purchaseOrderDTO.setCreatedBy("SYSTEM");
            purchaseOrderDTO.setCreatedDate(LocalDate.now());

            // DTO 리스트에 추가
            purchaseOrderList.add(purchaseOrderDTO);
        }

        // 7. 발주서 테이블에 저장
        List<PurchaseOrder> purchaseOrders = purchaseOrderList.stream()
                .map(this::dtoToEntity)
                .collect(Collectors.toList());

        purchaseOrderRepository.saveAll(purchaseOrders);
    }




    private PurchaseOrder dtoToEntity(PurchaseOrderDTO dto) {
        return PurchaseOrder.builder()
                .purchaseOrderCode(dto.getPurchaseOrderCode())
                .productPlanCode(dto.getProductPlanCode())
                .procurementPlanCode(dto.getProcurementPlanCode())
                .productCode(dto.getProductCode())
                .businessId(dto.getBusinessId())
                .procurementEndDate(dto.getProcurementEndDate())
                .quantity(dto.getQuantity())
                .Price(dto.getPrice())
                .status(dto.getStatus())
                .createdBy(dto.getCreatedBy())
                .createdDate(dto.getCreatedDate())
                .updatedBy(dto.getUpdatedBy())
                .updatedDate(dto.getUpdatedDate())
                .build();
    }


}
