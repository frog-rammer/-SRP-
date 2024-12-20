package com.procuone.mit_kdt.service.impl;

import com.procuone.mit_kdt.dto.ItemDTOs.PurchaseBOMDTO;
import com.procuone.mit_kdt.entity.BOM.BOMRelationship;
import com.procuone.mit_kdt.entity.BOM.Item;
import com.procuone.mit_kdt.entity.BOM.PurchaseBOM;
import com.procuone.mit_kdt.entity.Company;
import com.procuone.mit_kdt.repository.BOMRelationshipRepository;
import com.procuone.mit_kdt.repository.CompanyRepository;
import com.procuone.mit_kdt.repository.ItemRepository;
import com.procuone.mit_kdt.repository.PurchaseBOMRepository;
import com.procuone.mit_kdt.service.PBomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PBomServiceImpl implements PBomService {

    @Autowired
    private PurchaseBOMRepository purchaseBOMRepository;

    @Autowired
    private BOMRelationshipRepository bomRelationshipRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Override
    public void registerPurchaseBOM(PurchaseBOMDTO purchaseBOMDTO) throws Exception {
        // BOMRelationship을 사용하여 해당 품목의 수량 가져오기
        List<BOMRelationship> relationships = bomRelationshipRepository.findByChildItemProductCode(purchaseBOMDTO.getProductCode());
        if (relationships.isEmpty()) {
            throw new IllegalArgumentException("BOM 관계를 찾을 수 없습니다: " + purchaseBOMDTO.getProductCode());
        }

        // 첫 번째 관계의 수량을 DTO에 설정
        purchaseBOMDTO.setQuantity(relationships.get(0).getQuantity());

        // DTO를 엔티티로 변환 후 저장
        PurchaseBOM purchaseBOM = convertDTOToEntity(purchaseBOMDTO);
        purchaseBOMRepository.save(purchaseBOM);
    }

    @Override
    public Optional<PurchaseBOM> getPBomByProductCode(String productCode) {
        // PurchaseBOM 엔티티를 직접 반환
        return purchaseBOMRepository.findByProductCode(productCode);
    }


    private PurchaseBOMDTO convertRelationshipToDTO(BOMRelationship relationship) {
        // BOMRelationship을 바탕으로 DTO 생성
        return PurchaseBOMDTO.builder()
                .productCode(relationship.getChildItem().getProductCode())
                .itemId(relationship.getChildItem().getId())
                .quantity(relationship.getQuantity())
                .build();
    }

    private PurchaseBOM convertDTOToEntity(PurchaseBOMDTO purchaseBOMDTO) {
        // Item 및 Company를 repository를 통해 조회
        Item item = itemRepository.findById(purchaseBOMDTO.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid item ID: " + purchaseBOMDTO.getItemId()));
        Company company = companyRepository.findById(purchaseBOMDTO.getBusinessId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid company ID: " + purchaseBOMDTO.getBusinessId()));

        return PurchaseBOM.builder()
                .id(purchaseBOMDTO.getId())
                .productCode(purchaseBOMDTO.getProductCode())
                .item(item)
                .company(company)
                .quantity(purchaseBOMDTO.getQuantity())
                .unitCost(purchaseBOMDTO.getUnitCost()) // 단가 포함
                .supplyUnit(purchaseBOMDTO.getSupplyUnit())
                .productionQty(purchaseBOMDTO.getProductionQty())
                .build();
    }

    private PurchaseBOMDTO convertEntityToDTO(PurchaseBOM purchaseBOM) {
        return PurchaseBOMDTO.builder()
                .id(purchaseBOM.getId())
                .productCode(purchaseBOM.getProductCode())
                .supplyUnit(purchaseBOM.getSupplyUnit())
                .itemId(purchaseBOM.getItem().getId())
                .businessId(purchaseBOM.getCompany().getBusinessId())
                .quantity(purchaseBOM.getQuantity())
                .unitCost(purchaseBOM.getUnitCost()) // 단가 포함
                .supplyUnit(purchaseBOM.getSupplyUnit())
                .productionQty(purchaseBOM.getProductionQty())
                .build();
    }
}