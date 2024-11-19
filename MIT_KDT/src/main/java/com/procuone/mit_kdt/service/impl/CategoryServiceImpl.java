package com.procuone.mit_kdt.service.impl;

import com.procuone.mit_kdt.dto.ItemDTOs.CategoryDTO;
import com.procuone.mit_kdt.entity.BOM.Category;
import com.procuone.mit_kdt.repository.CategoryRepository;
import com.procuone.mit_kdt.service.CategoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryRepository categoryRepository;


    // 모든 카테고리 가져오기
    @Override
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::convertToDTO) // Entity -> DTO 변환
                .collect(Collectors.toList());
    }

    // 루트 카테고리(부모가 없는 카테고리) 가져오기
    @Override
    public List<CategoryDTO> getRootCategories() {
        return categoryRepository.findByParentIsNull().stream()
                .map(this::convertToDTO) // Entity -> DTO 변환
                .collect(Collectors.toList());
    }

    // 특정 부모 ID를 기준으로 하위 카테고리 가져오기
    @Override
    public List<CategoryDTO> getSubCategoriesByParentId(Long parentId) {
        return categoryRepository.findByParentId(parentId).stream()
                .map(this::convertToDTO) // Entity -> DTO 변환
                .collect(Collectors.toList());
    }

    // 모든 하위(leaf) 카테고리 가져오기
    @Override
    public List<CategoryDTO> getAllLeafCategories() {
        return categoryRepository.findAllLeafCategories().stream()
                .map(this::convertToDTO) // Entity -> DTO 변환
                .collect(Collectors.toList());
    }

    // 카테고리 저장
    @Override
    public CategoryDTO saveCategory(CategoryDTO categoryDTO) {
        // DTO -> Entity 변환 후 저장
        Category category = convertToEntity(categoryDTO);
        Category savedCategory = categoryRepository.save(category);
        // 저장된 Entity -> DTO 변환 후 반환
        return convertToDTO(savedCategory);
    }

    // Entity -> DTO 변환 메서드
    private CategoryDTO convertToDTO(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .type(category.getType())
                .parentId(category.getParent() != null ? category.getParent().getId() : null) // 부모 ID 설정
                .subCategoryIds(category.getSubCategories() != null
                        ? category.getSubCategories().stream().map(Category::getId).collect(Collectors.toSet())
                        : Collections.emptySet()) // 하위 카테고리 ID 집합
                .build();
    }

    // DTO -> Entity 변환 메서드
    private Category convertToEntity(CategoryDTO categoryDTO) {
        Category category = Category.builder()
                .id(categoryDTO.getId())
                .name(categoryDTO.getName())
                .type(categoryDTO.getType())
                .build();

        // 부모 카테고리를 설정
        if (categoryDTO.getParentId() != null) {
            category.setParent(categoryRepository.findById(categoryDTO.getParentId()).orElse(null));
        }

        return category;
    }
}
