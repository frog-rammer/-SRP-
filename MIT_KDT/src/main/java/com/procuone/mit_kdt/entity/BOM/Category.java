package com.procuone.mit_kdt.entity.BOM;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // 카테고리 이름 (예: 프레임, 전기모터 등)

    private String type; // 카테고리 타입 (예: 중분류, 소분류)

    @ManyToOne
    @JoinColumn(name = "parent_id") // 상위 카테고리
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private Set<Category> subCategories; // 하위 카테고리 집합

    @OneToMany(mappedBy = "category")
    private Set<Item> items; // 해당 카테고리에 속한 부품들
}
