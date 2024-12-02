package com.procuone.mit_kdt.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderDTO {
    private String purchaseOrderCode;
    private String productPlanCode;
    private String procurementPlanCode;
    private String productCode;
    private String businessId;
    private LocalDate procurementEndDate;
    private Long quantity;
    private Long Price; // 사용되지 않음
    private String status; // 사용되지 않음
    private String createdBy; // 사용되지 않음
    private LocalDate createdDate;
    private String updatedBy; // 사용되지 않음
    private LocalDate updatedDate; // 사용되지 않음
    private Long availableQuantity; // 추가된 필드: 납품 가능 수량
    private String comName; // 추가된 필드: 회사명
    private String ItemName; // 추가된 필드: 아이템이름

    // JPQL 쿼리에 사용되는 생성자
    public PurchaseOrderDTO(String purchaseOrderCode, String procurementPlanCode, String productPlanCode,
                            String businessId, String productCode, Long quantity,
                            LocalDate procurementEndDate, LocalDate createdDate) {
        this.purchaseOrderCode = purchaseOrderCode;
        this.procurementPlanCode = procurementPlanCode;
        this.productPlanCode = productPlanCode;
        this.businessId = businessId;
        this.productCode = productCode;
        this.quantity = quantity;
        this.procurementEndDate = procurementEndDate;
        this.createdDate = createdDate;
    }
}
