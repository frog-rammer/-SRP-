package com.procuone.mit_kdt.service;

import com.procuone.mit_kdt.dto.ProcumentPlanDTO;
import com.procuone.mit_kdt.dto.PurchaseOrderDTO;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface PurchaseOrderService {
    public void registerPurchaseOrder(ProcumentPlanDTO procurementPlanDTO);

    Page<PurchaseOrderDTO> getOrdersByStatus(String status, int page, int size);

    void completeOrders(List<String> orderIds);

    // 새로운 검색 메서드 추가
    List<PurchaseOrderDTO> searchOrders(String status, String keyword, String type, LocalDate startDate, LocalDate endDate);
}
