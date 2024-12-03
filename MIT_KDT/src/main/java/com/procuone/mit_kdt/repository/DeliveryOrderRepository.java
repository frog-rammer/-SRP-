package com.procuone.mit_kdt.repository;


import com.procuone.mit_kdt.entity.DeliveryOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryOrderRepository  extends JpaRepository<DeliveryOrder, String> {
}
