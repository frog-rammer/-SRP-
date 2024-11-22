package com.procuone.mit_kdt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractDTO {

    private Long id;                  // 계약 ID
    private String businessId;        // 사업자 번호
    private String productCode;       // 품목 코드
    private String comName;           // 회사명
    private String itemName;          // 품목명
    private String accountInfo;       // 계좌 정보
    private Integer unitCost;         // 단가
    private Integer leadTime;         // 소요 시간
    private Date contractDate;        // 계약 날짜
    private Integer productionQty;    // 최소 생산 수량
    private boolean contractStatus;   // 계약 상태 (예: 활성화/비활성화)
}
