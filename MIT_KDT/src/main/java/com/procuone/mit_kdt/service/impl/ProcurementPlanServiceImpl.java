package com.procuone.mit_kdt.service.impl;

import com.procuone.mit_kdt.dto.ProcumentPlanDTO;
import com.procuone.mit_kdt.dto.PurchaseOrderDTO;
import com.procuone.mit_kdt.dto.ShipmentDTO;
import com.procuone.mit_kdt.entity.ProcurementPlan;
import com.procuone.mit_kdt.entity.ProductionPlan;
import com.procuone.mit_kdt.repository.ProcurementPlanRepository;
import com.procuone.mit_kdt.repository.ProductionPlanRepository;
import com.procuone.mit_kdt.service.MaterialIssueService;
import com.procuone.mit_kdt.service.ProcurementPlanService;
import com.procuone.mit_kdt.service.PurchaseOrderService;
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
public class ProcurementPlanServiceImpl implements ProcurementPlanService {

    @Autowired
    private ProcurementPlanRepository repository;

    @Autowired
    private ProductionPlanRepository productionPlanRepository;

    @Autowired
    private ProcurementPlanRepository procurementPlanRepository;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private MaterialIssueService materialIssueService;

    @Override
    public ProcumentPlanDTO registerProcurementPlan(ProcumentPlanDTO dto) {
        // 1. 생산 계획 조회
        ProductionPlan productionPlan = productionPlanRepository.findById(dto.getProductPlanCode())
                .orElseThrow(() -> new IllegalArgumentException("해당 생산 계획이 존재하지 않습니다: " + dto.getProductPlanCode()));

        long productionQuantity = productionPlan.getQuantity(); // 생산 예정 수량

        // 2. 기존 조달 계획의 총 조달 수량 계산
        long existingProcurementQuantity = repository.findByProductPlanCode(dto.getProductPlanCode())
                .stream()
                .mapToLong(ProcurementPlan::getProcurementQuantity) // 조달 수량 합산
                .sum();

        // 3. 새로운 조달 수량 추가
        long newProcurementQuantity = dto.getProcurementQuantity();
        long totalProcurementQuantity = existingProcurementQuantity + newProcurementQuantity;

        // 4. 생산 예정 수량 초과 여부 검증
        if (totalProcurementQuantity > productionQuantity) {
            throw new IllegalStateException("조달 예정 수량이 생산 예정 수량을 초과할 수 없습니다. " +
                    "생산 예정 수량: " + productionQuantity +
                    ", 현재까지 조달 수량: " + existingProcurementQuantity +
                    ", 추가하려는 조달 수량: " + newProcurementQuantity);
        }

        // 5. 조달 계획 저장
        ProcurementPlan entity = dtoToEntity(dto);
        ProcurementPlan savedEntity = repository.save(entity);

        return entityToDto(savedEntity);
    }

    @Override
    public List<ProcumentPlanDTO> getProcurementPlanByProductPlanCode(String productPlanCode) {
        List<ProcurementPlan> procurementPlans = procurementPlanRepository.findByProductPlanCode(productPlanCode);
        return procurementPlans.stream()
                .map(this::entityToDto) // 엔티티를 DTO로 변환
                .collect(Collectors.toList());
    }
    @Override
    public long getRequiredProcurementQuantity(String productionPlanId) {
        // 1. 생산 계획 조회
        ProductionPlan productionPlan = productionPlanRepository.findById(productionPlanId)
                .orElseThrow(() -> new IllegalArgumentException("해당 생산 계획이 존재하지 않습니다: " + productionPlanId));

        long productionQuantity = productionPlan.getQuantity(); // 생산 예정 수량

        // 2. 기존 조달 계획의 총 조달 수량 계산
        long existingProcurementQuantity = repository.findByProductPlanCode(productionPlanId)
                .stream()
                .mapToLong(ProcurementPlan::getProcurementQuantity) // 조달 수량 합산
                .sum();

        return productionQuantity - existingProcurementQuantity;
    }

