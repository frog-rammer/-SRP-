package com.procuone.mit_kdt.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Company {
    @Id
    @Column(length = 20) // 사업자 번호 길이 제한
    private String businessId; // 사업자 번호
    private String comAccount; // 회사 계좌
    private String comAdd; // 회사 주소
    private String comEmail; // 회사 이메일
    private String comManage; // 회사 담당자 이름
    private String comName; // 회사 이름
    private String comPhone; // 회사 연락처
    private String comPaymentInfo; // 결제 정보
    private String comBank; // 은행명
    private String comAccountNumber; // 계좌번호
    private String comEstimateList; // 견적 목록


    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private Set<CompanyItem> companyItems; // 회사가 공급하는 아이템 리스트

}
