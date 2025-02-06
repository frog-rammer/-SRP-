package com.procuone.mit_kdt.dto.ItemDTOs;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CategoryDTO {
    private Long id; // Category ID
    private String name; // 카테고리 이름
    private String type; // 카테고리 타입 (예: 중분류, 소분류)

    private Long parentId; // 상위 카테고리 ID
    private Set<Long> subCategoryIds; // 하위 카테고리 ID 집합
}
