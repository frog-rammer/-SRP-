package com.procuone.mit_kdt.repository;

import com.procuone.mit_kdt.entity.Company;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CompanyRepositoryTest {

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    void testFindByBusinessId() {
        // Given: 테스트 데이터 삽입
        Company company = new Company();
        company.setBusinessId("123-45-67890");
        company.setComName("테스트 협력업체");
        companyRepository.save(company);

        // When: 회사 정보 조회
        Company foundCompany = companyRepository.findByBusinessId("123-45-67890").orElse(null);

        // Then: 결과 검증
        assertNotNull(foundCompany);
        assertEquals("123-45-67890", foundCompany.getBusinessId());
        assertEquals("테스트 협력업체", foundCompany.getComName());
    }

    @Test
    void testFindBusinessIdByAccount() {
        // Given: 테스트 데이터 삽입
        Company company = new Company();
        company.setComId("company123");
        company.setBusinessId("123-45-67890");
        companyRepository.save(company);

        // When: 회사 ID로 businessId 조회
        String businessId = companyRepository.findBusinessIdByAccount("company123");

        // Then: 결과 검증
        assertEquals("123-45-67890", businessId);
    }
}
