package com.procuone.mit_kdt.controller;


import com.procuone.mit_kdt.dto.ItemDTOs.PurchaseBOMDTO;
import com.procuone.mit_kdt.service.ItemService;
import com.procuone.mit_kdt.service.PBomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/purchase")
public class PBomController {

    @Autowired
    private PBomService pBomService;
    @Autowired
    ItemService itemService;

    @ResponseBody
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerPurchaseBOM(@RequestBody PurchaseBOMDTO purchaseBOMDTO) {
        try {
            System.out.println("Received 여기에 오나?: " + purchaseBOMDTO.toString());
            // 전달된 데이터 확인용 로그 출력
            System.out.println("Received PurchaseBOMDTO: " + purchaseBOMDTO.toString());
            purchaseBOMDTO.setItemId(itemService.findByProductCode(purchaseBOMDTO.getProductCode()).get().getId());
            pBomService.registerPurchaseBOM(purchaseBOMDTO);
            return ResponseEntity.ok(Collections.singletonMap("message", "P-BOM 구성 등록 완료"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Collections.singletonMap("error", "P-BOM 등록 중 문제가 발생했습니다."));
        }
    }

//    @GetMapping("/tree")
//    public ResponseEntity<List<PurchaseBOMDTO>> getPurchaseBOMTree() {
//        try {
//            List<PurchaseBOMDTO> treeData = pBomService.getPurchaseBOMTree();
//            return ResponseEntity.ok(treeData);
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body(null);
//        }
//    }
}
