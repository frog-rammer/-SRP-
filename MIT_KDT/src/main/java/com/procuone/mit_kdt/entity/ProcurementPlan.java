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
public class ProcurementPlan {

    @Id
    @GeneratedValue(generator = "custom-id")
    @GenericGenerator(
            name = "custom-id",
            strategy = "com.procuone.mit_kdt.customidGenerator.CustomIdGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "prefix", value = "PcPlan_"),
                    @org.hibernate.annotations.Parameter(name = "tableName", value = "procurement_plan"),
                    @org.hibernate.annotations.Parameter(name = "columnName", value = "procurement_plan_code")
            }
    )
    private String procurementPlanCode;
    private String productPlanCode;
    private String productName;
    private String productCode;
    private LocalDate planStartDate;
    private LocalDate planEndDate;
    private Long quantity;
    private Long procurementQuantity;
    private LocalDate procurementEndDate;
}