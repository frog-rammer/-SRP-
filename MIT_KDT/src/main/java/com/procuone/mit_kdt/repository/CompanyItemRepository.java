package com.procuone.mit_kdt.repository;

import com.procuone.mit_kdt.entity.CompanyItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyItemRepository extends JpaRepository<CompanyItem, Long> {
    // 품목 코드에 해당하는 계약 정보를 가져오는 메서드
    List<CompanyItem> findByItemProductCode(String productCode);
}
