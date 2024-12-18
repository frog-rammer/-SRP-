package com.procuone.mit_kdt.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrder {
    @Id
    @GeneratedValue(generator = "custom-id")
    @GenericGenerator(
            name = "custom-id",
            strategy = "com.procuone.mit_kdt.customidGenerator.CustomIdGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "prefix", value = "POrder_"),
                    @org.hibernate.annotations.Parameter(name = "tableName", value = "purchase_order"),
                    @org.hibernate.annotations.Parameter(name = "columnName", value = "purchase_order_code")
            }
    )
    private String purchaseOrderCode;
    private String productPlanCode;
    private String procurementPlanCode;
    private String productCode;
    private String businessId;
    private LocalDate procurementEndDate;
    private Long quantity;
    private Long Price;
    private String status;
    private String createdBy;
    private LocalDate createdDate;
    private LocalDate ExpectedArrivalDate;
    private String updatedBy;
    private LocalDate updatedDate;
}
