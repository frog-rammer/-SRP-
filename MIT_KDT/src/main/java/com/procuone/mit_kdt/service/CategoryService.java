package com.procuone.mit_kdt.service;

import com.procuone.mit_kdt.dto.ItemDTOs.CategoryDTO;

import java.util.List;

public interface CategoryService {
    List<CategoryDTO> getAllCategories(); // 모든 카테고리 조회
    List<CategoryDTO> getRootCategories(); // 최상위 카테고리 목록
    List<CategoryDTO> getSubCategoriesByParentId(Long parentId); // 부모 ID로 하위 카테고리 가져오기
    CategoryDTO saveCategory(CategoryDTO categoryDTO); // 카테고리 저장
    List<CategoryDTO> getAllLeafCategories(); // 가장 하위 카테고리를 가져오는 메서드

}
