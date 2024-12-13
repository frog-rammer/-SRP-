package com.procuone.mit_kdt.repository;

import com.procuone.mit_kdt.entity.InventoryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, String> {
    // JpaRepository에서 기본적인 CRUD 메서드는 제공됩니다.
    // 필요시 커스텀 쿼리 메서드를 추가할 수 있습니다.

    // productCode가 unique하므로 productCode로 거래 조회
    Optional<InventoryTransaction> findByProductCode(String productCode);
}
