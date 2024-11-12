package com.procuone.mit_kdt.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private String productId; // 제품코드
    private String productName; // 품목명
    private String productDimension; // 규격
    private String productLargeCategory; // 대분류
    private String productMediumCategory; // 중분류
    private String productSmallCategory; // 소분류
    private String productMaterial; // 재질
    private String productDrawingFile; // 도면파일
    private String productQuantity; // 수량
}
