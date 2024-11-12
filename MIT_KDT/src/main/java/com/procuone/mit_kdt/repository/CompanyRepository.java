package com.procuone.mit_kdt.repository;

import com.procuone.mit_kdt.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, String> {
    Page<Company> findByComNameContaining(String search, Pageable pageable);
    // 추가적인 쿼리가 필요하다면 커스텀 메소드 정의 가능
}
