package com.procuone.mit_kdt.service.impl;

import com.procuone.mit_kdt.entity.Item;
import com.procuone.mit_kdt.entity.Product;
import com.procuone.mit_kdt.repository.ItemRepository;
import com.procuone.mit_kdt.repository.ProductRepository;
import com.procuone.mit_kdt.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, ItemRepository itemRepository) {
        this.productRepository = productRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Product getProductById(Long productId) {
        Optional<Product> product = productRepository.findById(productId);
        return product.orElse(null);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
    }

    @Override
    public Item saveItem(Long productId, Item item) {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isPresent()) {
            item.setProduct(product.get());  // Product와 Item 연결
            return itemRepository.save(item);
        }
        return null;
    }

    @Override
    public List<Item> getItemsByProduct(Long productId) {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isPresent()) {
            return itemRepository.findByProduct(product.get());  // Product 객체를 기반으로 Item 찾기
        }
        return null;
    }


    @Override
    public void deleteItem(Long productId, Long itemId) {
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isPresent() && item.get().getProduct().getProductId().equals(productId)) {
            itemRepository.delete(item.get());
        }
    }
}
