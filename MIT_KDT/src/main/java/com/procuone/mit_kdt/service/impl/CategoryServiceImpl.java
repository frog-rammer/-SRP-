package com.procuone.mit_kdt.service.impl;

import com.procuone.mit_kdt.dto.ItemDTOs.CategoryDTO;
import com.procuone.mit_kdt.entity.BOM.Category;
import com.procuone.mit_kdt.repository.CategoryRepository;
import com.procuone.mit_kdt.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::convertToDTO) // Category 엔티티를 CategoryDTO로 변환
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryDTO> getRootCategories() {
        return categoryRepository.findByParentIsNull().stream()
                .map(this::convertToDTO) // Category 엔티티를 CategoryDTO로 변환
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryDTO> getSubCategoriesByParentId(Long parentId) {
        return categoryRepository.findByParentId(parentId).stream()
                .map(this::convertToDTO) // Category 엔티티를 CategoryDTO로 변환
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDTO saveCategory(CategoryDTO categoryDTO) {
        return null;
    }

//    // 필요 시 DTO를 엔티티로 변환하는 저장 메서드 예시
//    public Category saveCategory(CategoryDTO categoryDTO) {
//        Category category = convertToEntity(categoryDTO);
//        return categoryRepository.save(category);
//    }

    // 엔티티 -> DTO 변환 메서드
    private CategoryDTO convertToDTO(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .type(category.getType())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .subCategoryIds(category.getSubCategories() != null
                        ? category.getSubCategories().stream().map(Category::getId).collect(Collectors.toSet())
                        : Collections.emptySet())
                .build();
    }

    // DTO -> 엔티티 변환 메서드
    private Category convertToEntity(CategoryDTO categoryDTO) {
        Category category = Category.builder()
                .id(categoryDTO.getId())
                .name(categoryDTO.getName())
                .type(categoryDTO.getType())
                .build();

        // 부모 카테고리를 설정 (필요한 경우 레이지 로딩 등으로 DB에서 직접 조회 필요)
        if (categoryDTO.getParentId() != null) {
            category.setParent(categoryRepository.findById(categoryDTO.getParentId()).orElse(null));
        }

        return category;
    }
}
