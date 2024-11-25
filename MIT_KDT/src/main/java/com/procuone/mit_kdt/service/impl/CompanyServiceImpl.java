package com.procuone.mit_kdt.service.impl;

import com.procuone.mit_kdt.dto.CompanyDTO;
import com.procuone.mit_kdt.dto.ContractDTO;
import com.procuone.mit_kdt.entity.Company;
import com.procuone.mit_kdt.entity.CompanyItem;
import com.procuone.mit_kdt.entity.Contract;
import com.procuone.mit_kdt.repository.CompanyRepository;
import com.procuone.mit_kdt.repository.ContractRepository;
import com.procuone.mit_kdt.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CompanyServiceImpl implements CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ContractRepository contractRepository;

    public CompanyServiceImpl(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    public List<ContractDTO> getContractsByBusinessId(String businessId) {
        // 계약된 항목을 엔티티로 조회
        List<Contract> contracts = contractRepository.findByCompany_BusinessId(businessId);

        // Contract 엔티티를 DTO로 변환하여 반환
        return contracts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
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

    @Override
    public CompanyDTO getCompanyDetails(String businessId) {
        // DB에서 businessId로 회사 정보를 조회
        Optional<Company> company = companyRepository.findByBusinessId(businessId); // businessId로 조회

        // 회사가 존재하지 않으면 예외 처리
        company.orElseThrow(() -> new RuntimeException("Company not found with businessId: " + businessId));

        // CompanyDTO 빌더를 사용하여 DTO 객체 반환
        return CompanyDTO.builder()
                .comName(company.get().getComName())
                .businessId(company.get().getBusinessId())
                .comAdd(company.get().getComAdd())
                .comManage(company.get().getComManage())
                .comPhone(company.get().getComPhone())
                .comEmail(company.get().getComEmail())
                .comPaymentInfo(company.get().getComPaymentInfo())
                .comBank(company.get().getComBank())
                .comAccountNumber(company.get().getComAccountNumber())
                .build();
    }

    // 계정으로 사업자 번호 뽑기
    @Override
    public String getCompanyBusinessIdBycomId(String comId) {
        return companyRepository.findBusinessIdByAccount(comId)
                .orElseThrow(() -> new IllegalArgumentException("Business ID not found for comId: " + comId));
    }

    @Override
    public Page<CompanyDTO> searchCompaniesByName(String searchTerm, Pageable pageable) {
        // 회사 이름에 검색어가 포함된 결과를 페이징 처리하여 가져옴
        Page<Company> companyPage = companyRepository.findByComNameContaining(searchTerm, pageable);
        return companyPage.map(this::entityToDto); // 결과를 DTO로 변환
    }

    @Override
    public Company getCompanyEntityByBusinessId(String businessId) {
        return companyRepository.findByBusinessId(businessId)
                .orElseThrow(() -> new RuntimeException("Company not found with businessId: " + businessId));
    }

    // DTO -> Entity 변환
    private Company dtoToEntity(CompanyDTO companyDTO) {
        return Company.builder()
                .businessId(companyDTO.getBusinessId())
                .comId(companyDTO.getComId())
                .comAdd(companyDTO.getComAdd())
                .comEmail(companyDTO.getComEmail())
                .comManage(companyDTO.getComManage())
                .comName(companyDTO.getComName())
                .comPhone(companyDTO.getComPhone())
                .comPaymentInfo(companyDTO.getComPaymentInfo()) // 결제 정보
                .comBank(companyDTO.getComBank()) // 은행
                .comAccountNumber(companyDTO.getComAccountNumber()) // 계좌번호
                .build();
    }

    // Entity -> DTO 변환
    private CompanyDTO entityToDto(Company company) {
        return CompanyDTO.builder()
                .businessId(company.getBusinessId())
                .comId(company.getComId())
                .comAdd(company.getComAdd())
                .comEmail(company.getComEmail())
                .comManage(company.getComManage())
                .comName(company.getComName())
                .comPhone(company.getComPhone())
                .comPaymentInfo(company.getComPaymentInfo()) // 결제 정보
                .comBank(company.getComBank()) // 은행
                .comAccountNumber(company.getComAccountNumber()) // 계좌번호
                .build();
    }

    private ContractDTO convertToDTO(Contract contract) {
        return ContractDTO.builder()
                .id(contract.getId())
                .businessId(contract.getCompany().getBusinessId())
                .comName(contract.getCompany().getComName()) // 회사명
                .productCode(contract.getItem().getProductCode()) // 품목 코드
                .itemName(contract.getItem().getItemName()) // 품목명
                .accountInfo(contract.getCompany().getComPaymentInfo()) // 계좌 정보
                .unitCost(contract.getUnitCost())
                .leadTime(contract.getLeadTime())
                .contractDate(contract.getContractDate())
                .contractEndDate(contract.getContractEndDate())
                .productionQty(contract.getProductionQty())
                .contractStatus(contract.getContractStatus())
                .build();
    }
}
