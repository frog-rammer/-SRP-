package com.procuone.mit_kdt.service;

import com.procuone.mit_kdt.dto.CompanyInventoryDTO;

import java.util.List;

public interface CompanyInventoryService {
    // 특정 업체의 전체 재고 조회
    List<CompanyInventoryDTO> getInventoryByBusinessId(String businessId);

    // 특정 업체의 특정 품목 재고 조회
    CompanyInventoryDTO getInventoryByBusinessIdAndItemId(String businessId, Long itemId);

    // 재고 저장 또는 업데이트
    CompanyInventoryDTO saveOrUpdateInventory(CompanyInventoryDTO inventoryDTO);
}
