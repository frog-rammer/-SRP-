package com.procuone.mit_kdt.service;

import com.procuone.mit_kdt.entity.BOM.Category;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CategoryService {
    List<Category> getAllCategories();
    Optional<Category> getCategoryById(Long id);
    public Map<String, List<Category>> getCategoriesGroupedByType();
}
