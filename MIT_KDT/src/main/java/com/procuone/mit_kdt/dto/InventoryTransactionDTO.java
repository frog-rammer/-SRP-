package com.procuone.mit_kdt.dto;

import jakarta.persistence.Column;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryTransactionDTO {
    private String transactionCode;       // 거래 코드
    private String procurementCode;
    private String inventoryCode;             // 재고 ID
    private String productCode;           // 제품 코드 (unique, not null)
    private String businessId;
    private String transactionType;       // 거래 유형 ("입고", "출고", "불량품")
    private Long quantity;                // 거래 수량
    private LocalDate transactionDate; // 거래 일시
    private Double transactionValue;      // 거래 금액 (선택적)
    private String relatedOrderCode;      // 관련 발주서 또는 납품 지시 코드
}