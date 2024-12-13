package com.procuone.mit_kdt.service;

import com.procuone.mit_kdt.dto.DeliveryOrderDTO;
import com.procuone.mit_kdt.entity.DeliveryOrder;
import com.procuone.mit_kdt.entity.PurchaseOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface DeliveryOrderService {
    DeliveryOrderDTO registerDeliveryOrder(DeliveryOrderDTO deliveryOrderDTO); //납품 지시 테이블에 세이브
    void updateDeliveryStatus(); // "완료"로 상태 변경
    DeliveryOrderDTO convertEntityToDTO(DeliveryOrder deliveryOrder); // 엔티티 -> DTO 변환 메서드
    DeliveryOrder convertDTOToEntity(DeliveryOrderDTO dto, PurchaseOrder purchaseOrder); // DTO -> 엔티티
    List<DeliveryOrder> findCompletedOrders();
    Page<DeliveryOrderDTO> findbystatus(String status, Pageable pageable);

}
