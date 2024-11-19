package com.procuone.mit_kdt.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

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
    @GeneratedValue(generator = "custom-id")
    @GenericGenerator(
            name = "custom-id",
            strategy = "com.procuone.mit_kdt.customidGenerator.CustomIdGenerator"
    )
    private String productPlanCode;   // 생산 계획 기준정보 코드
    private String productName;       // 생산제품명
    private String productCode;       // 생산제품코드
    private LocalDate planStartDate;  // 생산 시작일
    private LocalDate planEndDate;    // 생산 종료일
    private Integer quantity;         // 생산 수량
}
