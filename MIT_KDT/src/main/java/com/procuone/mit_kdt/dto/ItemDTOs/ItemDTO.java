package com.procuone.mit_kdt.dto.ItemDTOs;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ItemDTO {

    private Long id; // Item ID
    private String productCode; // 제품 코드
    private String itemName; // 품목명
    private String drawingFile; // 도면 파일 경로
    private String dimensions; // 치수 정보

    private Long categoryId; // 카테고리 ID 참조
}
