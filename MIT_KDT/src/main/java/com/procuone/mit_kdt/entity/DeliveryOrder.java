package com.procuone.mit_kdt.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;

@Entity
@Table(name = "delivery_order")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DeliveryOrder {
    @Id
    @GeneratedValue(generator = "custom-id")
    @GenericGenerator(
            name = "custom-id",
            strategy = "com.procuone.mit_kdt.customidGenerator.CustomIdGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "prefix", value = "deliveryOrder_"),
                    @org.hibernate.annotations.Parameter(name = "tableName", value = "delivery_order"),
                    @org.hibernate.annotations.Parameter(name = "columnName", value = "delivery_code")
            }
    )
    private String deliveryCode; // 납품 지시 코드 (유니크)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_code", nullable = false)
    private PurchaseOrder purchaseOrder; // 발주서와 연관 관계

    @Column(name="business_id",nullable = false)
    private String businessId;

    @Column(name = "product_code", nullable = false)
    private String productCode; // 품목 코드

    @Column(name = "delivery_quantity", nullable = false)
    private Long deliveryQuantity; // 납품 수량

    @Column(name = "delivery_date", nullable = false)
    private LocalDate deliveryDate; // 납품일

    @Column(name = "status", nullable = false)
    private String status; // 납품 상태 (예: "운송중", "완료")

    @Column(name ="price")
    private Long price;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDate createdDate; // 생성일

    @PrePersist
    public void prePersist() {
        this.createdDate = LocalDate.now(); // 엔티티 저장 시 생성일 자동 설정
    }
}
