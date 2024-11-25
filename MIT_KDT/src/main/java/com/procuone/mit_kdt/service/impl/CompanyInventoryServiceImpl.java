package com.procuone.mit_kdt.service.impl;

import com.procuone.mit_kdt.dto.CompanyInventoryDTO;
import com.procuone.mit_kdt.entity.BOM.Item;
import com.procuone.mit_kdt.entity.Company;
import com.procuone.mit_kdt.entity.CompanyInventory;
import com.procuone.mit_kdt.repository.CompanyInventoryRepository;
import com.procuone.mit_kdt.repository.CompanyRepository;
import com.procuone.mit_kdt.repository.ItemRepository;
import com.procuone.mit_kdt.service.CompanyInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CompanyInventoryServiceImpl implements CompanyInventoryService {
    @Autowired
    private CompanyInventoryRepository inventoryRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Override
    public List<CompanyInventoryDTO> getInventoryByBusinessId(String businessId) {
        return inventoryRepository.findByCompanyBusinessId(businessId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CompanyInventoryDTO getInventoryByBusinessIdAndItemId(String businessId, Long itemId) {
        return inventoryRepository.findByCompanyBusinessIdAndItemId(businessId, itemId).stream()
                .findFirst()
                .map(this::convertToDTO)
                .orElse(null); // 재고가 없으면 null 반환
    }

    @Override
    public CompanyInventoryDTO saveOrUpdateInventory(CompanyInventoryDTO inventoryDTO) {
        // Company 조회 (businessId로 검색)
        Company company = companyRepository.findByBusinessId(inventoryDTO.getBusinessId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Business ID")); // Optional 처리

        // Item 조회
        Item item = itemRepository.findById(inventoryDTO.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Item ID"));

        // CompanyInventory 조회 또는 생성
        CompanyInventory inventory = inventoryRepository.findByCompanyBusinessIdAndItemId(
                company.getBusinessId(), item.getId()).stream().findFirst().orElse(new CompanyInventory());

        // 재고 업데이트 또는 새로 생성
        inventory.setCompany(company);
        inventory.setItem(item);
        inventory.setCurrentQuantity(inventoryDTO.getCurrentQuantity());
        inventory.setReservedQuantity(inventoryDTO.getReservedQuantity());

        CompanyInventory savedInventory = inventoryRepository.save(inventory);
        return convertToDTO(savedInventory);
    }

    private CompanyInventoryDTO convertToDTO(CompanyInventory entity) {
        return CompanyInventoryDTO.builder()
                .id(entity.getId())
                .businessId(entity.getCompany().getBusinessId()) // businessId를 가져옴
                .companyName(entity.getCompany().getComName()) // Company의 업체명
                .itemId(entity.getItem().getId())
                .itemName(entity.getItem().getItemName()) // Item의 품목명
                .currentQuantity(entity.getCurrentQuantity())
                .reservedQuantity(entity.getReservedQuantity())
                .lastUpdated(entity.getLastUpdated().toString()) // LocalDateTime -> String
                .build();
    }
}