    @Override
    public ProcurementPlan dtoToEntity(ProcumentPlanDTO dto) {
        return ProcurementPlan.builder()
                .procurementPlanCode(dto.getProcurementPlanCode())
                .productPlanCode(dto.getProductPlanCode())
                .productName(dto.getProductName())
                .productCode(dto.getProductCode())
                .planStartDate(dto.getPlanStartDate())
                .planEndDate(dto.getPlanEndDate())
                .quantity(dto.getQuantity())
                .procurementQuantity(dto.getProcurementQuantity())
                .procurementEndDate(dto.getProcurementEndDate())
                .build();
    }

    @Override
    public ProcumentPlanDTO entityToDto(ProcurementPlan entity) {
        return ProcumentPlanDTO.builder()
                .procurementPlanCode(entity.getProcurementPlanCode())
                .productPlanCode(entity.getProductPlanCode())
                .productName(entity.getProductName())
                .productCode(entity.getProductCode())
                .planStartDate(entity.getPlanStartDate())
                .planEndDate(entity.getPlanEndDate())
                .quantity(entity.getQuantity())
                .procurementQuantity(entity.getProcurementQuantity())
                .procurementEndDate(entity.getProcurementEndDate())
                .build();
    }

    @Override
    public String search(ProcumentPlanDTO procurementPlanDTO) {
        // Optional을 사용하여 값을 안전하게 가져오기
        Optional<ProcurementPlan> optional = repository.findById(procurementPlanDTO.getProductPlanCode());
        // 값을 찾은 경우
        if (optional.isPresent()) {
            ProcurementPlan procurementPlan = optional.get();
            // 수량이 0보다 크면 success 반환
            if (procurementPlan.getQuantity() > 0) {
                return "success";  // 성공 처리
            } else {
                return "failure";  // 수량이 없으면 실패 처리
            }
        }

        // 값을 찾지 못한 경우
        return "failure";  // 계획을 찾지 못한 경우 실패 처리
    }

    // 모든 조달 계획을 반환하는 메서드 추가
    @Override
    public List<ProcurementPlan> getAllProcurementPlans() {
        return repository.findAll();  // 모든 조달 계획을 조회
    }

    @Override
    public Page<ProcurementPlan> searchProcurementPlans(String search, String productName, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return procurementPlanRepository.searchProcurementPlans(search, productName, startDate, endDate, pageable);
    }

    @Override
    public Page<ProcurementPlan> findAll(Pageable pageable) {
        return procurementPlanRepository.findAll(pageable);
    }

    // 조달계획 조회
    @Override
    public ProcurementPlan getProcurementPlanByCode(String procurementPlanCode) {
        return procurementPlanRepository.findByProcurementPlanCode(procurementPlanCode);
    }

    // 조달계획 업데이트
    @Override
    public void updateProcurementPlan(ProcurementPlan procurementPlan) {
        procurementPlanRepository.save(procurementPlan); // JPA를 사용해 데이터 저장
    }

    @Override
    @Transactional
    public Long deleteProcurementPlanByCode(String procurementPlanCode) {
        // 조달계획 조회
        Optional<ProcurementPlan> procurementPlan = procurementPlanRepository.findById(procurementPlanCode);

        if (procurementPlan.isPresent()) {
            // 발주서 상태 조회
            List<PurchaseOrderDTO> orders = purchaseOrderService.getOrdersByStatusAndProcurementPlanCode("자동생성", procurementPlanCode);
            List<PurchaseOrderDTO> completedOrders = purchaseOrderService.getOrdersByStatusAndProcurementPlanCode("발주완료", procurementPlanCode);
            List<ShipmentDTO> ships =materialIssueService.getShipmentsByProcurementPlanCode(procurementPlanCode);

            // 이미 발주완료된 조달계획은 삭제 불가능
            if (!completedOrders.isEmpty()) {
                return 3L; // 발주완료된 경우
            }
            // 발주가 자동생성 상태일 경우 삭제 가능
            if (!orders.isEmpty()) {
                for (PurchaseOrderDTO order : orders) {
                    purchaseOrderService.delete(order);
                }
                for(ShipmentDTO ship : ships) {
                    materialIssueService.deleteShipment(ship);
                }
            }
            // 조달계획 삭제
            procurementPlanRepository.delete(procurementPlan.get());
            return 1L; // 성공
        }

        return 2L; // 조달계획 없음
    }
}

