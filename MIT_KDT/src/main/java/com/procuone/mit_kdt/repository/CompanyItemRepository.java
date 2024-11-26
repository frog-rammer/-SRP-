package com.procuone.mit_kdt.repository;

import com.procuone.mit_kdt.dto.CompanyItemDTO;
import com.procuone.mit_kdt.entity.Company;
import com.procuone.mit_kdt.entity.CompanyItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyItemRepository extends JpaRepository<CompanyItem, Long> {
    // 품목 코드에 해당하는 계약 정보를 가져오는 메서드
    List<CompanyItem> findByItemProductCode(String productCode);
    // 품목 ID로 공급업체 리스트를 조회하는 메서드
    List<CompanyItem> findByItemId(Long itemId);
    List<CompanyItem> findByCompany_BusinessId(String businessId);  // businessId로 CompanyItem 조회
    Optional<CompanyItem> findByItemIdAndCompany_BusinessId(Long itemId, String businessId);
    Optional<CompanyItem> findByCompany_BusinessIdAndItem_Id(String businessId, Long itemId); //  사업자번호,아이템 아이디로 조회
}
