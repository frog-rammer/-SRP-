package com.procuone.mit_kdt.dto.ItemDTOs;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ItemDTO {

    private Long id; // 기본 키
    private String productCode; // 제품 코드
    private String itemName; // 품목명
    private String drawingFile; // 도면 파일 경로
    private boolean isShared; // 공유 여부
    private String dimensions; // 치수 정보
    private int cost; // 품목 비용
    private Long categoryId; // 카테고리 ID
    private String categoryName; // 카테고리 이름
}
