package com.procuone.mit_kdt.service.impl;

import com.procuone.mit_kdt.dto.InventoryDTO;
import com.procuone.mit_kdt.entity.Inventory;
import com.procuone.mit_kdt.entity.BOM.Item;
import com.procuone.mit_kdt.repository.InventoryRepository;
import com.procuone.mit_kdt.repository.ItemRepository;
import com.procuone.mit_kdt.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public void addToInventory(InventoryDTO inventoryDTO) {
        // Inventory 데이터 조회
        Inventory inventory = inventoryRepository.findByItemId(inventoryDTO.getItemId())
                .orElseGet(() -> Inventory.builder()
                        .item(itemRepository.findById(inventoryDTO.getItemId())
                                .orElseThrow(() -> new IllegalArgumentException("해당 아이템 ID를 찾을 수 없습니다: " + inventoryDTO.getItemId())))
                        .itemName(inventoryDTO.getItemName())
                        .currentQuantity(0) // 초기 수량 설정
                        .reservedQuantity(0) // 초기 예약 수량 설정
                        .minimumRequired(inventoryDTO.getMinimumRequired() != null ? inventoryDTO.getMinimumRequired() : 0) // 최소 수량 설정
                        .build());

        // 수량 업데이트
        inventory.setCurrentQuantity(inventory.getCurrentQuantity() + inventoryDTO.getCurrentQuantity());

        // 저장
        inventoryRepository.save(inventory);
    }

    // Create
    @Override
    @Transactional
    public InventoryDTO createInventory(InventoryDTO inventoryDTO) {
        Item item = itemRepository.findById(inventoryDTO.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("해당 아이템 ID를 찾을 수 없습니다: " + inventoryDTO.getItemId()));

        Inventory inventory = convertDTOToEntity(inventoryDTO, item);
        Inventory savedInventory = inventoryRepository.save(inventory);
        return convertEntityToDTO(savedInventory);
    }

    // Read (ID로 조회)
    @Override
    public InventoryDTO getInventoryById(String inventoryCode) {
        Inventory inventory = inventoryRepository.findById(inventoryCode)
                .orElseThrow(() -> new IllegalArgumentException("해당 재고 코드를 찾을 수 없습니다: " + inventoryCode));
        return convertEntityToDTO(inventory);
    }

    // Read (모든 재고 조회)
    @Override
    public List<InventoryDTO> getAllInventories() {
        return inventoryRepository.findAll().stream()
                .map(this::convertEntityToDTO)
                .collect(Collectors.toList());
    }

    // Update
    @Override
    @Transactional
    public InventoryDTO updateInventory(String inventoryCode, InventoryDTO inventoryDTO) {
        Inventory inventory = inventoryRepository.findById(inventoryCode)
                .orElseThrow(() -> new IllegalArgumentException("해당 재고 코드를 찾을 수 없습니다: " + inventoryCode));

        inventory.setItemName(inventoryDTO.getItemName());
        inventory.setCurrentQuantity(inventoryDTO.getCurrentQuantity());
        inventory.setReservedQuantity(inventoryDTO.getReservedQuantity());
        inventory.setMinimumRequired(inventoryDTO.getMinimumRequired());
        inventory.setCoooperationCompanyInvertoryId(inventoryDTO.getCoooperationCompanyInvertoryId());

        Inventory updatedInventory = inventoryRepository.save(inventory);
        return convertEntityToDTO(updatedInventory);
    }

    // Delete
    @Override
    @Transactional
    public void deleteInventory(String inventoryCode) {
        Inventory inventory = inventoryRepository.findById(inventoryCode)
                .orElseThrow(() -> new IllegalArgumentException("해당 재고 코드를 찾을 수 없습니다: " + inventoryCode));
        inventoryRepository.delete(inventory);
    }

    @Override
    public InventoryDTO getInventoryByItemId(Long itemId) {
        // Repository를 사용하여 Inventory 조회
        Inventory inventory = inventoryRepository.findByItemId(itemId).orElse(null);

        // 재고가 없으면 null 반환
        if (inventory == null) {
            return null;
        }

        // 엔티티 → DTO 변환 후 반환
        return convertEntityToDTO(inventory);
    }

    // DTO → 엔티티 변환
    @Override
    public Inventory convertDTOToEntity(InventoryDTO dto, Item item) {
        return Inventory.builder()
                .InventoryCode(dto.getInventoryCode())
                .item(item)
                .itemName(dto.getItemName())
                .currentQuantity(dto.getCurrentQuantity())
                .reservedQuantity(dto.getReservedQuantity())
                .minimumRequired(dto.getMinimumRequired())
                .CoooperationCompanyInvertoryId(dto.getCoooperationCompanyInvertoryId())
                .lastUpdated(dto.getLastUpdated())
                .build();
    }

    // 엔티티 → DTO 변환
    @Override
    public  InventoryDTO convertEntityToDTO(Inventory entity) {
        return InventoryDTO.builder()
                .InventoryCode(entity.getInventoryCode())
                .itemId(entity.getItem().getId())
                .itemName(entity.getItemName())
                .currentQuantity(entity.getCurrentQuantity())
                .reservedQuantity(entity.getReservedQuantity())
                .minimumRequired(entity.getMinimumRequired())
                .coooperationCompanyInvertoryId(entity.getCoooperationCompanyInvertoryId())
                .lastUpdated(entity.getLastUpdated())
                .build();
    }
}
