package com.procuone.mit_kdt.repository;

import com.procuone.mit_kdt.entity.BOM.PurchaseBOM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseBOMRepository extends JpaRepository<PurchaseBOM, Long> {
    // 추가적인 쿼리가 필요한 경우 여기에 작성
}