package com.procuone.mit_kdt.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;

@Entity
@Table(name = "inspection")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Inspection {

    @Id
    @GeneratedValue(generator = "custom-id")
    @GenericGenerator(
            name = "custom-id",
            strategy = "com.procuone.mit_kdt.customidGenerator.CustomIdGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "prefix", value = "inspection_"),
                    @org.hibernate.annotations.Parameter(name = "tableName", value = "inspection"),
                    @org.hibernate.annotations.Parameter(name = "columnName", value = "inspection_code")
            }
    )
    private String inspectionCode; // 검수 코드 (유니크)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_code", nullable = false)
    private DeliveryOrder deliveryOrder; // 납품 지시와 연관 관계

    @Column(name = "inspection_date", nullable = false)
    private LocalDate inspectionDate; // 검수 일자

    @Column(name = "product_name", nullable = false)
    private String productName; // 상품명

    @Column(name="product_code", nullable = false)
    private String productCode;

    @Column(name = "quantity", nullable = false)
    private Long quantity; // 납품 수량

    @Column(name = "defective_quantity", nullable = false)
    private Long defectiveQuantity; // 불량 수량

    @Column(name="busniess_id",nullable = false)
    private String busniessId;


    @Column(name = "inspection_status", nullable = false)
    private String inspectionStatus; // 검수 상태 ("검수 완료", "검수 중", "불량")

    @PrePersist
    public void prePersist() {
        this.inspectionDate = LocalDate.now(); // 저장 시 검수 일자 자동 설정
        if (this.inspectionStatus == null) {
            this.inspectionStatus = "검수 중"; // 초기 상태
        }
    }
}
