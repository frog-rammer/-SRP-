package com.procuone.mit_kdt.service.impl;

import com.procuone.mit_kdt.dto.ContractDTO;
import com.procuone.mit_kdt.entity.BOM.Item;
import com.procuone.mit_kdt.entity.CompanyItem;
import com.procuone.mit_kdt.entity.Contract;
import com.procuone.mit_kdt.repository.ContractRepository;
import com.procuone.mit_kdt.repository.ItemRepository;
import com.procuone.mit_kdt.repository.CompanyItemRepository;
import com.procuone.mit_kdt.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContractServiceImpl implements ContractService {

    @Autowired
    private ItemRepository itemRepository;  // ItemRepository

    @Autowired
    private CompanyItemRepository companyItemRepository;  // CompanyItemRepository

    @Autowired
    private ContractRepository contractRepository;

    @Override
    public void saveContract(Contract contract) {
        contractRepository.save(contract);
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

    private ContractDTO convertEntityToDTO(Contract contract) {
        return ContractDTO.builder()
                .id(contract.getId())
                .conitemNo(contract.getConitemNo())
                .businessId(contract.getCompany().getBusinessId())
                .companyName(contract.getCompanyName())
                .productCode(contract.getItem().getProductCode())
                .itemName(contract.getItemName())
                .contractPrice(contract.getContractPrice())
                .unitCost(contract.getUnitCost())
                .leadTime(contract.getLeadTime())
                .contractFile(contract.getContractFile())
                .contractInfo(contract.getContractInfo())
                .contractDate(contract.getContractDate())
                .contractStatus(contract.getContractStatus())
                .build();
    }

    private Contract convertDTOToEntity(ContractDTO contractDTO) {
        return Contract.builder()
                .id(contractDTO.getId())
                .conitemNo(contractDTO.getConitemNo())
                .contractPrice(contractDTO.getContractPrice())
                .unitCost(contractDTO.getUnitCost())
                .leadTime(contractDTO.getLeadTime())
                .contractFile(contractDTO.getContractFile())
                .contractInfo(contractDTO.getContractInfo())
                .contractDate(contractDTO.getContractDate())
                .contractStatus(contractDTO.isContractStatus())
                .build();
    }


}
