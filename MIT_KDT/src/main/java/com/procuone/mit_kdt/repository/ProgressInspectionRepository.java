package com.procuone.mit_kdt.repository;

import com.procuone.mit_kdt.entity.ProgressInspection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface ProgressInspectionRepository extends JpaRepository<ProgressInspection, String> {

    @Query("SELECT p FROM ProgressInspection p " +
            "WHERE (:productCodeQuery IS NULL OR p.productCode LIKE %:productCodeQuery%) " +
            "AND (:productNameQuery IS NULL OR p.productName LIKE %:productNameQuery%) " +
            "AND (:procurementPlanCodeQuery IS NULL OR p.purchaseOrder.procurementPlanCode LIKE %:procurementPlanCodeQuery%) " +
            "AND (:dateStart IS NULL OR p.inspectionDate >= :dateStart) " +
            "AND (:dateEnd IS NULL OR p.inspectionDate <= :dateEnd)")
    Page<ProgressInspection> searchByFilters(String productCodeQuery,
                                             String productNameQuery,
                                             String procurementPlanCodeQuery,
                                             LocalDate dateStart,
                                             LocalDate dateEnd,
                                             Pageable pageable);

    // 비즈니스 아이디로 진척검수 처리 과정 불러오기
    @Query("SELECT pi FROM ProgressInspection pi WHERE pi.businessId = :businessId")
    Page<ProgressInspection> findByBusinessId(@Param("businessId") String businessId, Pageable pageable);
}
