package com.procuone.mit_kdt.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class InventoryTransaction {
    @Id
    @GeneratedValue(generator = "custom-id")
    @GenericGenerator(
            name = "custom-id",
            strategy = "com.procuone.mit_kdt.customidGenerator.CustomIdGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "prefix", value = "transaction_"),
                    @org.hibernate.annotations.Parameter(name = "tableName", value = "inventoryTransaction"),
                    @org.hibernate.annotations.Parameter(name = "columnName", value = "transaction_Code")
            }
    )
    private String transactionCode;

    @ManyToOne
    @JoinColumn(name = "inventory_id", nullable = false)
    private Inventory inventory;

    @Column(unique = true,nullable = false)
    private String productCode;

    @Column(name = "transaction_type", nullable = false)
    private String transactionType; // "입고", "출고", "불량품"

    @Column(name = "quantity", nullable = false)
    private Long quantity;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    @Column(name = "transaction_value")
    private Double transactionValue; // 입출고 금액 (선택적)

    @Column(name = "related_order_code")
    private String relatedOrderCode; // 관련 발주서 또는 납품 지시 코드
}

