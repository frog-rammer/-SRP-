package com.procuone.mit_kdt.service.impl;

import com.procuone.mit_kdt.dto.ProductionPlanDTO;
import com.procuone.mit_kdt.entity.ProductionPlan;
import com.procuone.mit_kdt.repository.ProductionPlanRepository;
import com.procuone.mit_kdt.service.ProductionPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductionPlanServiceImpl implements ProductionPlanService {

    @Autowired
    private ProductionPlanRepository productionPlanRepository;

    @Override
    public void savePlan(ProductionPlanDTO productionPlanDTO) {
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

    // DTO를 엔티티로 변환
    private ProductionPlan dtoToEntity(ProductionPlanDTO dto) {
        return ProductionPlan.builder()
                .planNo(dto.getPlanNo())
                .productName(dto.getProductName())
                .productCode(dto.getProductCode())
                .planStartDate(dto.getPlanStartDate())
                .planEndDate(dto.getPlanEndDate())
                .quantity(dto.getQuantity())
                .partCode(dto.getPartCode())
                .companyName(dto.getCompanyName())
                .companyNumber(dto.getCompanyNumber())
                .build();
    }

    // 엔티티를 DTO로 변환
    private ProductionPlanDTO entityToDto(ProductionPlan entity) {
        return ProductionPlanDTO.builder()
                .planNo(entity.getPlanNo())
                .productName(entity.getProductName())
                .productCode(entity.getProductCode())
                .planStartDate(entity.getPlanStartDate())
                .planEndDate(entity.getPlanEndDate())
                .quantity(entity.getQuantity())
                .partCode(entity.getPartCode())
                .companyName(entity.getCompanyName())
                .companyNumber(entity.getCompanyNumber())
                .build();
    }
}
