package com.procuone.mit_kdt.repository;

import com.procuone.mit_kdt.entity.ProcurementPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ProcurementPlanRepository extends JpaRepository<ProcurementPlan, String> {

    // 제품명(productName) 또는 제품 코드(productCode)에 검색어가 포함된 데이터 조회
    Page<ProcurementPlan> findByProductNameContainingOrProductCodeContaining(String productName, String productCode, Pageable pageable);

    // 특정 상품 계획 코드(productPlanCode)로 데이터 조회
    List<ProcurementPlan> findByProductPlanCode(String productPlanCode);

    // 제품 계획 코드(productPlanCode)와 상품명(productName)으로 조건을 걸어 검색
    Page<ProcurementPlan> findByProductPlanCodeContainingOrProductNameContaining(String productPlanCode, String productName, Pageable pageable);

    ProcurementPlan findByProcurementPlanCode(String procurementPlanCode);

    // 예시 1: JPA 메서드 이름을 이용한 검색
    List<ProcurementPlan> findByProductNameContainingAndPlanStartDateGreaterThanEqualAndPlanEndDateLessThanEqualAndQuantityGreaterThan(
            String productName, LocalDate startDate, LocalDate endDate, Long quantity);

    // 예시 2: @Query를 사용한 검색
    @Query("SELECT p FROM ProcurementPlan p WHERE " +
            "(p.productName LIKE %:productName% OR :productName IS NULL) AND " +
            "(p.planStartDate >= :startDate OR :startDate IS NULL) AND " +
            "(p.planEndDate <= :endDate OR :endDate IS NULL) AND " +
            "(p.quantity >= :quantity OR :quantity IS NULL)")
    List<ProcurementPlan> searchProcurementPlans(@Param("productName") String productName,
                                                 @Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate,
                                                 @Param("quantity") Long quantity);
}