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
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
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
    public ContractDTO getContractById(Long id) {
        // 계약 ID로 계약 데이터 조회
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Contract not found with id: " + id));

        // Contract 엔티티를 ContractDTO로 변환
        return convertEntityToDTO(contract);
    }

    @Override
    public void deleteExpiredContracts() {
        // 현재 날짜 가져오기
        Date currentDate = new Date(System.currentTimeMillis());
        System.out.println("Expired contracts deleted successfully at " + new Date());
        // 종료일이 현재 날짜 이전인 계약 삭제
        contractRepository.deleteByContractEndDateBefore(currentDate);

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
                .supplyUnit(contract.getSupplyUnit())
                .contractDate(contract.getContractDate())
                .contractEndDate(contract.getContractEndDate())
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
                .supplyUnit(contractDTO.getSupplyUnit())
                .contractDate(contractDTO.getContractDate())
                .contractEndDate(contractDTO.getContractEndDate())
                .contractStatus(contractDTO.isContractStatus())
                .productionQty(contractDTO.getProductionQty())
                .build();
    }

    public String generateContractHtml(Long contractId) {
        // 계약 ID로 계약 데이터 조회
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new NoSuchElementException("Contract not found with id: " + contractId));

        // 계약 데이터를 HTML 형식으로 변환
        return convertContractToHtml(contract);
    }

    private String convertContractToHtml(Contract contract) {
        StringBuilder html = new StringBuilder();

        html.append("<h3>계약서 정보</h3>")
                .append("<p><strong>계약 ID:</strong> ").append(contract.getId()).append("</p>")
                .append("<p><strong>업체명:</strong> ").append(contract.getComName()).append("</p>")
                .append("<p><strong>품목명:</strong> ").append(contract.getItemName()).append("</p>")
                .append("<p><strong>계약 시작일:</strong> ").append(contract.getContractDate()).append("</p>")
                .append("<p><strong>계약 종료일:</strong> ").append(contract.getContractEndDate()).append("</p>")
                .append("<p><strong>단가:</strong> ").append(contract.getUnitCost()).append("</p>")
                .append("<p><strong>소요 시간:</strong> ").append(contract.getLeadTime()).append("</p>")
                .append("<p><strong>생산 수량:</strong> ").append(contract.getProductionQty()).append("</p>")
                .append("<p><strong>계약 상태:</strong> ").append(contract.getContractStatus() ? "활성" : "종료").append("</p>");

        return html.toString();
    }
}