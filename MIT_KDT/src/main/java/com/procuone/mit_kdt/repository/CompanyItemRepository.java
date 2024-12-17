package com.procuone.mit_kdt.repository;

import com.procuone.mit_kdt.dto.CompanyItemDTO;
import com.procuone.mit_kdt.entity.Company;
import com.procuone.mit_kdt.entity.CompanyItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
    // 특정 품목 ID에 해당하며 계약 상태가 false인 CompanyItem 검색
    @Query("SELECT ci FROM CompanyItem ci WHERE ci.item.id = :itemId AND ci.contractStatus = false")
    List<CompanyItem> findByItemIdAndContractStatusFalse(@Param("itemId") Long itemId);

    // 특정 카테고리 이름에 해당하며 계약 상태가 false인 CompanyItem 검색
    @Query("SELECT ci FROM CompanyItem ci WHERE ci.item.category.name = :categoryName AND ci.contractStatus = false")
    List<CompanyItem> findByCategoryNameAndContractStatusFalse(@Param("categoryName") String categoryName);

    @Query("SELECT ci FROM CompanyItem ci WHERE ci.item.productCode IN :childCodes AND ci.contractStatus = false")
    List<CompanyItem> findByParentProductCodeAndContractStatusFalse(@Param("childCodes") List<String> childCodes);

    @Query("SELECT ci FROM CompanyItem ci WHERE ci.item.productCode IN :productCodes AND ci.contractStatus = false")
    List<CompanyItem> findAvailableCompanyItems(@Param("productCodes") List<String> productCodes);

    // JPQL을 사용한 메서드
    @Query("SELECT ci FROM CompanyItem ci WHERE ci.item.id = :itemId AND ci.contractStatus = false")
    List<CompanyItem> findItemsByItemId(@Param("itemId") Long itemId);
}
