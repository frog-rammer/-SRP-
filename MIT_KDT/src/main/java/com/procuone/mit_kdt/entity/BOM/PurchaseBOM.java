package com.procuone.mit_kdt.entity.BOM;

import com.procuone.mit_kdt.entity.Company;
import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"product_code", "item_id", "business_id"})
        }
)
public class PurchaseBOM {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_code", nullable = false)
    private String productCode;

    @ManyToOne
    @JoinColumn(name = "item_id", referencedColumnName = "id", nullable = false)
    private Item item;

    @ManyToOne
    @JoinColumn(name = "business_id", referencedColumnName = "businessId", nullable = false)
    private Company company;

    private Integer quantity;

    private Integer unitCost;
}
