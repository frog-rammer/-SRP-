package com.procuone.mit_kdt.controller;

import com.procuone.mit_kdt.dto.ProductDTO;
import com.procuone.mit_kdt.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // 제품 등록
    @PostMapping("/add")
    public ProductDTO addProduct(@RequestBody ProductDTO productDTO) {
        return productService.saveProduct(productDTO);
    }

    // 제품 조회
    @GetMapping("/{productId}")
    public ProductDTO getProduct(@PathVariable String productId) {
        return productService.getProductById(productId);
    }

    // 모든 제품 조회
    @GetMapping
    public List<ProductDTO> getAllProducts() {
        return productService.getAllProducts();
    }
}
