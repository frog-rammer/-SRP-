package com.procuone.mit_kdt.repository;

import com.procuone.mit_kdt.entity.Shipment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShipmentRepository extends JpaRepository<Shipment, String> {
    Page<Shipment> findByShipmentStatus(String status, Pageable pageable);
    Page<Shipment> findByShipmentStatusIn(List<String> statuses, Pageable pageable);
}
