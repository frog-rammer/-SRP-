package com.procuone.mit_kdt.repository;

import com.procuone.mit_kdt.dto.PurchaseOrderDTO;
import com.procuone.mit_kdt.entity.PurchaseOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, String> {
    Page<PurchaseOrder> findByStatus(String status, Pageable pageable);
    @Query("SELECT new com.procuone.mit_kdt.dto.PurchaseOrderDTO(o.purchaseOrderCode, o.procurementPlanCode, " +
            "o.productPlanCode, o.businessId, o.productCode, o.quantity, o.procurementEndDate, o.createdDate) " +
            "FROM PurchaseOrder o " +
            "WHERE o.status = :status " +
            "AND (:keyword IS NULL OR LOWER(COALESCE(CASE WHEN :type = 'businessId' THEN o.businessId ELSE o.productCode END, '')) " +
            "LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:startDate IS NULL OR o.procurementEndDate >= :startDate) " +
            "AND (:endDate IS NULL OR o.procurementEndDate <= :endDate)")
    List<PurchaseOrderDTO> searchOrders(
            @Param("status") String status,
            @Param("type") String type,
            @Param("keyword") String keyword,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);


    @Query("SELECT po FROM PurchaseOrder po WHERE po.status = :status")
    List<PurchaseOrder> findByStatus(@Param("status") String status);
    
    // 사업자 번호로 검색
    List<PurchaseOrder> findByBusinessId(String businessId);

    List<PurchaseOrder> findByStatusAndProcurementPlanCode(String status, String procurementPlanCode);
}