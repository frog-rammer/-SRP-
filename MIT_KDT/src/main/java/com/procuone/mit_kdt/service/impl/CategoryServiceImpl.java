package com.procuone.mit_kdt.service.impl;

import com.procuone.mit_kdt.entity.BOM.Category;
import com.procuone.mit_kdt.repository.CategoryRepository;
import com.procuone.mit_kdt.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;


    public Map<String, List<Category>> getCategoriesGroupedByType() {
        List<Category> categories = categoryRepository.findAll();
        // 타입별로 카테고리를 그룹화하여 반환
        return categories.stream().collect(Collectors.groupingBy(Category::getType));
    }
    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }
}
