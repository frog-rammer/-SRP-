package com.procuone.mit_kdt.service;


import com.procuone.mit_kdt.dto.CompanyItemDTO;
import com.procuone.mit_kdt.dto.ContractDTO;

import com.procuone.mit_kdt.entity.BOM.Item;
import com.procuone.mit_kdt.entity.CompanyItem;
import com.procuone.mit_kdt.entity.Contract;

import java.util.List;

public interface ContractService {

    Contract saveContract(Contract contract);

    List<Item> getAllItems(); // 모든 품목 가져오기

    List<CompanyItem> getCompanyItemsByProductCode(String productCode); // 선택한 품목에 대한 계약 정보 가져오기

    // 계약 정보를 가져오는 메서드


    // 지정된 제품 코드에 해당하는 모든 계약 정보 가져오기
    List<ContractDTO> getContractsByProductCode(String productCode);

    // Contract 상태 업데이트
    void updateContractStatus(Long itemId, String businessId,boolean status);

    void deleteExpiredContracts();

    ContractDTO getContractById(Long id);


}
