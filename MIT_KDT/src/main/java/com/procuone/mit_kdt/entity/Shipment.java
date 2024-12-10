package com.procuone.mit_kdt.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Shipment {
    @Id
    @GeneratedValue(generator = "custom-id")
    @GenericGenerator(
            name = "custom-id",
            strategy = "com.procuone.mit_kdt.customidGenerator.CustomIdGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "prefix", value = "shipment_"),
                    @org.hibernate.annotations.Parameter(name = "tableName", value = "shipment"),
                    @org.hibernate.annotations.Parameter(name = "columnName", value = "shipment_id")
            }
    )
    private String shipmentId; // 출고번호
    private LocalDate requestDate; // 요청일
    private String manager; // 담당자
    private String procurementPlanCode; // 조달 계획 코드
    private String productPlanCode; // 생산 계획 코드
    private LocalDate shipmentDate; // 출고일
    private String productCode; // 자재코드
    private String itemName; // 자재명
    private Long currentQuantity; // 현재 수량
    private Long requestedQuantity; // 요청 수량
    private LocalDate registrationDate; // 등록일
    private LocalDate updateDate; // 수정일
    private String shipmentStatus; // 출고 상태
}