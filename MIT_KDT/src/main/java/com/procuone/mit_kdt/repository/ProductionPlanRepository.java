package com.procuone.mit_kdt.repository;

import com.procuone.mit_kdt.entity.ProductionPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface ProductionPlanRepository extends JpaRepository<ProductionPlan, String> {
    @Query("SELECT MAX(p.id) FROM ProductionPlan p")
    Optional<String> findMaxId();
    // 키워드 검색
    @Query("SELECT p FROM ProductionPlan p WHERE " +
            "(:searchType = 'productPlanCode' AND p.productPlanCode LIKE %:searchKeyword%) OR " +
            "(:searchType = 'productName' AND p.productName LIKE %:searchKeyword%) OR " +
            "(:searchType = 'productCode' AND p.productCode LIKE %:searchKeyword%)")
    Page<ProductionPlan> findByKeyword(@Param("searchType") String searchType,
                                       @Param("searchKeyword") String searchKeyword,
                                       Pageable pageable);

    // 날짜와 키워드 검색
    @Query("SELECT p FROM ProductionPlan p WHERE " +
            "((:searchType = 'productPlanCode' AND p.productPlanCode LIKE %:searchKeyword%) OR " +
            "(:searchType = 'productName' AND p.productName LIKE %:searchKeyword%) OR " +
            "(:searchType = 'productCode' AND p.productCode LIKE %:searchKeyword%)) AND " +
            "p.planStartDate >= :startDate AND p.planEndDate <= :endDate")
    Page<ProductionPlan> findByDateRangeAndKeyword(@Param("searchType") String searchType,
                                                   @Param("searchKeyword") String searchKeyword,
                                                   @Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate,
                                                   Pageable pageable);
}
