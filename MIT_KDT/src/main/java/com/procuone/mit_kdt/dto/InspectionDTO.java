package com.procuone.mit_kdt.dto;

import com.procuone.mit_kdt.entity.DeliveryOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InspectionDTO {
    private DeliveryOrder deliveryOrder;
    private String productName;
    private Long quantity;
    private Long defectiveQuantity;
}
