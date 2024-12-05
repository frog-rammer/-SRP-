package com.procuone.mit_kdt.service;

import com.procuone.mit_kdt.dto.ProcumentPlanDTO;
import com.procuone.mit_kdt.dto.ShipmentDTO;
import com.procuone.mit_kdt.entity.Shipment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MaterialIssueService {

    void saveShipment(List<ShipmentDTO> shipments);
    Page<ShipmentDTO> getAllShipments(Pageable pageable);
    //DTO<->엔티티 변환
    ShipmentDTO convertToDTO(Shipment shipment);
    Shipment convertToEntity(ShipmentDTO shipmentDTO);

    void createAndSaveShipmentsFromProcurementPlan(ProcumentPlanDTO procurementPlanDTO);
}
