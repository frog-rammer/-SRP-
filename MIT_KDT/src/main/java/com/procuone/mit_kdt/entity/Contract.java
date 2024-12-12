package com.procuone.mit_kdt.entity;

import com.procuone.mit_kdt.entity.BOM.Item;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;


@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "business_id", referencedColumnName = "businessId", nullable = false)
    private Company company;

    @ManyToOne
    @JoinColumn(name = "product_code", referencedColumnName = "productCode", nullable = false)
    private Item item;

    @Column(nullable = false)
    private Date contractDate;

    @Column(nullable = false)
    private Date contractEndDate; // 계약 종료일 추가

    @Column(length = 255)
    private String comName;

    @Column(length = 255)
    private String itemName;

    @Column(length = 255)
    private String accountInfo;      // 계좌 정보

    @Column(nullable = true)
    private Integer unitCost;

    @Column(nullable = false)
    private Integer productionQty; // 최소 생산 수량

    @Column(nullable = true)
    private Integer supplyUnit;
    
    @Column(nullable = true)
    private Integer leadTime;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean contractStatus=false;


    @PrePersist
    public void prePersist() {
        if (contractStatus == null) {
            contractStatus = false;
        }
    }
}

