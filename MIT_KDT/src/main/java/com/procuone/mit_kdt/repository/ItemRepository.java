package com.procuone.mit_kdt.repository;

import com.procuone.mit_kdt.entity.BOM.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    // 품목명과 규격으로 품목 검색
    Optional<Item> findByItemNameAndDimensions(String itemName, String dimensions);

    // 제품 코드로 품목 검색
    Optional<Item> findByProductCode(String productCode);

    // 카테고리 ID로 품목 검색
    List<Item> findByCategoryId(Long categoryId);

    // 특정 조건에 맞는 모든 공유 품목 검색
    List<Item> findByIsShared(boolean isShared);
}
