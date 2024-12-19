package com.procuone.mit_kdt.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DeliveryOrderDTO {
    private String deliveryCode; // 납품 지시 코드
    private String purchaseOrderCode; // 발주서 코드 (PurchaseOrder 연관 필드)
    private LocalDate expectedArrivalDate;// 입고예정일
    private Long totalQuantity; // 발주수량
    private String businessId;
    private String productCode; // 품목 코드
    private Long price;
    private Long deliveryQuantity; // 납품 수량
    private LocalDate deliveryDate; // 납품일
    private String status; // 납품 상태
    private LocalDate createdDate; // 생성일
}
