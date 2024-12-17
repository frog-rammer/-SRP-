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
            "WHERE (:productCodeQuery IS NULL OR :productCodeQuery = '' OR p.productCode LIKE %:productCodeQuery%) " +
            "AND (:productNameQuery IS NULL OR :productNameQuery = '' OR p.productName LIKE %:productNameQuery%) " +
            "AND (:procurementPlanCodeQuery IS NULL OR :procurementPlanCodeQuery = '' OR p.purchaseOrder.procurementPlanCode LIKE %:procurementPlanCodeQuery%) " +
            "AND (:dateStart IS NULL OR p.inspectionDate >= :dateStart) " +
            "AND (:dateEnd IS NULL OR p.inspectionDate <= :dateEnd) " +
            "AND (:inspectionStatus IS NULL OR :inspectionStatus = '' OR p.inspectionStatus = :inspectionStatus)")
    Page<ProgressInspection> searchByFilters(@Param("productCodeQuery") String productCodeQuery,
                                             @Param("productNameQuery") String productNameQuery,
                                             @Param("procurementPlanCodeQuery") String procurementPlanCodeQuery,
                                             @Param("dateStart") LocalDate dateStart,
                                             @Param("dateEnd") LocalDate dateEnd,
                                             @Param("inspectionStatus") String inspectionStatus,
                                             Pageable pageable);

    // 비즈니스 아이디로 진척검수 처리 과정 불러오기
    @Query("SELECT pi FROM ProgressInspection pi WHERE pi.businessId = :businessId")
    Page<ProgressInspection> findByBusinessId(@Param("businessId") String businessId, Pageable pageable);


    // inspectedQuantity가 0 이상인 데이터 검색
    @Query("SELECT pi FROM ProgressInspection pi WHERE pi.inspectedQuantity > 0")
    Page<ProgressInspection> findAllWithInspectedQuantityGreaterThanZero(Pageable pageable);

    @Query("SELECT pi FROM ProgressInspection pi " +
            "WHERE pi.businessId = :businessId " +
            "AND (pi.inspectionStatus = :status1 OR pi.inspectionStatus = :status2)")
    Page<ProgressInspection> findByStatusIn(@Param("businessId") String businessId,
                                            @Param("status1") String status1,
                                            @Param("status2") String status2,
                                            Pageable pageable);

    @Query("SELECT pi FROM ProgressInspection pi " +
            "WHERE pi.businessId = :businessId " +
            "AND pi.inspectionStatus = :status1")
    Page<ProgressInspection> findByStatus(@Param("businessId") String businessId,
                                          @Param("status1") String status1,
                                          Pageable pageable);

}
