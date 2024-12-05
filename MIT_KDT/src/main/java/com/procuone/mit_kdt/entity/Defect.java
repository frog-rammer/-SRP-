package com.procuone.mit_kdt.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Defect {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 기본 키

    @Column(name = "business_code", nullable = false, length = 50)
    private String businessCode; // 비즈니스 코드

    @Column(name = "defective_quantity", nullable = false)
    private Long defectiveQuantity; // 불량 개수

    @Column(name = "product_code", nullable = false, length = 50)
    private String productCode; // 제품 코드

    @Column(name = "defect_date", nullable = false)
    private LocalDate defectDate; // 불량 발생 날짜

}
