package com.procuone.mit_kdt.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.procuone.mit_kdt.dto.ItemDTOs.ItemDTO;
import com.procuone.mit_kdt.dto.ItemDTOs.PurchaseBOMDTO;
import com.procuone.mit_kdt.dto.PurchaseOrderDTO;
import com.procuone.mit_kdt.entity.BOM.BOMRelationship;
import com.procuone.mit_kdt.entity.BOM.Category;
import com.procuone.mit_kdt.entity.BOM.PurchaseBOM;
import com.procuone.mit_kdt.repository.BOMRelationshipRepository;
import com.procuone.mit_kdt.repository.CategoryRepository;
import com.procuone.mit_kdt.service.CategoryService;
import com.procuone.mit_kdt.service.ItemService;
import com.procuone.mit_kdt.service.PBomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/purchase")
public class PBomController {

    @Autowired
    private PBomService pBomService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private BOMRelationshipRepository relationshipRepository;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CategoryRepository categoryRepository;

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

    @PostMapping("/treeInfoGet")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> treeInfoGet(@RequestParam String productCode) {
        try {
            System.out.println("======오나요 ======" + productCode);

            //1. Bomrelatuonship 에서 하위 아이템 목록 모두 가져오기
            List<BOMRelationship> relationships = relationshipRepository.findByParentItemProductCode(productCode);
            //2. 하위 아이템 리스트 ItemDTO 로 생성
            List<ItemDTO> childItems = new ArrayList<ItemDTO>();
            for(BOMRelationship relationship : relationships) {
                System.out.println(relationship.getChildItem().getProductCode());
                Optional<ItemDTO> itemDTOOptional = itemService.findByProductCode(relationship.getChildItem().getProductCode());
                itemDTOOptional.ifPresent(childItems::add);
            }

            // 3. 카테고리 계층을 추적하며 트리 형태로 데이터 구성
            Map<String, Object> tree = new HashMap<>();
            for (ItemDTO itemDTO : childItems) {
                // 각각의 부품에 대한 구매 BOM 확인
                Optional<PurchaseBOM> pBOMOptional = pBomService.getPBomByProductCode(itemDTO.getProductCode());

                // 회사명 및 사업자번호 기본값 설정
                String companyName = "아직 구성되지 않았습니다.";
                String businessId = "아직 구성되지 않았습니다.";

                if (pBOMOptional.isPresent()) {
                    // 구매 BOM 데이터가 있을 경우 회사명 및 사업자번호 설정
                    PurchaseBOM pBOM = pBOMOptional.get();
                    companyName = pBOM.getCompany().getComName();
                    businessId = pBOM.getCompany().getBusinessId();
                }

                System.out.println(itemDTO);

                // 카테고리 계층을 가져옴
                List<String> categoryHierarchy = getCategoryHierarchy(itemDTO.getCategoryId());

                // 트리를 따라 계층적으로 데이터 추가
                Map<String, Object> currentNode = tree;
                for (int i = 0; i < categoryHierarchy.size(); i++) {
                    String categoryName = categoryHierarchy.get(i);

                    // 마지막 계층에 도달하면 아이템 및 구매 BOM 데이터 추가
                    if (i == categoryHierarchy.size() - 1) {
                        currentNode.put(categoryName, Map.of(
                                "아이템 이름", itemDTO.getItemName(),
                                "아이템 코드", itemDTO.getProductCode(),
                                "회사명", companyName,
                                "사업자번호", businessId
                        ));
                    } else {
                        // 중간 계층 생성 (없으면 추가)
                        currentNode = (Map<String, Object>) currentNode.computeIfAbsent(categoryName, k -> new HashMap<>());
                    }
                }
            }

            // 4. JSON으로 변환하여 콘솔에 출력
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonOutput = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(tree);
            System.out.println("==== 트리 JSON 출력 ====");
            System.out.println(jsonOutput);
            return ResponseEntity.ok(tree);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Collections.singletonMap("error", "데이터 조회 중 문제가 발생했습니다."));
        }
    }

    // 부모 추적
    public List<String> getCategoryHierarchy(Long categoryId) {
        // 최종 결과를 저장할 리스트
        List<String> hierarchy = new ArrayList<>();

        // 현재 카테고리를 추적하는 변수
        Category currentCategory = categoryRepository.findById(categoryId).orElse(null);

        // parent를 따라 최상위 카테고리까지 추적
        while (currentCategory != null) {
            // 현재 카테고리 이름을 결과에 추가
            hierarchy.add(currentCategory.getName());

            // 부모 카테고리로 이동
            currentCategory = currentCategory.getParent();
        }

        // 최상위 카테고리부터 정렬하기 위해 리스트를 반대로 정렬
        Collections.reverse(hierarchy);

        return hierarchy;
    }


}
