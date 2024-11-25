package com.procuone.mit_kdt.entity;

import com.procuone.mit_kdt.entity.BOM.Item;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "company_inventory")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CompanyInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 재고 고유 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", referencedColumnName = "businessId", nullable = false)
    @ToString.Exclude
    private Company company; // 업체 정보 (Company 테이블과 연관)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", referencedColumnName = "id", nullable = false)
    @ToString.Exclude
    private Item item; // 품목 정보 (Item 테이블과 연관)

    @Column(name = "current_quantity", nullable = false)
    private Integer currentQuantity; // 현재 재고 수량

    @Column(name = "reserved_quantity", nullable = false)
    private Integer reservedQuantity; // 예약된 수량

    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated; // 마지막 업데이트 시간

    @PrePersist
    @PreUpdate
    public void setLastUpdated() {
        this.lastUpdated = LocalDateTime.now(); // 저장 또는 업데이트 시 마지막 업데이트 시간 설정
    }
}
