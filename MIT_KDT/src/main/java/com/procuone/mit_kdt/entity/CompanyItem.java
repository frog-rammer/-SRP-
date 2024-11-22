package com.procuone.mit_kdt.entity;

import com.procuone.mit_kdt.entity.BOM.Item;
import jakarta.persistence.*;
import lombok.*;

import java.util.Optional;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CompanyItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "businessId", referencedColumnName = "businessId", nullable = false)
    @ToString.Exclude // 순환 참조 방지
    private Company company;

    @ManyToOne
    @JoinColumn(name = "item_id", referencedColumnName = "id", nullable = false)
    private Item item;

    private Integer leadTime; // 생산 L/T
    private Integer supplyUnit; // 최소 공급 단위
    private Integer productionQty; // 최소 생산 수량
    private Integer unitCost; // 단가
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean contractStatus=false;



    @PrePersist
    public void prePersist() {
        if (contractStatus == null) {
            contractStatus = false;
        }
    }
}
