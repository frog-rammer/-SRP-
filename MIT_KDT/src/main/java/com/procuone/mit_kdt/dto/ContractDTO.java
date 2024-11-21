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
    private String conitemNo;         // 계약 항목 번호
    private String businessId;        // 사업자 번호
    private String companyName;       // 회사명
    private String productCode;       // 품목 코드
    private String itemName;          // 품목명
    private Integer contractPrice;    // 계약 가격
    private Integer unitCost;         // 단가
    private Integer leadTime;         // 소요 시간
    private String contractFile;      // 계약서 파일 경로
    private String contractInfo;      // 계약 정보
    private Date contractDate;        // 계약 날짜
    private boolean contractStatus;   // 계약 상태 (예: 활성화/비활성화)

    // 필요한 경우 생성된 DTO에 맞는 추가 메소드도 작성 가능
}
