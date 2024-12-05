package com.procuone.mit_kdt.repository;

import com.procuone.mit_kdt.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShipmentRepository extends JpaRepository<Shipment, String> {
}
