package com.procuone.mit_kdt.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class InspectionDTO {
    private String inspectionCode; // 검수 코드
    private String deliveryCode;   // 납품 코드 (DeliveryOrder와 연관된 코드)
    private LocalDate inspectionDate; // 검수 일자
    private String productName;    // 상품명
    private String productCode;    // 상품 코드
    private Long quantity;         // 납품 수량
    private Long defectiveQuantity; // 불량 수량
    private String busniessId;     // 사업자 번호
    private LocalDate deliveryDate; // 납품일
    private String inspectionStatus; // 검수 상태
}