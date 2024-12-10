package com.procuone.mit_kdt.entity;

import jakarta.persistence.*;
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
public class ProgressInspection {

    @Id
    @GeneratedValue(generator = "custom-id")
    @GenericGenerator(
            name = "custom-id",
            strategy = "com.procuone.mit_kdt.customidGenerator.CustomIdGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "prefix", value = "ProgressIns_"),
                    @org.hibernate.annotations.Parameter(name = "tableName", value = "progress_inspection"),
                    @org.hibernate.annotations.Parameter(name = "columnName", value = "progress_inspection_code")
            }
    )
    private String progressInspectionCode;

    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩을 통해 성능 최적화
    @JoinColumn(name = "purchase_order_code", referencedColumnName = "purchaseOrderCode", nullable = false)
    private PurchaseOrder purchaseOrder; // 발주서 연계 객체

    @Column(nullable = false)
    private String businessId;

    @Column(nullable = false)
    private String comName;

    @Column(nullable = false)
    private String productCode; // DB에 저장할 품목 코드

    @Column(nullable = false)
    private String productName; // 품목명 (필요시 다른 테이블에서 가져오기)

    @Column(nullable = false)
    private Long totalQuantity; // 발주 수량

    @Column(nullable = false)
    private Long inspectedQuantity; // 검수된 수량

    @Column(nullable = false)
    private String inspectionStatus; // 검수 상태 ("검수 중", "검수 완료" 등)

    @Column(nullable = false)
    private LocalDate inspectionDate; // 검수 일자

    @Column(nullable = false)
    private LocalDate orderDate; // 발주 일자
}
