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
    private Long Price;
    private String status;
    private String createdBy;
    private LocalDate createdDate;
    private String updatedBy;
    private LocalDate updatedDate;
}
