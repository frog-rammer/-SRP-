package com.procuone.mit_kdt.service;

import com.procuone.mit_kdt.dto.DeliveryOrderDTO;
import com.procuone.mit_kdt.entity.DeliveryOrder;
import com.procuone.mit_kdt.entity.PurchaseOrder;

public interface DeliveryOrderService {
    DeliveryOrderDTO registerDeliveryOrder(DeliveryOrderDTO deliveryOrderDTO); //납품 지시 테이블에 세이브


    DeliveryOrderDTO convertEntityToDTO(DeliveryOrder deliveryOrder); // 엔티티 -> DTO 변환 메서드
    DeliveryOrder convertDTOToEntity(DeliveryOrderDTO dto, PurchaseOrder purchaseOrder); // DTO -> 엔티티
}
