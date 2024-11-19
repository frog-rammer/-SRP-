package com.procuone.mit_kdt.entity;

import com.procuone.mit_kdt.entity.BOM.Item;
import jakarta.persistence.*;
import lombok.*;
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
    @JoinColumn(name = "business_id", nullable = false) // business_id로 수정
    private Company company; // 공급 업체 정보

    @ManyToOne
    @JoinColumn(name = "productCode", nullable = false)
    private Item item; // 품목 정보
    private Integer leadTime; // 생산 L/T
    private Integer supplyUnit; // 최소 공급 단위
    private Integer productionQty; // 최소 생산 수량
    private Integer unitCost; // 단가
}
