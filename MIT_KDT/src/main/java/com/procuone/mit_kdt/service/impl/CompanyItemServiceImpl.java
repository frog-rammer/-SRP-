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

@Service
public class CompanyItemServiceImpl implements CompanyItemService {

    @Autowired
    private CompanyItemRepository companyItemRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Override
    public void saveCompanyItem(CompanyItemDTO companyItemDTO) {
        // Company 조회
        Company company = companyRepository.findById(companyItemDTO.getBusinessId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Business ID: " + companyItemDTO.getBusinessId()));

        // Item 조회
        Item item = itemRepository.findByProductCode(companyItemDTO.getItemCode())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Item Code: " + companyItemDTO.getItemCode()));

        // CompanyItem 생성 및 데이터 설정
        CompanyItem companyItem = CompanyItem.builder()
                .company(company)
                .item(item)
                .leadTime(companyItemDTO.getLeadTime())
                .supplyUnit(companyItemDTO.getSupplyUnit())
                .productionQty(companyItemDTO.getProductionQty())
                .unitCost(companyItemDTO.getUnitCost())
                .build();

        // 저장
        companyItemRepository.save(companyItem);
    }
}
