package com.procuone.mit_kdt.service;

import com.procuone.mit_kdt.dto.CompanyItemDTO;
import com.procuone.mit_kdt.dto.ItemDTOs.ItemDTO;
import com.procuone.mit_kdt.entity.CompanyItem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface CompanyItemService {
    void saveCompanyItems(List<CompanyItemDTO> companyItemDTOs);

    // 품목 ID로 공급업체 리스트를 조회하는 메서드 선언
    List<CompanyItemDTO> getSuppliersByItem(Long itemId);

    // BusinessId로 CompanyItemDTO 조회
    CompanyItemDTO getCompanyItemByBussinessId(String businessId);

    // Contract 상태 업데이트
    void updateContractStatus(Long itemId, String businessId, boolean status);

    CompanyItemDTO getCompanyItemByBussinessIdAnditemId(String businessId,Long itemId);
    // Item ID와 Business ID로 CompanyItem 조회
    Optional<CompanyItem> findByItemIdAndBusinessId(Long itemId, String businessId);

    // CompanyItem 업데이트
    void update(CompanyItem companyItem);

    List<ItemDTO> getItemsByBusinessId(String businessId);

    void processExcelFile(MultipartFile file) throws Exception;

    // 특정 카테고리 이름에 해당하며 계약 상태가 false인 업체 리스트
    List<CompanyItemDTO> getSuppliersByCategoryName(String categoryName);

    // 선택된 제품 코드에 따라 계약 상태가 false인 업체 정보 가져오기
    List<CompanyItemDTO> getAvailableCompanyItems(String parentProductCode);

    List<CompanyItem> findItemsByItemId(Long itemId);
}
