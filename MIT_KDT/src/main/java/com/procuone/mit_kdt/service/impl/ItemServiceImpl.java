package com.procuone.mit_kdt.service.impl;

import com.procuone.mit_kdt.dto.ItemDTOs.ItemDTO;
import com.procuone.mit_kdt.entity.BOM.Category;
import com.procuone.mit_kdt.entity.BOM.Item;
import com.procuone.mit_kdt.repository.CategoryRepository;
import com.procuone.mit_kdt.repository.ItemRepository;
import com.procuone.mit_kdt.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    // 품목 저장 (DTO를 받아 처리)
    @Override
    public boolean saveItem(ItemDTO itemDTO) {
        // 품목명과 규격으로 기존 데이터 확인
        Optional<Item> existingItem = itemRepository.findByItemNameAndDimensions(itemDTO.getItemName(), itemDTO.getDimensions());
        if (existingItem.isPresent()) {
            return false; // 이미 존재하는 품목
        }

        // DTO -> Entity 변환
        Item newItem = convertDTOToEntity(itemDTO);

        // 엔티티 저장
        itemRepository.save(newItem);
        return true; // 새로 저장
    }
    // 최상위 아이템(A 자전거, B 자전거)을 조회
    @Override
    public List<ItemDTO> getTopItems() {
        // `parent_id`가 NULL인 카테고리의 아이템 조회
        List<Item> topItems = itemRepository.findTopItems();

        // 기존 전환 메서드 사용하여 DTO 변환
        return topItems.stream()
                .map(this::convertEntityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ItemDTO> getItemById(Long id) {
        // 엔티티 조회
        Optional<Item> item = itemRepository.findById(id);

        // 엔티티 -> DTO 변환 후 반환
        return item.map(this::convertEntityToDTO);
    }

    // 모든 품목 조회
    @Override
    public List<ItemDTO> getAllItems() {
        List<Item> items = itemRepository.findAll();

        // Entity -> DTO 변환
        return items.stream()
                .map(this::convertEntityToDTO)
                .collect(Collectors.toList());
    }

    // 품목명과 규격으로 품목 검색
    @Override
    public Optional<ItemDTO> findByItemNameAndDimensions(String itemName, String dimensions) {
        // 엔티티를 조회
        Optional<Item> item = itemRepository.findByItemNameAndDimensions(itemName, dimensions);

        // 엔티티 -> DTO 변환 후 반환
        return item.map(this::convertEntityToDTO);
    }

    // 여러 카테고리 ID로 아이템 리스트 조회
    @Override
    public List<ItemDTO> getItemsByCategoryIds(List<Long> categoryIds) {
        return itemRepository.findByCategoryIdIn(categoryIds).stream()
                .map(this::convertEntityToDTO) // Entity -> DTO 변환
                .collect(Collectors.toList());
    }

    //하나의 카테고리로 아이템 리스트 가져오기
    @Override
    public List<ItemDTO> getItemsByCategoryId(Long categoryId) {
        return itemRepository.findByCategoryId(categoryId).stream()
                .map(this::convertEntityToDTO) // Entity -> DTO 변환
                .collect(Collectors.toList());
    }
    
    //제품코드로 아이템아이디 가져오기
    @Override
    public Long getItemIdByProductCode(String productCode) {
        return itemRepository.findIdByProductCode(productCode);
    }


    // 제품 코드로 품목 조회
    @Override
    public Optional<ItemDTO> findByProductCode(String productCode) {
        Optional<Item> item = itemRepository.findByProductCode(productCode);

        // Entity -> DTO 변환
        return item.map(this::convertEntityToDTO);
    }

    // 품목 삭제
    @Override
    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }

    @Override
    public Item getItemEntityByProductCode(String productCode) {
        return itemRepository.findByProductCode(productCode)
                .orElseThrow(() -> new RuntimeException("Item not found with productCode: " + productCode));
    }

    // DTO -> Entity 변환 메서드
    private Item convertDTOToEntity(ItemDTO itemDTO) {
        Item item = Item.builder()
                .id(itemDTO.getId())
                .productCode(itemDTO.getProductCode())
                .itemName(itemDTO.getItemName())
                .drawingFile(itemDTO.getDrawingFile())
                .isShared(itemDTO.isShared())
                .dimensions(itemDTO.getDimensions())
                .build();

        // 카테고리 설정
        if (itemDTO.getCategoryId() != null) {
            Optional<Category> category = categoryRepository.findById(itemDTO.getCategoryId());
            category.ifPresent(item::setCategory);
        }

        return item;
    }

    // Entity -> DTO 변환 메서드
    private ItemDTO convertEntityToDTO(Item item) {
        return ItemDTO.builder()
                .id(item.getId())
                .productCode(item.getProductCode())
                .itemName(item.getItemName())
                .drawingFile(item.getDrawingFile())
                .isShared(item.isShared())
                .dimensions(item.getDimensions())
                .categoryId(item.getCategory() != null ? item.getCategory().getId() : null)
                .categoryName(item.getCategory() != null ? item.getCategory().getName() : null)
                .build();
    }
}
