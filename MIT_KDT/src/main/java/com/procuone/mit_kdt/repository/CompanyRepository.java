package com.procuone.mit_kdt.repository;

import com.procuone.mit_kdt.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, String> {
    Page<Company> findByComNameContaining(String search, Pageable pageable);
    // 추가적인 쿼리가 필요하다면 커스텀 메소드 정의 가능

    // businessId로 회사 조회하는 메소드 추가
    Optional<Company> findByBusinessId(String businessId);
    //주어진 계정(comAccount)을 기반으로 회사의 비즈니스 ID를 조회합니다.
    @Query("SELECT c.businessId FROM Company c WHERE c.comId = :comId")
    String findBusinessIdByAccount(String comId);
}
