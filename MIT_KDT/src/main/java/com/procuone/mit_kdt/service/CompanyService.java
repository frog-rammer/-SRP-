package com.procuone.mit_kdt.service;

import com.procuone.mit_kdt.dto.CompanyDTO;
import com.procuone.mit_kdt.dto.ContractDTO;
import com.procuone.mit_kdt.entity.Company;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CompanyService {

    // 회사 등록
    void registerCompany(CompanyDTO companyDTO);

    // 모든 회사 조회 (DTO 페이징 반환)
    Page<CompanyDTO> getAllCompanies(Pageable pageable);

    // BusinessId로 회사 조회 (DTO 반환)
    CompanyDTO getCompanyDetails(String businessId);

    // Company 엔티티를 BusinessId로 조회
    Company getCompanyEntityByBusinessId(String businessId);

    // 계정으로 사업자 번호 조회
    String getCompanyBusinessIdBycomId(String comId);

    // 이름으로 회사 검색 (DTO 페이징 반환)
    Page<CompanyDTO> searchCompaniesByName(String searchTerm, Pageable pageable);

    List<ContractDTO> getContractsByBusinessId(String businessId); // 계약된 항목 조회
}
