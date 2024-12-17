package com.procuone.mit_kdt.repository;

import com.procuone.mit_kdt.entity.InventoryTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, String> {
    // 특정 productCode에 해당하는 단일 거래 조회
    Optional<InventoryTransaction> findFirstByProductCode(String productCode);

    // 특정 productCode에 해당하는 모든 거래 조회
    List<InventoryTransaction> findAllByProductCode(String productCode);

    // 특정 거래 유형(입고/출고)으로 거래 페이징 조회
    Page<InventoryTransaction> findByTransactionType(String transactionType, Pageable pageable);

    // 특정 기간 동안의 거래 내역 조회
    List<InventoryTransaction> findByTransactionDateBetween(LocalDate startDate, LocalDate endDate);

    // 특정 거래 유형과 제품 코드로 조회
    List<InventoryTransaction> findByTransactionTypeAndProductCode(String transactionType, String productCode);

}
