package com.procuone.mit_kdt.service;

import com.procuone.mit_kdt.dto.ProductionPlanDTO;
import com.procuone.mit_kdt.entity.ProductionPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface ProductionPlanService{
    void savePlan(ProductionPlanDTO productionPlanDTO); // 생산 계획 저장
    ProductionPlanDTO getPlanById(String planNum); // 특정 생산 계획 조회
    List<ProductionPlanDTO> getAllPlans(); // 모든 생산 계획 조회
    void deletePlan(String planNum); // 생산 계획 삭제
    public abstract Page<ProductionPlanDTO> getAllPlans(Pageable pageable);
    // 검색 메서드
    Page<ProductionPlanDTO> searchPlans(String searchType, String searchKeyword, String startDate, String endDate, Pageable pageable);

    // 추가적인 검색 메서드 (제품명 및 기간 기반)
    Page<ProductionPlanDTO> searchProductionPlans(String productName, LocalDate startDate, LocalDate endDate, Pageable pageable);

    void processExcelFile(MultipartFile file) throws Exception;
}
