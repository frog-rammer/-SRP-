package com.procuone.mit_kdt.service;

import com.procuone.mit_kdt.dto.CompanyItemDTO;

import java.util.List;

public interface CompanyItemService {
    void saveCompanyItems(List<CompanyItemDTO> companyItemDTOs);
    // 품목 ID로 공급업체 리스트를 조회하는 메서드 선언
    List<CompanyItemDTO> getSuppliersByItem(Long itemId);
    CompanyItemDTO getCompanyItemByBussinessId(String bussinessId);
}
