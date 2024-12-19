package com.procuone.mit_kdt.service;

import com.procuone.mit_kdt.dto.DeliveryOrderDTO;
import com.procuone.mit_kdt.entity.DeliveryOrder;
import com.procuone.mit_kdt.entity.PurchaseOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Map;

public interface DeliveryOrderService {
    DeliveryOrderDTO registerDeliveryOrder(DeliveryOrderDTO deliveryOrderDTO); //납품 지시 테이블에 세이브
    DeliveryOrderDTO getDeliveryOrder(DeliveryOrderDTO deliveryOrderDTO);
    void updateDeliveryStatus(); // "완료"로 상태 변경
    DeliveryOrderDTO convertEntityToDTO(DeliveryOrder deliveryOrder); // 엔티티 -> DTO 변환 메서드
    DeliveryOrder convertDTOToEntity(DeliveryOrderDTO dto, PurchaseOrder purchaseOrder); // DTO -> 엔티티
    List<DeliveryOrder> findCompletedOrders();
    Page<DeliveryOrderDTO> findbystatus(String status, Pageable pageable);
    Page<DeliveryOrderDTO> searchDeliveryOrders(String purchaseOrderCode, String productCode, Pageable pageable); // 검색 및 페이징
    Map<String, Long> calculateTotalQuantities(Map<String, List<DeliveryOrderDTO>> groupedOrders); // 발주 코드별 합계 계산
    Map<String, String> calculateAchievementStatus(Map<String, Long> purchaseOrderQuantities); // 발주 코드별 달성 여부 계산
}
