package com.procuone.mit_kdt.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductionPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int planNo;               //생산 계획 번호
    private String productName;       // 생산제품명
    private String productCode;       // 생산제품코드
    private LocalDate planStartDate;  // 생산 시작일
    private LocalDate planEndDate;    // 생산 종료일
    private Integer quantity;         // 생산 수량
    private String partCode;          // 부품 코드
    private String companyName;       // 회사명
    private String companyNumber;     // 사업자번호
}
