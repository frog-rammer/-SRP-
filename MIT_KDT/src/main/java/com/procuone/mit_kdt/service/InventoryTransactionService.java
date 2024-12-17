package com.procuone.mit_kdt.service;

import com.procuone.mit_kdt.dto.InventoryTransactionDTO;
import com.procuone.mit_kdt.entity.Inventory;
import com.procuone.mit_kdt.entity.InventoryTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface InventoryTransactionService{
    InventoryTransactionDTO createTransaction(InventoryTransactionDTO dto);
    InventoryTransactionDTO getTransaction(String transactionCode);
    List<InventoryTransactionDTO> getAllTransactions();
    InventoryTransactionDTO updateTransaction(String transactionCode, InventoryTransactionDTO dto);
    void deleteTransaction(String transactionCode);

    // 특정 거래 유형(입고/출고/불량품)별 페이징된 거래 가져오기
    Page<InventoryTransactionDTO> getPagedTransactionsByStatus(String transactionType, Pageable pageable);

    // 특정 제품코드로 모든 거래 내역 가져오기
    List<InventoryTransactionDTO> getTransactionsByProductCode(String productCode);

    // 특정 기간 동안의 거래 내역 가져오기
    List<InventoryTransactionDTO> getTransactionsByDateRange(LocalDate startDate, LocalDate endDate);

    // 월별 입고량 통계
    Map<String, Long> getMonthlyInboundStatsByProductCode(String productCode);

    // 월별 출고량 통계
    Map<String, Long> getMonthlyOutboundStatsByProductCode(String productCode);

    // 주별 입고량 통계
    Map<String, Long> getWeeklyInboundStatsByProductCode(String productCode);

    // 주별 출고량 통계
    Map<String, Long> getWeeklyOutboundStatsByProductCode(String productCode);

    //제품 코드에 해당하는 월별 입고 금액 통계를 반환
    Map<String, Long> getMonthlyInboundPriceStatsByProductCode(String productCode);

    // 월별 거래 내역 집계 (입고량/출고량)
    Map<String, Long> getMonthlyTransactionStatsByProductCode(String productCode);

    // 주별 거래 내역 집계 (입고량/출고량)
    Map<String, Long> getWeeklyTransactionStatsByProductCode(String productCode);

    // 제품 코드로 특정 거래 금액 통계 가져오기
    Double getCostStatsByProductCode(String productCode);

    // 월별 금액 통계
    Map<String, Double> getMonthlyCostStatsByProductCode(String productCode);

    //  주별 금액 통계
    Map<String, Double> getWeeklyCostStatsByProductCode(String productCode);

    // 거래 유형별 총 금액 계산 (예: 총 입고 금액, 총 출고 금액)
    Map<String, Double> calculateTotalTransactionValueByType();

    // 거래 수량 통계 (입고, 출고, 불량품 별)
    Map<String, Long> calculateTransactionQuantities();
    // DTO -> Entity 변환 메서드 시그니처
    InventoryTransaction toEntity(InventoryTransactionDTO dto, Inventory inventory);

    // Entity -> DTO 변환 메서드 시그니처
    InventoryTransactionDTO toDTO(InventoryTransaction entity);
}
