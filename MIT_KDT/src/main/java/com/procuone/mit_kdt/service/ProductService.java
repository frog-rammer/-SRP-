package com.procuone.mit_kdt.service;

import com.procuone.mit_kdt.entity.Item;
import com.procuone.mit_kdt.entity.Product;

import java.util.List;

public interface ProductService {
    Product saveProduct(Product product);  // 제품 저장
    Product getProductById(Long productId);  // 제품 조회
    List<Product> getAllProducts();  // 모든 제품 조회
    void deleteProduct(Long productId);  // 제품 삭제

    Item saveItem(Long productId, Item item);  // 제품에 아이템 추가
    List<Item> getItemsByProduct(Long productId);  // 제품의 아이템 목록 조회
    void deleteItem(Long productId, Long itemId);  // 제품에서 아이템 삭제
}
