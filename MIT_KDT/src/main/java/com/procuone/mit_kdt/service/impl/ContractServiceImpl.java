package com.procuone.mit_kdt.service.impl;

import com.procuone.mit_kdt.dto.ContractDTO;
import com.procuone.mit_kdt.entity.BOM.Item;
import com.procuone.mit_kdt.entity.Company;
import com.procuone.mit_kdt.entity.CompanyItem;
import com.procuone.mit_kdt.entity.Contract;
import com.procuone.mit_kdt.repository.ContractRepository;
import com.procuone.mit_kdt.repository.ItemRepository;
import com.procuone.mit_kdt.repository.CompanyItemRepository;
import com.procuone.mit_kdt.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import java.util.Optional;

@Service
public class ContractServiceImpl implements ContractService {

    @Autowired
    private ItemRepository itemRepository;  // ItemRepository

    @Autowired
    private CompanyItemRepository companyItemRepository;  // CompanyItemRepository

    @Autowired
    private ContractRepository contractRepository;

    @Override
    public void deleteExpiredContracts() {
        // 현재 날짜 가져오기
        Date currentDate = new Date(System.currentTimeMillis());

        // 종료일이 현재 날짜 이전인 계약 삭제
        contractRepository.deleteByContractEndDateBefore(currentDate);
        System.out.println("Expired contracts deleted successfully at " + new Date());
    }

    @Override
    public Contract saveContract(Contract contract) {
        return contractRepository.save(contract); // Contract 저장
    }

    @Override
    public List<Item> getAllItems() {
        return itemRepository.findAll();  // 모든 품목 조회
    }
    @Override
    public List<CompanyItem> getCompanyItemsByProductCode(String productCode) {
        return companyItemRepository.findByItemProductCode(productCode);  // 품목 코드에 맞는 계약 정보 조회
    }

    @Override
    public List<ContractDTO> getContractsByProductCode(String productCode) {
        List<Contract> contracts = contractRepository.findByItemProductCode(productCode);
        return contracts.stream().map(this::convertEntityToDTO).collect(Collectors.toList());
    }

    public void updateContractStatus(Long itemId, String businessId, boolean status) {
        Optional<Contract> Contract =  contractRepository.findByItemIdAndCompany_BusinessId(itemId, businessId);
        if (Contract.isPresent()) {
            Contract con = Contract.get();
            con.setContractStatus(status);
            contractRepository.save(con);  // 상태 업데이트
        }
    }

    private ContractDTO convertEntityToDTO(Contract contract) {
        return ContractDTO.builder()
                .id(contract.getId())
                .businessId(contract.getCompany().getBusinessId())
                .productCode(contract.getItem().getProductCode())
                .comName(contract.getComName())
                .itemName(contract.getItemName())
                .accountInfo(contract.getAccountInfo())
                .unitCost(contract.getUnitCost())
                .leadTime(contract.getLeadTime())
                .contractDate(contract.getContractDate())
                .productionQty(contract.getProductionQty())
                .contractStatus(contract.getContractStatus())
                .build();
    }

    private Contract convertDTOToEntity(ContractDTO contractDTO) {
        return Contract.builder()
                .id(contractDTO.getId())
                .company(Company.builder().businessId(contractDTO.getBusinessId()).build()) // ID 기반의 회사 설정
                .item(Item.builder().productCode(contractDTO.getProductCode()).build()) // ID 기반의 아이템 설정
                .comName(contractDTO.getComName())
                .itemName(contractDTO.getItemName())
                .accountInfo(contractDTO.getAccountInfo())
                .unitCost(contractDTO.getUnitCost())
                .leadTime(contractDTO.getLeadTime())
                .contractDate(contractDTO.getContractDate())
                .contractStatus(contractDTO.isContractStatus())
                .productionQty(contractDTO.getProductionQty())
                .build();
    }
}