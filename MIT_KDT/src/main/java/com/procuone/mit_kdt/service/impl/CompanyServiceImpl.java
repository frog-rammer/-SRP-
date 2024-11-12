package com.procuone.mit_kdt.service.impl;

import com.procuone.mit_kdt.dto.CompanyDTO;
import com.procuone.mit_kdt.entity.Company;
import com.procuone.mit_kdt.repository.CompanyRepository;
import com.procuone.mit_kdt.service.CompanyService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyServiceImpl(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    @Transactional
    public void registerCompany(CompanyDTO companyDTO) {
        // DTO -> Entity 변환
        Company company = dtoToEntity(companyDTO);

        // DB에 저장
        companyRepository.save(company);
    }

    @Override
    public Page<CompanyDTO> getAllCompanies(Pageable pageable) {
        Page<Company> companyPage = companyRepository.findAll(pageable);
        return companyPage.map(this::entityToDto); // entityToDto 사용
    }




    // DTO -> Entity 변환
    private Company dtoToEntity(CompanyDTO companyDTO) {
        return Company.builder()
                .businessId(companyDTO.getBusinessId())
                .comAccount(companyDTO.getComAccount())
                .comAdd(companyDTO.getComAdd())
                .comEmail(companyDTO.getComEmail())
                .comManage(companyDTO.getComManage())
                .comName(companyDTO.getComName())
                .comPhone(companyDTO.getComPhone())
                .build();
    }

    // Entity -> DTO 변환
    private CompanyDTO entityToDto(Company company) {
        return CompanyDTO.builder()
                .businessId(company.getBusinessId())
                .comAccount(company.getComAccount())
                .comAdd(company.getComAdd())
                .comEmail(company.getComEmail())
                .comManage(company.getComManage())
                .comName(company.getComName())
                .comPhone(company.getComPhone())
                .build();
    }
}
