package com.procuone.mit_kdt.service;

import com.procuone.mit_kdt.dto.CompanyDTO;
import com.procuone.mit_kdt.entity.Company;

import java.util.List;

public interface CompanyService {
    void registerCompany(CompanyDTO companyDTO);
    List<CompanyDTO> getAllCompanies(); // 회사 목록을 반환하는 메소드 추가
}
