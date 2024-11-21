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
    private Long id; // 기본 키 (자동 생성)

    @Column(unique = true, nullable = false)
    private String conitemNo; // 계약 항목 코드 (Primary Key)

    @ManyToOne
    @JoinColumn(name = "business_id", referencedColumnName = "businessId", nullable = false)
    private Company company; // 회사 (Company 엔티티와 연관)

    @ManyToOne
    @JoinColumn(name = "product_code", referencedColumnName = "productCode", nullable = false)
    private Item item; // 품목 (Item 엔티티와 연관)

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date contractDate; // 계약 날짜

    @Column(length = 255)
    private String contractFile; // 계약서 파일 경로

    @Column(length = 255)
    private String contractInfo; // 계약 정보

    @Column(nullable = false)
    private Integer contractPrice; // 계약 가격

    // 회사명, 품목명, 단가, 소요시간 추가
    @Transient
    private String companyName; // 회사명

    @Transient
    private String itemName; // 품목명

    @Transient
    private Integer unitCost; // 단가

    @Transient
    private Integer leadTime; // 소요시간

//    // 계약 생성 시 회사명, 품목명, 단가, 소요시간을 자동으로 설정하는 메소드
//    @PostLoad
//    public void setContractDetails() {
//        this.companyName = company.getComName(); // 회사명
//        this.itemName = item.getItemName(); // 품목명
//        this.unitCost = item.getUnitCost(); // 단가 (Item에 단가가 있을 경우)
//        this.leadTime = item.getLeadTime(); // 소요시간 (Item에 소요시간이 있을 경우)
//    }
}
