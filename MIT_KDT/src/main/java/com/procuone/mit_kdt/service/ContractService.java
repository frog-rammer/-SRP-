package com.procuone.mit_kdt.service;

import com.procuone.mit_kdt.entity.BOM.Item;
import com.procuone.mit_kdt.entity.CompanyItem;
import com.procuone.mit_kdt.entity.Contract;

import java.util.List;

public interface ContractService {

    Contract saveContract(Contract contract);

    List<Item> getAllItems(); // 모든 품목 가져오기

    List<CompanyItem> getCompanyItemsByProductCode(String productCode); // 선택한 품목에 대한 계약 정보 가져오기
    // Contract 상태 업데이트
    void updateContractStatus(Long itemId, String businessId,boolean status);
}
