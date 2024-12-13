package com.procuone.mit_kdt.service.impl;

import com.procuone.mit_kdt.dto.InventoryTransactionDTO;
import com.procuone.mit_kdt.entity.Inventory;
import com.procuone.mit_kdt.entity.InventoryTransaction;
import com.procuone.mit_kdt.repository.InventoryRepository;
import com.procuone.mit_kdt.repository.InventoryTransactionRepository;
import com.procuone.mit_kdt.service.InventoryTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
        Inventory inventory = inventoryRepository.findById(dto.getInventoryId())
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
     * 제품 코드(productCode)로 해당 거래를 조회하고 DTO 정보로 갱신하는 메서드.
     * productCode가 unique하므로 productCode로 거래를 식별할 수 있다.
     */
    @Override
    public InventoryTransactionDTO updateTransactionByProductCode(String productCode, InventoryTransactionDTO dto) {
        // productCode로 거래 조회
        InventoryTransaction entity = inventoryTransactionRepository.findByProductCode(productCode)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found for product code: " + productCode));

        // inventory 갱신 로직 (DTO에 inventoryId가 있을 경우)
        Inventory inventory = null;
        if (dto.getInventoryId() != null) {
            inventory = inventoryRepository.findById(dto.getInventoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Inventory not found"));
        }

        // productCode는 여기서 productCode로 조회했으므로, DTO의 productCode로 갱신할지, 유지할지 결정
        // 상황에 따라 다르지만, 여기서는 DTO에 productCode가 있으면 업데이트, 없으면 기존값 유지
        String updatedProductCode = (dto.getProductCode() != null) ? dto.getProductCode() : entity.getProductCode();

        InventoryTransaction updatedEntity = InventoryTransaction.builder()
                .transactionCode(entity.getTransactionCode()) // PK는 변경 불가
                .inventory(inventory == null ? entity.getInventory() : inventory)
                .productCode(updatedProductCode)
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
     * 특정 거래 코드(transactionCode)에 해당하는 재고 거래 정보를 업데이트하는 메서드.
     * DTO를 받아 해당 거래 엔티티의 내용을 업데이트 후, DB에 저장하고 갱신된 데이터를 DTO로 반환한다.
     */
    @Override
    public InventoryTransactionDTO updateTransaction(String transactionCode, InventoryTransactionDTO dto) {
        InventoryTransaction entity = inventoryTransactionRepository.findById(transactionCode)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

        Inventory inventory = null;
        if (dto.getInventoryId() != null) {
            inventory = inventoryRepository.findById(dto.getInventoryId())
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
                .inventory(inventory)
                .productCode(dto.getProductCode())
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
                .productCode(entity.getProductCode())
                .transactionType(entity.getTransactionType())
                .quantity(entity.getQuantity())
                .transactionDate(entity.getTransactionDate())
                .transactionValue(entity.getTransactionValue())
                .relatedOrderCode(entity.getRelatedOrderCode());

        if (entity.getInventory() != null) {
            builder.inventoryId(entity.getInventory().getId());
        }

        return builder.build();
    }
}
