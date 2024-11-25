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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        purchaseBOMDTO.setQuantity(bomRelationshipRepository.findByChildItemProductCode(purchaseBOMDTO.getProductCode()).get(0).getQuantity());
        // PurchaseBOMDTO를 PurchaseBOM 엔티티로 변환 후 저장
        PurchaseBOM purchaseBOM = convertDTOToEntity(purchaseBOMDTO);

        // 엔티티 저장
        purchaseBOMRepository.save(purchaseBOM);
    }

    @Override
    public List<PurchaseBOMDTO> getPurchaseBOMTree() throws Exception {
        List<BOMRelationship> relationships = bomRelationshipRepository.findAll();
        Map<String, List<PurchaseBOMDTO>> parentChildMap = new HashMap<>();

        // 부모-자식 관계 매핑
        for (BOMRelationship relationship : relationships) {
            PurchaseBOMDTO childDTO = convertRelationshipToDTO(relationship);
            parentChildMap.computeIfAbsent(relationship.getParentItem().getProductCode(), k -> new ArrayList<>()).add(childDTO);
        }

        // 트리 형태로 구성 (루트 노드를 찾고, 재귀적으로 구성)
        List<PurchaseBOMDTO> tree = new ArrayList<>();
        for (String parentProductCode : parentChildMap.keySet()) {
            // 루트 노드를 찾기
            if (!isChild(parentProductCode, relationships)) {
                tree.add(buildTree(parentProductCode, parentChildMap));
            }
        }
        return tree;
    }

    private boolean isChild(String productCode, List<BOMRelationship> relationships) {
        return relationships.stream().anyMatch(rel -> rel.getChildItem().getProductCode().equals(productCode));
    }

    private PurchaseBOMDTO buildTree(String productCode, Map<String, List<PurchaseBOMDTO>> parentChildMap) {
        PurchaseBOMDTO node = new PurchaseBOMDTO();
        node.setProductCode(productCode);
        node.setChildren(parentChildMap.getOrDefault(productCode, new ArrayList<>()));

        for (PurchaseBOMDTO child : node.getChildren()) {
            child.setChildren(parentChildMap.getOrDefault(child.getProductCode(), new ArrayList<>()));
        }
        return node;
    }

    private PurchaseBOMDTO convertRelationshipToDTO(BOMRelationship relationship) {
        // 부모와 자식 아이템 정보를 바탕으로 DTO 생성
        PurchaseBOMDTO dto = PurchaseBOMDTO.builder()
                .productCode(relationship.getParentItem().getProductCode())
                .itemId(relationship.getChildItem().getId())
                .quantity(relationship.getQuantity())
                .build();

        // 자식 아이템들의 관계도 가져와서 트리 형태로 추가
        List<BOMRelationship> childRelationships = bomRelationshipRepository.findByParentItemId(relationship.getChildItem().getId());
        if (!childRelationships.isEmpty()) {
            List<PurchaseBOMDTO> childDTOs = childRelationships.stream()
                    .map(this::convertRelationshipToDTO)
                    .collect(Collectors.toList());
            dto.setChildren(childDTOs);
        }

        return dto;
    }

    private PurchaseBOM convertDTOToEntity(PurchaseBOMDTO purchaseBOMDTO) {
        // Item 및 Company는 적절한 방식으로 찾아와야 함 (repository 등을 이용하여 조회)
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
                .build();
    }

    private PurchaseBOMDTO convertEntityToDTO(PurchaseBOM purchaseBOM) {
        return PurchaseBOMDTO.builder()
                .id(purchaseBOM.getId())
                .productCode(purchaseBOM.getProductCode())
                .itemId(purchaseBOM.getItem().getId())    // 엔티티의 item 필드에서 ID 추출
                .businessId(purchaseBOM.getCompany().getBusinessId()) // 엔티티의 company 필드에서 ID 추출 (String 타입)
                .quantity(purchaseBOM.getQuantity())
                .build();
    }


}
