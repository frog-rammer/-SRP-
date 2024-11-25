package com.procuone.mit_kdt.service;


import com.procuone.mit_kdt.dto.ItemDTOs.PurchaseBOMDTO;

import java.util.List;

public interface PBomService {
    void registerPurchaseBOM(PurchaseBOMDTO purchaseBOMDTO) throws Exception;
//    List<PurchaseBOMDTO> getPurchaseBOMTree() throws Exception;
}
