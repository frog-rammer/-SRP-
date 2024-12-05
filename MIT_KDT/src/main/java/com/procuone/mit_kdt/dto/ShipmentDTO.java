package com.procuone.mit_kdt.dto;

import lombok.*;

import java.time.LocalDate;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ShipmentDTO {
    private String shipmentId; // 출고번호
    private LocalDate requestDate; // 요청일
    private String manager; // 담당자
    private String procurementPlanCode;//조달계획 코드
    private String productPlanCode; // 생산계획 코드
    private LocalDate shipmentDate; // 출고일
    private String productCode; // 자재코드
    private String itemName; // 자재명
    private Long currentQuantity; // 현재 수량
    private Long requestedQuantity; // 요청 수량
    private LocalDate registrationDate; // 등록일
    private LocalDate updateDate; // 수정일
    private String shipmentStatus; // 출고 상태
}
