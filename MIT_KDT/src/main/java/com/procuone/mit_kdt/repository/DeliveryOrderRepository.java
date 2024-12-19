package com.procuone.mit_kdt.repository;


import com.procuone.mit_kdt.entity.DeliveryOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface DeliveryOrderRepository  extends JpaRepository<DeliveryOrder, String> {
    List<DeliveryOrder> findByStatus(String status);
    List<DeliveryOrder> findByStatusAndDeliveryDate(String status, LocalDate deliveryDate);// 날짜에 따른 상태변경
    // 동적 검색: 발주 코드와 품목 코드로 검색, 둘 다 null이면 전체 조회
    @Query("SELECT d FROM DeliveryOrder d WHERE " +
            "(:purchaseOrderCode IS NULL OR d.purchaseOrder.purchaseOrderCode LIKE %:purchaseOrderCode%) AND " +
            "(:productCode IS NULL OR d.productCode LIKE %:productCode%)")
    Page<DeliveryOrder> searchByPurchaseOrderCodeAndProductCode(
            @Param("purchaseOrderCode") String purchaseOrderCode,
            @Param("productCode") String productCode,
            Pageable pageable
    );
}
