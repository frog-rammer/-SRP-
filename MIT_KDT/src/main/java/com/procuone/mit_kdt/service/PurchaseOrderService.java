package com.procuone.mit_kdt.service;

import com.procuone.mit_kdt.dto.ProcumentPlanDTO;
import com.procuone.mit_kdt.dto.PurchaseOrderDTO;
import com.procuone.mit_kdt.entity.PurchaseOrder;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PurchaseOrderService {
    public void registerPurchaseOrder(ProcumentPlanDTO procurementPlanDTO);
    Page<PurchaseOrderDTO> getOrdersByStatus(String status, int page, int size);

    void completeOrders(List<String> orderIds);
}
