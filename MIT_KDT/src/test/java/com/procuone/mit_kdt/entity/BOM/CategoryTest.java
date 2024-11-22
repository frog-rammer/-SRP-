package com.procuone.mit_kdt.entity.BOM;

import jakarta.persistence.*;

@Entity
public class CategoryTest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private CategoryTest parent;

    // 기본 생성자
    public CategoryTest() {
    }

    // 테스트용 생성자
    public CategoryTest(Long id, String name, String type, CategoryTest parent) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.parent = parent;
    }

    // Getter & Setter (생략)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public CategoryTest getParent() {
        return parent;
    }

    public void setParent(CategoryTest parent) {
        this.parent = parent;
    }
}
