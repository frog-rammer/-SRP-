package com.procuone.mit_kdt.service.impl;

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
    public void updateContractStatus(Long itemId, String businessId, boolean status) {
        Optional<Contract> Contract =  contractRepository.findByItemIdAndCompany_BusinessId(itemId, businessId);
        if (Contract.isPresent()) {
            Contract con = Contract.get();
            con.setContractStatus(status);
            contractRepository.save(con);  // 상태 업데이트
        }
    }


}
