package com.procuone.mit_kdt.service.impl;

import com.procuone.mit_kdt.dto.ProductDTO;
import com.procuone.mit_kdt.entity.Product;
import com.procuone.mit_kdt.repository.ProductRepository;
import com.procuone.mit_kdt.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ProductDTO saveProduct(ProductDTO productDTO) {
        Product product = Product.builder()
                .productId(productDTO.getProductId())
                .productName(productDTO.getProductName())
                .productDimension(productDTO.getProductDimension())
                .productLargeCategory(productDTO.getProductLargeCategory())
                .productMediumCategory(productDTO.getProductMediumCategory())
                .productSmallCategory(productDTO.getProductSmallCategory())
                .productMaterial(productDTO.getProductMaterial())
                .productDrawingFile(productDTO.getProductDrawingFile())
                .productQuantity(productDTO.getProductQuantity())
                .build();

        Product savedProduct = productRepository.save(product);

        return new ProductDTO(
                savedProduct.getProductId(),
                savedProduct.getProductName(),
                savedProduct.getProductDimension(),
                savedProduct.getProductLargeCategory(),
                savedProduct.getProductMediumCategory(),
                savedProduct.getProductSmallCategory(),
                savedProduct.getProductMaterial(),
                savedProduct.getProductDrawingFile(),
                savedProduct.getProductQuantity()
        );
    }

    @Override
    public ProductDTO getProductById(String productId) {
        Product product = productRepository.findByProductId(productId);

        if (product != null) {
            return new ProductDTO(
                    product.getProductId(),
                    product.getProductName(),
                    product.getProductDimension(),
                    product.getProductLargeCategory(),
                    product.getProductMediumCategory(),
                    product.getProductSmallCategory(),
                    product.getProductMaterial(),
                    product.getProductDrawingFile(),
                    product.getProductQuantity()
            );
        }
        return null; // Product not found, you can throw exception if needed
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(product -> new ProductDTO(
                        product.getProductId(),
                        product.getProductName(),
                        product.getProductDimension(),
                        product.getProductLargeCategory(),
                        product.getProductMediumCategory(),
                        product.getProductSmallCategory(),
                        product.getProductMaterial(),
                        product.getProductDrawingFile(),
                        product.getProductQuantity()
                ))
                .collect(Collectors.toList());
    }
}
