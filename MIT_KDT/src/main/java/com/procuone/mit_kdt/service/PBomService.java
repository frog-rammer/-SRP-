package com.procuone.mit_kdt.service;


import com.procuone.mit_kdt.dto.ItemDTOs.PurchaseBOMDTO;
import com.procuone.mit_kdt.entity.BOM.PurchaseBOM;

import java.util.List;
import java.util.Optional;

public interface PBomService {
    void registerPurchaseBOM(PurchaseBOMDTO purchaseBOMDTO) throws Exception;
//    List<PurchaseBOMDTO> getPurchaseBOMTree() throws Exception;

    // 특정 제품 코드로 PurchaseBOM 엔티티를 가져오는 메서드
    Optional<PurchaseBOM> getPBomByProductCode(String productCode);
}
