package com.procuone.mit_kdt.dto;

import com.procuone.mit_kdt.entity.DeliveryOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InspectionDTO {
    private String inspectionCode; // 검수 코드 (선택적으로 사용)
    private DeliveryOrder deliveryOrder;
    private String deliveryCode;   // DeliveryOrder의 deliveryCode
    private String productName;    // 상품명
    private Long quantity;         // 납품 수량
    private Long defectiveQuantity;// 불량 수량
    private String inspectionStatus; // 검수 상태 ("검수 완료", "검수 중", "불량")
}
