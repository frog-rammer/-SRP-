package com.procuone.mit_kdt.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ProductionPlan {

    @Id
    @GeneratedValue(generator = "custom-id")
    @GenericGenerator(
            name = "custom-id",
            strategy = "com.procuone.mit_kdt.customidGenerator.CustomIdGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "prefix", value = "PdpPlan_"),
                    @org.hibernate.annotations.Parameter(name = "tableName", value = "production_plan"),
                    @org.hibernate.annotations.Parameter(name = "columnName", value = "product_plan_code")
            }
    )
    private String productPlanCode;

    private String productName;
    private String productCode;
    private LocalDate planStartDate;
    private LocalDate planEndDate;
    private Integer quantity;
}
