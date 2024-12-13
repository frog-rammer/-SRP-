package com.procuone.mit_kdt.service;

import com.procuone.mit_kdt.dto.InventoryTransactionDTO;
import com.procuone.mit_kdt.entity.Inventory;
import com.procuone.mit_kdt.entity.InventoryTransaction;

import java.util.List;

public interface InventoryTransactionService{
    InventoryTransactionDTO createTransaction(InventoryTransactionDTO dto);
    InventoryTransactionDTO getTransaction(String transactionCode);
    List<InventoryTransactionDTO> getAllTransactions();
    InventoryTransactionDTO updateTransaction(String transactionCode, InventoryTransactionDTO dto);
    void deleteTransaction(String transactionCode);


    // 제품코드로 거래를 업데이트하는 메서드
    InventoryTransactionDTO updateTransactionByProductCode(String productCode, InventoryTransactionDTO dto);
    // DTO -> Entity 변환 메서드 시그니처
    InventoryTransaction toEntity(InventoryTransactionDTO dto, Inventory inventory);

    // Entity -> DTO 변환 메서드 시그니처
    InventoryTransactionDTO toDTO(InventoryTransaction entity);
}
