package com.procuone.mit_kdt.service;

import com.procuone.mit_kdt.dto.ProcumentPlanDTO;
import com.procuone.mit_kdt.dto.PurchaseOrderDTO;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface PurchaseOrderService {
    void registerPurchaseOrder(ProcumentPlanDTO procurementPlanDTO);

    Page<PurchaseOrderDTO> getOrdersByStatus(String status, int page, int size);
    List<PurchaseOrderDTO> getCompletedOrders(String status); // 스태이터스 리스트형태로 뽑기
    // 발주 완료된 것 가져오기
    void completeOrders(List<String> orderIds);
    // 새로운 검색 메서드 추가
    List<PurchaseOrderDTO> searchOrders(String status, String keyword, String type, LocalDate startDate, LocalDate endDate);
    //발주 코드로가져오기..
    PurchaseOrderDTO getpurchaseOrderById(String purchaseOrderCode);
    //사업자 번호로 검색
    List<PurchaseOrderDTO> getCompletedOrdersBybusinessId(String businessId);
}
