package com.procuone.mit_kdt.service.impl;

import com.procuone.mit_kdt.dto.InventoryTransactionDTO;
import com.procuone.mit_kdt.entity.Inventory;
import com.procuone.mit_kdt.entity.InventoryTransaction;
import com.procuone.mit_kdt.repository.InventoryRepository;
import com.procuone.mit_kdt.repository.InventoryTransactionRepository;
import com.procuone.mit_kdt.service.InventoryTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InventoryTransactionServiceImpl implements InventoryTransactionService {

    @Autowired
    private InventoryTransactionRepository inventoryTransactionRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    /**
     * 새로운 재고 거래를 생성하는 메서드.
     * DTO를 받아 엔티티로 변환한 뒤, DB에 저장하고 저장된 결과를 다시 DTO로 변환하여 반환한다.
     */
    @Override
    public InventoryTransactionDTO createTransaction(InventoryTransactionDTO dto) {
        Inventory inventory = inventoryRepository.findById(dto.getInventoryCode())
                .orElseThrow(() -> new IllegalArgumentException("Inventory not found"));

        InventoryTransaction entity = toEntity(dto, inventory);
        InventoryTransaction saved = inventoryTransactionRepository.save(entity);
        return toDTO(saved);
    }

    /**
     * 거래 코드(transactionCode)에 해당하는 재고 거래 정보를 조회하는 메서드.
     * 해당 거래 엔티티를 찾아 DTO로 변환하여 반환한다.
     */
    @Override
    public InventoryTransactionDTO getTransaction(String transactionCode) {
        InventoryTransaction entity = inventoryTransactionRepository.findById(transactionCode)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
        return toDTO(entity);
    }

    /**
     * 모든 재고 거래 정보를 조회하는 메서드.
     * DB에서 모든 엔티티를 조회한 뒤 DTO 리스트로 변환하여 반환한다.
     */
    @Override
    public List<InventoryTransactionDTO> getAllTransactions() {
        return inventoryTransactionRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    /**
     * 특정 거래 코드(transactionCode)에 해당하는 재고 거래 정보를 업데이트하는 메서드.
     * DTO를 받아 해당 거래 엔티티의 내용을 업데이트 후, DB에 저장하고 갱신된 데이터를 DTO로 반환한다.
     */
    @Override
    public InventoryTransactionDTO updateTransaction(String transactionCode, InventoryTransactionDTO dto) {
        InventoryTransaction entity = inventoryTransactionRepository.findById(transactionCode)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

        Inventory inventory = null;
        if (dto.getInventoryCode() != null) {
            inventory = inventoryRepository.findById(dto.getInventoryCode())
                    .orElseThrow(() -> new IllegalArgumentException("Inventory not found"));
        }

        InventoryTransaction updatedEntity = InventoryTransaction.builder()
                .transactionCode(entity.getTransactionCode())
                .inventory(inventory == null ? entity.getInventory() : inventory)
                .productCode(dto.getProductCode() != null ? dto.getProductCode() : entity.getProductCode())
                .transactionType(dto.getTransactionType() != null ? dto.getTransactionType() : entity.getTransactionType())
                .quantity(dto.getQuantity() != null ? dto.getQuantity() : entity.getQuantity())
                .transactionDate(dto.getTransactionDate() != null ? dto.getTransactionDate() : entity.getTransactionDate())
                .transactionValue(dto.getTransactionValue() != null ? dto.getTransactionValue() : entity.getTransactionValue())
                .relatedOrderCode(dto.getRelatedOrderCode() != null ? dto.getRelatedOrderCode() : entity.getRelatedOrderCode())
                .build();

        InventoryTransaction saved = inventoryTransactionRepository.save(updatedEntity);
        return toDTO(saved);
    }

    /**
     * 특정 거래 코드(transactionCode)에 해당하는 재고 거래 정보를 삭제하는 메서드.
     */
    @Override
    public void deleteTransaction(String transactionCode) {
        if (!inventoryTransactionRepository.existsById(transactionCode)) {
            throw new IllegalArgumentException("Transaction not found");
        }
        inventoryTransactionRepository.deleteById(transactionCode);
    }

    /**
     * 특정 거래 유형(입고/출고)별 페이징된 거래 반환
     */
    @Override
    public Page<InventoryTransactionDTO> getPagedTransactionsByStatus(String transactionType, Pageable pageable) {
        return inventoryTransactionRepository.findByTransactionType(transactionType, pageable)
                .map(this::toDTO);
    }

    /**
     * 특정 제품 코드로 거래 내역 조회
     */
    @Override
    public List<InventoryTransactionDTO> getTransactionsByProductCode(String productCode) {
        return inventoryTransactionRepository.findAllByProductCode(productCode).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 특정 기간 동안의 거래 내역 조회
     */
    @Override
    public List<InventoryTransactionDTO> getTransactionsByDateRange(LocalDate startDate, LocalDate endDate) {
        return inventoryTransactionRepository.findByTransactionDateBetween(startDate, endDate).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    @Override
    public Map<String, Long> getMonthlyInboundStatsByProductCode(String productCode) {
        List<InventoryTransaction> transactions = inventoryTransactionRepository
                .findByTransactionTypeAndProductCode("입고", productCode);
        return aggregateByMonth(transactions);
    }

    @Override
    public Map<String, Long> getMonthlyOutboundStatsByProductCode(String productCode) {
        List<InventoryTransaction> transactions = inventoryTransactionRepository
                .findByTransactionTypeAndProductCode("출고", productCode);
        return aggregateByMonth(transactions);
    }

    @Override
    public Map<String, Long> getWeeklyInboundStatsByProductCode(String productCode) {
        List<InventoryTransaction> transactions = inventoryTransactionRepository
                .findByTransactionTypeAndProductCode("입고", productCode);
        return aggregateByWeek(transactions);
    }

    @Override
    public Map<String, Long> getWeeklyOutboundStatsByProductCode(String productCode) {
        List<InventoryTransaction> transactions = inventoryTransactionRepository
                .findByTransactionTypeAndProductCode("출고", productCode);
        return aggregateByWeek(transactions);
    }

    @Override
    public Map<String, Long> getMonthlyInboundPriceStatsByProductCode(String productCode) {
        // '입고' 거래 유형의 데이터만 가져옵니다.
        List<InventoryTransaction> transactions = inventoryTransactionRepository
                .findByTransactionTypeAndProductCode("입고", productCode);

        // 월별 입고 금액을 집계합니다.
        Map<String, Long> monthlyPriceStats = new HashMap<>();
        for (InventoryTransaction transaction : transactions) {
            String month = transaction.getTransactionDate().getMonth().toString(); // 영문 월 이름 예: JANUARY
            Double transactionValue = Optional.ofNullable(transaction.getTransactionValue()).orElse(0.0);
            monthlyPriceStats.put(month, monthlyPriceStats.getOrDefault(month, 0L) + transactionValue.longValue());
        }

        return monthlyPriceStats;
    }


    private Map<String, Long> aggregateByMonth(List<InventoryTransaction> transactions) {
        Map<String, Long> monthlyStats = new HashMap<>();
        for (InventoryTransaction transaction : transactions) {
            String month = transaction.getTransactionDate().getMonth().toString(); // 월 이름 예: JANUARY
            monthlyStats.put(month, monthlyStats.getOrDefault(month, 0L) + transaction.getQuantity());
        }
        return monthlyStats;
    }
    private Map<String, Long> aggregateByWeek(List<InventoryTransaction> transactions) {
        Map<String, Long> weeklyStats = new HashMap<>();
        for (InventoryTransaction transaction : transactions) {
            String week = "Week " + transaction.getTransactionDate().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR); // ISO 주차
            weeklyStats.put(week, weeklyStats.getOrDefault(week, 0L) + transaction.getQuantity());
        }
        return weeklyStats;
    }


    /**
     * 특정 거래 유형과 제품 코드로 거래 내역 조회
     */
    public List<InventoryTransactionDTO> getTransactionsByTypeAndProductCode(String transactionType, String productCode) {
        return inventoryTransactionRepository.findByTransactionTypeAndProductCode(transactionType, productCode).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 제품 코드별 월별 거래 통계 계산
     */
    @Override
    public Map<String, Long> getMonthlyTransactionStatsByProductCode(String productCode) {
        List<InventoryTransaction> transactions = inventoryTransactionRepository.findAllByProductCode(productCode);
        return transactions.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getTransactionDate().getMonth().toString(),
                        Collectors.summingLong(InventoryTransaction::getQuantity)
                ));
    }

    /**
     * 제품 코드별 주별 거래 통계 계산
     */
    @Override
    public Map<String, Long> getWeeklyTransactionStatsByProductCode(String productCode) {
        List<InventoryTransaction> transactions = inventoryTransactionRepository.findAllByProductCode(productCode);
        return transactions.stream()
                .collect(Collectors.groupingBy(
                        t -> "Week " + t.getTransactionDate().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR),
                        Collectors.summingLong(InventoryTransaction::getQuantity)
                ));
    }

    /**
     * 거래 금액 통계 (특정 제품 코드별)
     */
    @Override
    public Double getCostStatsByProductCode(String productCode) {
        return inventoryTransactionRepository.findAllByProductCode(productCode).stream()
                .mapToDouble(t -> Optional.ofNullable(t.getTransactionValue()).orElse(0.0))
                .sum();
    }

    // 거래 유형별 총 금액
    @Override
    public Map<String, Double> calculateTotalTransactionValueByType() {
        List<InventoryTransaction> transactions = inventoryTransactionRepository.findAll();
        Map<String, Double> totalValues = new HashMap<>();

        for (InventoryTransaction transaction : transactions) {
            String type = transaction.getTransactionType();
            totalValues.put(type, totalValues.getOrDefault(type, 0.0) +
                    (transaction.getTransactionValue() != null ? transaction.getTransactionValue() : 0.0));
        }

        return totalValues;
    }
     // 거래 수량 통계
    @Override
    public Map<String, Long> calculateTransactionQuantities() {
        List<InventoryTransaction> transactions = inventoryTransactionRepository.findAll();
        Map<String, Long> quantities = new HashMap<>();

        for (InventoryTransaction transaction : transactions) {
            String type = transaction.getTransactionType();
            quantities.put(type, quantities.getOrDefault(type, 0L) + transaction.getQuantity());
        }

        return quantities;
    }

    /**
     * DTO를 엔티티로 변환하는 메서드.
     * DTO에서 받은 정보를 이용해 InventoryTransaction 엔티티를 생성한다.
     *
     * @param dto DTO 객체
     * @param inventory Inventory 엔티티 (필요할 경우 외부에서 조회)
     * @return 변환된 InventoryTransaction 엔티티
     */
    @Override
    public InventoryTransaction toEntity(InventoryTransactionDTO dto, Inventory inventory) {
        if (dto == null) {
            return null;
        }

        return InventoryTransaction.builder()
                .transactionCode(dto.getTransactionCode())
                .procurementCode(dto.getProcurementCode())
                .inventory(inventory)
                .productCode(dto.getProductCode())
                .businessId(dto.getBusinessId())
                .transactionType(dto.getTransactionType())
                .quantity(dto.getQuantity())
                .transactionDate(dto.getTransactionDate())
                .transactionValue(dto.getTransactionValue())
                .relatedOrderCode(dto.getRelatedOrderCode())
                .build();
    }

    /**
     * 엔티티를 DTO로 변환하는 메서드.
     * InventoryTransaction 엔티티 정보를 DTO로 매핑한다.
     *
     * @param entity InventoryTransaction 엔티티
     * @return 변환된 InventoryTransactionDTO
     */
    @Override
    public InventoryTransactionDTO toDTO(InventoryTransaction entity) {
        if (entity == null) {
            return null;
        }

        InventoryTransactionDTO.InventoryTransactionDTOBuilder builder = InventoryTransactionDTO.builder()
                .transactionCode(entity.getTransactionCode())
                .procurementCode(entity.getProcurementCode())
                .productCode(entity.getProductCode())
                .transactionType(entity.getTransactionType())
                .businessId(entity.getBusinessId())
                .quantity(entity.getQuantity())
                .transactionDate(entity.getTransactionDate())
                .transactionValue(entity.getTransactionValue())
                .relatedOrderCode(entity.getRelatedOrderCode());

        if (entity.getInventory() != null) {
            builder.inventoryCode(entity.getInventory().getInventoryCode());
        }

        return builder.build();
    }
}
