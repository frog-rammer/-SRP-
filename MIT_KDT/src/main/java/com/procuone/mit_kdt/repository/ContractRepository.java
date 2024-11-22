package com.procuone.mit_kdt.repository;


import com.procuone.mit_kdt.dto.CompanyItemDTO;
import com.procuone.mit_kdt.entity.CompanyItem;

import com.procuone.mit_kdt.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {

    // Item 엔티티의 productCode를 기준으로 계약 목록을 조회하는 쿼리 메서드
    List<Contract> findByItemProductCode(String productCode);
    // 특정 업체와 품목 코드로 계약 정보 조회
    @Query("SELECT c FROM Contract c WHERE c.company.businessId = :businessId AND c.item.productCode = :productCode")
    Contract findContractByBusinessIdAndProductCode(String businessId, String productCode);
    Optional<Contract> findByItemIdAndCompany_BusinessId(Long itemId, String businessId);
}

