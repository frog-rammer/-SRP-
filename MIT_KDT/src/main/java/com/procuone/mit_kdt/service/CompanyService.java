package com.procuone.mit_kdt.service;

import com.procuone.mit_kdt.dto.CompanyDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CompanyService {

    // 회사 등록
    public abstract void registerCompany(CompanyDTO companyDTO);

    // 페이징 처리된 회사 리스트 가져오기
    public abstract Page<CompanyDTO> getAllCompanies(Pageable pageable);

    // 특정 회사 상세 조회
    CompanyDTO getCompanyDetails(String businessId);

    // 아이디로 사업자번호 찾기
    String getCompanyBusinessIdBycomId(String comId);

    // 회사 이름으로 검색
    Page<CompanyDTO> searchCompaniesByName(String searchTerm, Pageable pageable);

}
