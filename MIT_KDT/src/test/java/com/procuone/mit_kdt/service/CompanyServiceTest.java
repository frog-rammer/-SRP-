package com.procuone.mit_kdt.service;

import com.procuone.mit_kdt.dto.CompanyDTO;
import com.procuone.mit_kdt.entity.Company;
import com.procuone.mit_kdt.repository.CompanyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private CompanyService companyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterCompany() {
        // Given: 테스트 데이터 준비
        CompanyDTO companyDTO = CompanyDTO.builder()
                .businessId("123-45-67890")
                .comAdd("서울시 강남구")
                .comId("company123")
                .comEmail("company123@example.com")
                .comManage("김철수")
                .comName("테스트 협력업체")
                .comPhone("02-1234-5678")
                .comPaymentInfo("계좌번호: 123-456-7890")
                .comBank("우리은행")
                .comAccountNumber("1234567890")
                .comEstimateList("견적1, 견적2")
                .build();

        Company company = new Company();
        company.setBusinessId(companyDTO.getBusinessId());
        company.setComName(companyDTO.getComName());
        company.setComId(companyDTO.getComId());
        company.setComAdd(companyDTO.getComAdd());
        company.setComEmail(companyDTO.getComEmail());
        company.setComManage(companyDTO.getComManage());
        company.setComPhone(companyDTO.getComPhone());
        company.setComPaymentInfo(companyDTO.getComPaymentInfo());
        company.setComBank(companyDTO.getComBank());
        company.setComAccountNumber(companyDTO.getComAccountNumber());

        // When: 서비스 메서드 호출
        when(companyRepository.save(any(Company.class))).thenReturn(company);

        // Then: 결과 검증
        companyService.registerCompany(companyDTO);

        verify(companyRepository, times(1)).save(any(Company.class));  // save() 메소드가 한번 호출됐는지 확인
    }
}
