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

    // 다중 ProcurementPlanCode를 사용한 데이터 조회 (페이징 지원)
    Page<ProcurementPlan> findByProcurementPlanCodeIn(List<String> procurementPlanCodes, Pageable pageable);

    @Query("SELECT p FROM ProcurementPlan p WHERE " +
            "(:search IS NULL OR p.productName LIKE %:search% OR p.productCode LIKE %:search%) AND " +
            "(:productName IS NULL OR p.productName LIKE %:productName%) AND " +
            "(:startDate IS NULL OR p.planStartDate >= :startDate) AND " +
            "(:endDate IS NULL OR p.planEndDate <= :endDate)")
    Page<ProcurementPlan> searchProcurementPlans(
            @Param("search") String search,
            @Param("productName") String productName,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

}