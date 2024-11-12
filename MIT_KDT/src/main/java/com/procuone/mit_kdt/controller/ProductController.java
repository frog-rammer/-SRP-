package com.procuone.mit_kdt.controller;

import com.procuone.mit_kdt.entity.Item;
import com.procuone.mit_kdt.entity.Product;
import com.procuone.mit_kdt.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Product 관련 API
    @PostMapping
    public Product saveProduct(@RequestBody Product product) {
        return productService.saveProduct(product);
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable("id") Long productId) {
        return productService.getProductById(productId);
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable("id") Long productId) {
        productService.deleteProduct(productId);
    }

    // Item 관련 API (Product 내부에서 아이템을 관리)
    @PostMapping("/{productId}/items")
    public Item saveItem(@PathVariable("productId") Long productId, @RequestBody Item item) {
        return productService.saveItem(productId, item);
    }

    @GetMapping("/{productId}/items")
    public List<Item> getItemsByProduct(@PathVariable("productId") Long productId) {
        return productService.getItemsByProduct(productId);
    }

    @DeleteMapping("/{productId}/items/{itemId}")
    public void deleteItem(@PathVariable("productId") Long productId, @PathVariable("itemId") Long itemId) {
        productService.deleteItem(productId, itemId);
    }
}
