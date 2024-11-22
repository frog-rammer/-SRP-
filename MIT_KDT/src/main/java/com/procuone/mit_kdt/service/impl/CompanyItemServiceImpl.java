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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CompanyItemServiceImpl implements CompanyItemService {

    @Autowired
    private CompanyItemRepository companyItemRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Override
    public List<CompanyItemDTO> getSuppliersByItem(Long itemId) {
        // 품목 ID를 기반으로 CompanyItem 리스트 조회
        List<CompanyItem> companyItems = companyItemRepository.findByItemId(itemId);

        // DTO로 변환하여 반환
        return companyItems.stream()
                .map(companyItem -> new CompanyItemDTO(
                        companyItem.getId(),
                        companyItem.getCompany().getBusinessId(),
                        companyItem.getItem().getId(),
                        companyItem.getLeadTime(),
                        companyItem.getSupplyUnit(),
                        companyItem.getProductionQty(),
                        companyItem.getUnitCost(),
                        companyItem.getContractStatus()

                ))
                .collect(Collectors.toList());
    }

    @Override
    public CompanyItemDTO getCompanyItemByBussinessId(String businessId) {
        // businessId를 기반으로 CompanyItem을 조회
        List<CompanyItem> companyItems = companyItemRepository.findByCompany_BusinessId(businessId);

        if (companyItems.isEmpty()) {
            throw new IllegalArgumentException("No CompanyItem found for Business ID: " + businessId);
        }

        // 첫 번째 CompanyItem을 DTO로 변환하여 반환 (여러 개의 CompanyItem이 있을 수 있음)
        CompanyItem companyItem = companyItems.get(0);

        return new CompanyItemDTO(
                companyItem.getId(),
                companyItem.getCompany().getBusinessId(),
                companyItem.getItem().getId(),
                companyItem.getLeadTime(),
                companyItem.getSupplyUnit(),
                companyItem.getProductionQty(),
                companyItem.getUnitCost(),
                companyItem.getContractStatus()
        );
    }

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
                .contractStatus(companyItemDTO.getContractStatus())
                .build();

        // 저장
        companyItemRepository.save(companyItem);
    }

    @Override
    public void updateContractStatus(Long itemId, String businessId, boolean status) {
        Optional<CompanyItem> companyItem = companyItemRepository.findByItemIdAndCompany_BusinessId(itemId, businessId);
        if (companyItem.isPresent()) {
            CompanyItem item = companyItem.get();
            item.setContractStatus(status);
            companyItemRepository.save(item);  // 상태 업데이트
        }
    }

    @Override
    public Optional<CompanyItem> findByItemIdAndBusinessId(Long itemId, String businessId) {
        return Optional.empty();
    }

    @Override
    public void update(CompanyItem companyItem) {

    }

}
