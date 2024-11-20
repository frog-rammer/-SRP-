package com.procuone.mit_kdt.service.impl;

import com.procuone.mit_kdt.dto.CompanyItemDTO;
import com.procuone.mit_kdt.entity.BOM.Item;
import com.procuone.mit_kdt.entity.Company;
import com.procuone.mit_kdt.entity.CompanyItem;
import com.procuone.mit_kdt.repository.CompanyItemRepository;
import com.procuone.mit_kdt.repository.CompanyRepository;
import com.procuone.mit_kdt.repository.ItemRepository;
import com.procuone.mit_kdt.service.CompanyItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyItemServiceImpl implements CompanyItemService {

    @Autowired
    private CompanyItemRepository companyItemRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Override
    public void saveCompanyItems(List<CompanyItemDTO> companyItemDTOs) {
        System.out.println("==============================================================" + companyItemDTOs + "===========================================================");
        for (CompanyItemDTO companyItemDTO : companyItemDTOs) {
            saveSingleCompanyItem(companyItemDTO);
        }
    }

    private void saveSingleCompanyItem(CompanyItemDTO companyItemDTO) {
        // Company 조회
        System.out.println("==============================================================" + companyItemDTO.getBusinessId() + "===========================================================");
        Company company = companyRepository.findByBusinessId(companyItemDTO.getBusinessId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Business ID: " + companyItemDTO.getBusinessId()));

        // 디버깅: 조회된 Company 객체 확인
        if (company == null) {
            throw new IllegalStateException("Company is null after retrieval");
        } else {
            System.out.println("Retrieved Company: " + company);
        }

        // Item 조회
        Item item = itemRepository.findById(companyItemDTO.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Item ID: " + companyItemDTO.getItemId()));

        // CompanyItem 생성
        CompanyItem companyItem = CompanyItem.builder()
                .company(company) // Company 설정
                .item(item)       // Item 설정
                .leadTime(companyItemDTO.getLeadTime())
                .supplyUnit(companyItemDTO.getSupplyUnit())
                .productionQty(companyItemDTO.getProductionQty())
                .unitCost(companyItemDTO.getUnitCost())
                .build();

        // 저장
        companyItemRepository.save(companyItem);
    }

}
