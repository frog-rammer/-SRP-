package com.procuone.mit_kdt.repository;

import com.procuone.mit_kdt.dto.CompanyDTO;
import com.procuone.mit_kdt.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, String> {
    Page<Company> findByComNameContaining(String search, Pageable pageable);

    // businessId로 회사 조회하는 메소드 추가
    Optional<Company> findByBusinessId(String businessId);
    //주어진 계정(comAccount)을 기반으로 회사의 비즈니스 ID를 조회합니다.
    @Query("SELECT c.businessId FROM Company c WHERE c.comId = :comId")
    Optional<String> findBusinessIdByAccount(@Param("comId") String comId);
}
