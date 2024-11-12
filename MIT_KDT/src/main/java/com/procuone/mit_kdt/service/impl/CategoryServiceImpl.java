package com.procuone.mit_kdt.service.impl;

import com.procuone.mit_kdt.entity.BOM.Category;
import com.procuone.mit_kdt.repository.CategoryRepository;
import com.procuone.mit_kdt.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public List<Category> getRootCategories() {
        // 상위 카테고리가 없는 최상위 카테고리 가져오기 (대분류)
        return categoryRepository.findByParentIsNull();
    }

    @Override
    public List<Category> getSubCategoriesByParentId(Long parentId) {
        // parentId에 해당하는 하위 카테고리 가져오기
        Optional<Category> parentCategory = categoryRepository.findById(parentId);
        return parentCategory.map(Category::getSubCategories)
                .map(List::copyOf)
                .orElse(List.of());
    }
}
