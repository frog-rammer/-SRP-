package com.procuone.mit_kdt.repository;

import com.procuone.mit_kdt.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    // 제품코드로 조회
    Product findByProductId(String productId);
}
