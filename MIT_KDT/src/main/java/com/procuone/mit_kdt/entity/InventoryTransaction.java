package com.procuone.mit_kdt.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class InventoryTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "inventory_id", nullable = false)
    private Inventory inventory;

    @Column(name = "transaction_type", nullable = false)
    private String transactionType; // "입고", "출고", "불량품"

    @Column(name = "quantity", nullable = false)
    private Long quantity;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    @Column(name = "transaction_value")
    private Double transactionValue; // 입출고 금액 (선택적)

    @Column(name = "related_order_id")
    private String relatedOrderId; // 관련 발주서 또는 납품 지시 코드
}

