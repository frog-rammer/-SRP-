package com.procuone.mit_kdt.repository;

import com.procuone.mit_kdt.entity.Company;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CompanyRepositoryTest {

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    void testFindByBusinessId() {
        // Given: 테스트 데이터 삽입
        Company company = new Company();
        company.setBusinessId("123-45-67890-1");
        company.setComName("업체 A");
        company.setComEmail("company1@company.com");
        company.setComPhone("010-1234-5671");
        company.setComManage("담당자 1");
        company.setComAdd("업체 1 주소");
        company.setComPaymentInfo("은행명 1 / 계좌번호 1");
        company.setComBank("은행명 1");
        company.setComAccountNumber("123-45-67890-1");
        company.setComId("company1");

        // Insert: 회사 정보 저장
        companyRepository.save(company);

        // Then: 회사 정보 조회
        Company foundCompany = companyRepository.findByBusinessId("123-45-67890-1").orElse(null);
        assertNotNull(foundCompany);
        assertEquals("123-45-67890-1", foundCompany.getBusinessId());
        assertEquals("업체 A", foundCompany.getComName());
        assertEquals("company1@company.com", foundCompany.getComEmail());
        assertEquals("010-1234-5671", foundCompany.getComPhone());
    }
}
