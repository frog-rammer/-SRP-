package com.procuone.mit_kdt.repository;

import com.procuone.mit_kdt.entity.CompanyInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyInventoryRepository extends JpaRepository<CompanyInventory, Long> {

    // 특정 업체의 재고를 조회
    List<CompanyInventory> findByCompanyBusinessId(String businessId);

    // 특정 업체의 특정 품목 재고 조회
    List<CompanyInventory> findByCompanyBusinessIdAndItemId(String businessId, Long itemId);

}
