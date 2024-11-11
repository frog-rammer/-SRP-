package com.procuone.mit_kdt.repository;

import com.procuone.mit_kdt.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // 필요에 따라 커스텀 쿼리 메소드 추가 가능
    // 예: Product 이름으로 검색
    // List<Product> findByProductName(String productName);
}
