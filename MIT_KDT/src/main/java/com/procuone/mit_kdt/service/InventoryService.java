package com.procuone.mit_kdt.service;

import com.procuone.mit_kdt.dto.InventoryDTO;
import com.procuone.mit_kdt.entity.Inventory;
import com.procuone.mit_kdt.entity.BOM.Item;

import java.util.List;

public interface InventoryService {

    // Create
    InventoryDTO createInventory(InventoryDTO inventoryDTO);

    // Read (ID로 조회)
    InventoryDTO getInventoryById(String inventoryCode);

    // Read (모든 재고 조회)
    List<InventoryDTO> getAllInventories();

    // Update
    InventoryDTO updateInventory(String inventoryCode, InventoryDTO inventoryDTO);

    // Delete
    void deleteInventory(String inventoryCode);

    InventoryDTO getInventoryByItemId(Long ItemId);

    // Add to inventory (수량 추가)
    void addToInventory(InventoryDTO inventoryDTO);

    // DTO → 엔티티 변환
    Inventory convertDTOToEntity(InventoryDTO dto, Item item);

    // 엔티티 → DTO 변환
    InventoryDTO convertEntityToDTO(Inventory entity);
}
