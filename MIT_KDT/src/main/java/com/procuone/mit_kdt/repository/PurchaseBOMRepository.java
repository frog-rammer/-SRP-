package com.procuone.mit_kdt.repository;

import com.procuone.mit_kdt.entity.BOM.PurchaseBOM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PurchaseBOMRepository extends JpaRepository<PurchaseBOM, Long> {
    // 추가적인 쿼리가 필요한 경우 여기에 작성
    /**
     * 특정 하위 품목 코드에 매핑된 단일 사업자 ID 가져오기
     */
    @Query("SELECT pb.company.businessId FROM PurchaseBOM pb WHERE pb.productCode = :productCode")
    String findBusinessIdByProductCode(@Param("productCode") String productCode);

    /**
     * 특정 productCode에 해당하는 PurchaseBOM을 Optional로 반환.
     *
     * @param productCode 상품 코드
     * @return Optional<PurchaseBOM>
     */
    Optional<PurchaseBOM> findByProductCode(String productCode);
}