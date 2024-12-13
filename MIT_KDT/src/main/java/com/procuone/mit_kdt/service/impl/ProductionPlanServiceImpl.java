package com.procuone.mit_kdt.service.impl;

import com.procuone.mit_kdt.dto.ProductionPlanDTO;
import com.procuone.mit_kdt.entity.ProductionPlan;
import com.procuone.mit_kdt.repository.ProductionPlanRepository;
import com.procuone.mit_kdt.service.ProductionPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductionPlanServiceImpl implements ProductionPlanService {

    @Autowired
    private ProductionPlanRepository productionPlanRepository;

    @Override
    public void savePlan(ProductionPlanDTO productionPlanDTO) {
        // 새로운 ID 생성
        String newId = generateNewId();
        productionPlanDTO.setProductPlanCode(newId);

        productionPlanRepository.save(dtoToEntity(productionPlanDTO));
    }
    @Override
    public ProductionPlanDTO getPlanById(String planNum) {
        return productionPlanRepository.findById(planNum)
                .map(this::entityToDto)
                .orElse(null);
    }

    @Override
    public List<ProductionPlanDTO> getAllPlans() {
        return productionPlanRepository.findAll().stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deletePlan(String planNum) {
        productionPlanRepository.deleteById(planNum);
    }

    @Override
    public Page<ProductionPlanDTO> getAllPlans(Pageable pageable) {
        Page<ProductionPlan> planPage = productionPlanRepository.findAll(pageable);
        return planPage.map(this::entityToDto);
    }

    // DTO를 엔티티로 변환
    private ProductionPlan dtoToEntity(ProductionPlanDTO dto) {
        return ProductionPlan.builder()
                .productPlanCode(dto.getProductPlanCode())
                .productName(dto.getProductName())
                .productCode(dto.getProductCode())
                .planStartDate(dto.getPlanStartDate())
                .planEndDate(dto.getPlanEndDate())
                .quantity(dto.getQuantity())
                .build();
    }

    // 엔티티를 DTO로 변환
    private ProductionPlanDTO entityToDto(ProductionPlan entity) {
        return ProductionPlanDTO.builder()
                .productPlanCode(entity.getProductPlanCode())
                .productName(entity.getProductName())
                .productCode(entity.getProductCode())
                .planStartDate(entity.getPlanStartDate())
                .planEndDate(entity.getPlanEndDate())
                .quantity(entity.getQuantity())
                .build();
    }

    private String generateNewId() {
        String prefix = "PdPlan_";
        int nextId = productionPlanRepository.findMaxId()
                .map(id -> Integer.parseInt(id.substring(8)) + 1)
                .orElse(1);
        return prefix + String.format("%03d", nextId);
    }

    public Page<ProductionPlanDTO> searchPlans(String searchType, String searchKeyword, String startDate, String endDate, Pageable pageable) {
        LocalDate start = (startDate != null && !startDate.isEmpty()) ? LocalDate.parse(startDate) : null;
        LocalDate end = (endDate != null && !endDate.isEmpty()) ? LocalDate.parse(endDate) : null;

        Page<ProductionPlan> productionPlanPage; // 변수 선언

        // 검색 조건에 따라 다른 쿼리 실행
        if (start != null && end != null) {
            productionPlanPage = productionPlanRepository.findByDateRangeAndKeyword(searchType, searchKeyword, start, end, pageable);
        } else if (searchKeyword != null && !searchKeyword.isEmpty()) {
            productionPlanPage = productionPlanRepository.findByKeyword(searchType, searchKeyword, pageable);
        } else {
            productionPlanPage = productionPlanRepository.findAll(pageable); // 검색 조건이 없으면 전체 조회
        }

        // ProductionPlan 엔티티를 DTO로 변환하여 반환
        return productionPlanPage.map(this::entityToDto);
    }

    @Override
    public Page<ProductionPlanDTO> searchProductionPlans(String productName, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        Page<ProductionPlan> productionPlans = productionPlanRepository.findByProductNameAndPlanStartDateBetween(
                productName, startDate, endDate, pageable);

        return productionPlans.map(this::entityToDto);
    }

    // 생산계획 수정 메서드
    @Override
    @Transactional  // 트랜잭션 처리가 필요할 수 있음
    public void updatePlan(ProductionPlanDTO productionPlanDTO) {
        // 1. productPlanCode를 기준으로 DB에서 기존 데이터 조회
        ProductionPlan existingPlan = productionPlanRepository.findByProductPlanCode(productionPlanDTO.getProductPlanCode());

        // 2. 데이터가 존재하지 않으면 예외를 던지거나 처리
        if (existingPlan == null) {
            throw new IllegalArgumentException("해당 생산계획을 찾을 수 없습니다.");
        }

        // 3. 기존 데이터를 수정
        existingPlan.setPlanStartDate(productionPlanDTO.getPlanStartDate());
        existingPlan.setPlanEndDate(productionPlanDTO.getPlanEndDate());
        existingPlan.setQuantity(productionPlanDTO.getQuantity());

        // 4. 수정된 데이터를 저장 (JPA에서는 save()가 update 역할을 함)
        productionPlanRepository.save(existingPlan);
    }
}


