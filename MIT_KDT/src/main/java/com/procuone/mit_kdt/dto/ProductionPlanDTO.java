package com.procuone.mit_kdt.dto;


import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductionPlanDTO {
    
    // 수정 해야함
    private String productPlanCode;       // 생산 기준정보 코드
    private String productName;       // 생산제품명
    private String productCode;       // 생산제품코드
    private LocalDate planStartDate;  // 생산 시작일
    private LocalDate planEndDate;    // 생산 종료일
    private Integer quantity;         // 생산 수량
    private String partCode;          // 부품 코드
}
