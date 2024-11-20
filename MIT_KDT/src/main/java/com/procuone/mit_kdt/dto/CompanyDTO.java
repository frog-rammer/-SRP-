package com.procuone.mit_kdt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompanyDTO {
    private String businessId;       // 사업자 번호
    private String comAdd;           // 회사 주소
    private String comId;            // 회사 아이디
    private String comEmail;         // 회사 이메일
    private String comManage;        // 관리자이름
    private String comName;          // 업체명
    private String comPhone;         // 회사 번호
    private String comPaymentInfo;   // 결제 정보
    private String comBank;          // 은행명
    private String comAccountNumber; // 계좌번호
    private String comEstimateList;  // 견적 목록
}
