package com.procuone.mit_kdt.service.impl;

import com.procuone.mit_kdt.dto.CompanyInventoryDTO;
import com.procuone.mit_kdt.dto.InventoryDTO;
import com.procuone.mit_kdt.entity.Inventory;
import com.procuone.mit_kdt.repository.InventoryRepository;
import com.procuone.mit_kdt.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional
    public void addToInventory(InventoryDTO inventoryDTO) {
        // Inventory 데이터 조회
        Inventory inventory = inventoryRepository.findByItemId(inventoryDTO.getItemId())
                .orElseGet(() -> Inventory.builder()
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

}
