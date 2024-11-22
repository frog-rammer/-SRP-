package com.procuone.mit_kdt.repository;

import com.procuone.mit_kdt.entity.BOM.CategoryTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryTestRepository extends JpaRepository<CategoryTest, Long> {
}
