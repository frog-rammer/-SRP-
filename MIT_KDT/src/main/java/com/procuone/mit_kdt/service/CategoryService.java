package com.procuone.mit_kdt.service;

import com.procuone.mit_kdt.entity.BOM.Category;

import java.util.List;

public interface CategoryService {
    List<Category> getAllCategories();
    List<Category> getRootCategories(); // 최상위 카테고리 목록
    List<Category> getSubCategoriesByParentId(Long parentId); // 부모 ID로 하위 카테고리 가져오기
}
