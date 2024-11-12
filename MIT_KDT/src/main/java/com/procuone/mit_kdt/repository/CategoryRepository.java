package com.procuone.mit_kdt.repository;

import com.procuone.mit_kdt.entity.BOM.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // 최상위 카테고리 (부모가 없는 카테고리) 조회
    List<Category> findByParentIsNull();
}
