package com.procuone.mit_kdt.service;

import com.procuone.mit_kdt.dto.CompanyItemDTO;
import com.procuone.mit_kdt.entity.BOM.Item;
import com.procuone.mit_kdt.entity.CompanyItem;

import java.util.List;

public interface ContractService {

    List<Item> getAllItems(); // 모든 품목 가져오기

    List<CompanyItem> getCompanyItemsByProductCode(String productCode); // 선택한 품목에 대한 계약 정보 가져오기
    // 계약 정보를 가져오는 메서드

}
