package com.procuone.mit_kdt.repository;

import com.procuone.mit_kdt.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, String> {

}
