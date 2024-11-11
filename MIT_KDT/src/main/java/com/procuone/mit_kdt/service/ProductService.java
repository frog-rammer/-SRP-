package com.procuone.mit_kdt.service;

import com.procuone.mit_kdt.dto.ProductDTO;
import com.procuone.mit_kdt.entity.Product;
import java.util.List;

public interface ProductService {
    ProductDTO saveProduct(ProductDTO productDTO); // 제품 저장
    ProductDTO getProductById(String productId); // 제품 조회
    List<ProductDTO> getAllProducts(); // 모든 제품 조회
}
