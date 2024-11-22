package com.procuone.mit_kdt.entity;

import com.procuone.mit_kdt.entity.BOM.Item;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;
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

    @Column(unique = true, nullable = false)
    private String conitemNo;

    @ManyToOne
    @JoinColumn(name = "business_id", referencedColumnName = "businessId", nullable = false)
    private Company company;

    @ManyToOne
    @JoinColumn(name = "product_code", referencedColumnName = "productCode", nullable = false)
    private Item item;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date contractDate;

    @Column(length = 255)
    private String contractFile;

    @Column(length = 255)
    private String contractInfo;

    @Column(nullable = false)
    private Integer contractPrice;

    @Column(length = 255)
    private String companyName;

    @Column(length = 255)
    private String itemName;

    @Column(nullable = true)
    private Integer unitCost;

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

