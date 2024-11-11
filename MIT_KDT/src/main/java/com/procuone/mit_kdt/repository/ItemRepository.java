package com.procuone.mit_kdt.repository;

import com.procuone.mit_kdt.entity.Item;
import com.procuone.mit_kdt.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByProduct(Product product);  // Product 객체를 기준으로 Item을 찾는 메소드
}
