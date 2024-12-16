package com.procuone.mit_kdt.entity;

import com.procuone.mit_kdt.entity.BOM.Item;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {

    @Id
    @GeneratedValue(generator = "custom-id")
    @GenericGenerator(
            name = "custom-id",
            strategy = "com.procuone.mit_kdt.customidGenerator.CustomIdGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "prefix", value = "inventory_"),
                    @org.hibernate.annotations.Parameter(name = "tableName", value = "Inventory"),
                    @org.hibernate.annotations.Parameter(name = "columnName", value = "inventory_code")
            }
    )
    private String InventoryCode; // 재고 고유 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item; // item 테이블과의 연관 관계 (Many-to-One)

    @Column(name = "item_name", nullable = false)
    private String itemName; // 아이템 이름 (보조 정보)

    @Column(name = "current_quantity", nullable = false)
    private Integer currentQuantity; // 현재 재고 수량

    @Column(name = "reserved_quantity", nullable = false)
    private Integer reservedQuantity; // 예약된 수량

    @Column(name = "minimum_required", nullable = false)
    private Integer minimumRequired; // 최소 유지 수량

    @Column(name = "CoooperationCompanyInventoryId", length = 255)
    private String CoooperationCompanyInvertoryId; // 재고 위치 (창고, 지역 등)


    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated; // 마지막 업데이트 시간

    @PrePersist
    @PreUpdate
    public void setLastUpdated() {
        this.lastUpdated = LocalDateTime.now(); // 저장 또는 업데이트 시 마지막 업데이트 시간 설정
    }
}
