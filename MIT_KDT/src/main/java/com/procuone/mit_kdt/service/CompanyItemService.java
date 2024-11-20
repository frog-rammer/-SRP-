package com.procuone.mit_kdt.service;

import com.procuone.mit_kdt.dto.CompanyItemDTO;

import java.util.List;

public interface CompanyItemService {
    void saveCompanyItems(List<CompanyItemDTO> companyItemDTOs);
}
