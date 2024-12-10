package com.procuone.mit_kdt.service;

import com.procuone.mit_kdt.dto.ProcumentPlanDTO;
import com.procuone.mit_kdt.dto.ShipmentDTO;
import com.procuone.mit_kdt.entity.Shipment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MaterialIssueService {

    Page<ShipmentDTO> getShipmentsByStatus(String status, Pageable pageable);
    Page<ShipmentDTO> getShipmentsByStatuses(List<String> statuses, Pageable pageable);
    void saveShipment(List<ShipmentDTO> shipments);
    Page<ShipmentDTO> getAllShipments(Pageable pageable);
    void updateCurrentQuantity(); // 현재 재고 반영
    void createAndSaveShipmentsFromProcurementPlan(ProcumentPlanDTO procurementPlanDTO); //shipment 자동 생성
    void updateToOngoing(List<String> shipmentIds); // 출고 진행
    void confirmReceipt(List<String> shipmentIds);  // 수령확인 처리 메서드
    //DTO<->엔티티 변환
    ShipmentDTO convertToDTO(Shipment shipment);
    Shipment convertToEntity(ShipmentDTO shipmentDTO);


}
