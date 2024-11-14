package com.procuone.mit_kdt.entity;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(length = 1000) // 아이템 코드 리스트를 저장하는 필드
    private String itemCodes; // 회사가 관리하는 아이템 코드 리스트 (콤마로 구분)

}
