package com.procuone.mit_kdt.repository;

import com.procuone.mit_kdt.entity.BOM.BOMRelationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BOMRelationshipRepository extends JpaRepository<BOMRelationship, Long> {
    // 상위 아이템에 대한 모든 하위 아이템 관계 조회
    @Query("SELECT r FROM BOMRelationship r WHERE r.parentItem.productCode = :parentProductCode")
    List<BOMRelationship> findChildItemsByParentProductCode(@Param("parentProductCode") String parentProductCode);
    // 상위 제품 코드에 대한 모든 하위 아이템 관계 조회
    List<BOMRelationship> findByParentItemProductCode(String parentProductCode);
    // 특정 하위 제품 코드를 부모로 가지는 모든 관계 조회
    List<BOMRelationship> findByChildItemProductCode(String childProductCode);
}
