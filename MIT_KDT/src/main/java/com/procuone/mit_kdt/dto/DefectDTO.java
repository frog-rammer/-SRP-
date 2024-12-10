package com.procuone.mit_kdt.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class DefectDTO {

    private Long id; // 고유 식별자
    private String businessCode; // 비즈니스 코드
    private Long defectiveQuantity; // 불량 개수
    private String productCode; // 제품 코드
    private LocalDate defectDate; // 불량 발생 날짜
}