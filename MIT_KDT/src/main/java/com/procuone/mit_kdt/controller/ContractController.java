package com.procuone.mit_kdt.controller;

import com.procuone.mit_kdt.dto.CompanyDTO;
import com.procuone.mit_kdt.dto.CompanyItemDTO;
import com.procuone.mit_kdt.dto.ItemDTOs.CategoryDTO;
import com.procuone.mit_kdt.dto.ItemDTOs.ItemDTO;
import com.procuone.mit_kdt.entity.BOM.Item;
import com.procuone.mit_kdt.entity.CompanyItem;
import com.procuone.mit_kdt.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/contract")
public class ContractController {

    @Autowired
    private ContractService contractService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CompanyItemService companyItemService;
    @Autowired
    private CompanyService companyService;

    // 품목 목록을 타임리프 뷰에 전달
    @GetMapping("/register")
    public String register(Model model) {
        // 1. 모든 하위 카테고리 조회
        List<CategoryDTO> leafCategories = categoryService.getAllLeafCategories();

        // 2. 하위 카테고리 ID만 추출
        List<Long> categoryIds = leafCategories.stream()
                .map(CategoryDTO::getId)
                .collect(Collectors.toList());

        System.out.println("Category IDs: " + categoryIds);

        // 3. 해당 카테고리들의 아이템 리스트 조회
        List<ItemDTO> items = itemService.getItemsByCategoryIds(categoryIds);
        System.out.println("Items: " + items);
        // 4. 모델에 데이터 추가
        model.addAttribute("items", items);

        // 5. 뷰 렌더링
        return "procurementPlan/compareContracts"; // 뷰 이름을 수정하지 않음 // 품목 목록을 뷰에 전달

    }
    //
    @PostMapping("/suppliers")
    @ResponseBody
    public List<CompanyItemDTO> getSuppliersByItem(@RequestBody Map<String, String> requestBody) {
        String  itemId = requestBody.get("itemId"); // JSON 본문에서 itemId 추출
        Optional<ItemDTO> itemDTO = itemService.findByProductCode(itemId);

        return companyItemService.getSuppliersByItem(itemDTO.get().getId()); // 공급업체 목록 반환
    }

    @GetMapping("/contractWithCompany/{businessId}")
    public String contractWithCompany(@PathVariable String businessId, Model model) {

        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++businessId: " + businessId);

        // businessId를 이용해서 업체 정보와 계약 정보를 DB에서 조회
        CompanyDTO companyDTO = companyService.getCompanyDetails(businessId);
        CompanyItemDTO companyItemDTO = companyItemService.getCompanyItemByBussinessId(businessId);
        Optional<ItemDTO> itemDTO = itemService.getItemById(companyItemDTO.getItemId());

        // 모델에 데이터를 추가하여 뷰로 전달
        model.addAttribute("companyDTO", companyDTO);
        model.addAttribute("companyItemDTO", companyItemDTO);
        model.addAttribute("itemDTO", itemDTO.get());

        return "procurementPlan/registerContract"; // 계약 등록 페이지로 이동
    }


    // 품목에 맞는 계약 정보를 가져와서 테이블에 출력
    @GetMapping("/compare")
    public String compareContracts(@RequestParam("productCode") String productCode, Model model) {
        List<CompanyItem> companyItems = contractService.getCompanyItemsByProductCode(productCode);
        model.addAttribute("companyItems", companyItems);
        return "procurementPlan/compareContracts :: contractTable"; // 테이블 부분만 업데이트
    }

    @GetMapping("/registerContract")
    public String registerContract() {
        return "procurementPlan/registerContract";
    }

    @GetMapping("/registerContract2")
    public String registerContract2(RedirectAttributes redirectAttributes) {
        return "redirect:/contract/register";
    }
}
