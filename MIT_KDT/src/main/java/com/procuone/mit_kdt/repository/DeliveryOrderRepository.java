package com.procuone.mit_kdt.repository;


import com.procuone.mit_kdt.entity.DeliveryOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DeliveryOrderRepository  extends JpaRepository<DeliveryOrder, String> {
    List<DeliveryOrder> findByStatus(String status);
    List<DeliveryOrder> findByStatusAndDeliveryDate(String status, LocalDate deliveryDate); // 날짜에 따른 상태변경
}
