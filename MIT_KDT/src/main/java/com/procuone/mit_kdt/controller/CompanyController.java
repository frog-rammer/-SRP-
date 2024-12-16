package com.procuone.mit_kdt.controller;

import com.procuone.mit_kdt.dto.*;
import com.procuone.mit_kdt.dto.ItemDTOs.CategoryDTO;
import com.procuone.mit_kdt.dto.ItemDTOs.ItemDTO;
import com.procuone.mit_kdt.entity.Company;
import com.procuone.mit_kdt.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/company")
public class CompanyController {

    @Autowired
    private CompanyService companyService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CompanyItemService companyItemService;
    @Autowired
    private ContractService contractService;
    @Autowired
    private CompanyInventoryService companyInventoryService;

    // 업체 계약 목록 보기
    @GetMapping("/contractList")
    public String contractList(Model model, HttpSession session) {
        String businessId = getBusinessIdFromSession(session);
        if (businessId == null) return "redirect:/login";

        List<ContractDTO> contracts = companyService.getContractsByBusinessId(businessId);
        model.addAttribute("contracts", contracts);
        return "contractListForm"; // 계약 목록 페이지
    }

    @GetMapping("/contract/contractForm/{id}")
    public String getContractForm(@PathVariable("id") Long contractId, Model model) {
        ContractDTO contract = contractService.getContractById(contractId);

        ContractDTO contractDTO = contractService.getContractById(contractId);
        CompanyDTO companyDTO =companyService.getCompanyDetails(contractDTO.getBusinessId());

        model.addAttribute("contract", contract);
        model.addAttribute("company", companyDTO);
        return "contractForm";
    }

    // 업체 재고 관리 페이지
    @GetMapping("/companyInventory")
    public String companyInventory(Model model, HttpSession session) {
        String businessId = getBusinessIdFromSession(session);
        if (businessId == null) return "redirect:/login";

        List<CompanyInventoryDTO> inventoryList = companyInventoryService.getInventoryByBusinessId(businessId);
        model.addAttribute("inventoryList", inventoryList);

        List<ItemDTO> items = companyItemService.getItemsByBusinessId(businessId);
        model.addAttribute("items", items);
        model.addAttribute("inventory", new CompanyInventoryDTO());
        return "companyInventory"; // 템플릿 이름
    }
    // 업체 상세 정보 보기
    @GetMapping("/companyDetail/{businessId}")
    public String getCompanyDetails(@PathVariable String businessId, Model model) {
        CompanyDTO companyDTO = companyService.getCompanyDetails(businessId);
        model.addAttribute("companyDTO", companyDTO);
        return "procurementPlan/viewDetailCompany";
    }
    // 업체 등록 폼
    @GetMapping("/register")
    public String showCompanyRegisterForm(Model model) {
        model.addAttribute("companyDTO", new CompanyDTO());
        return "procurementPlan/registerCompanyForm";
    }
    // 업체 등록 처리
    @PostMapping("/register")
    public String registerCompany(@ModelAttribute CompanyDTO companyDTO, @RequestParam String password) {
        companyService.registerCompany(companyDTO);
        MemberDTO memberDTO = convertCompanyToMemberDTO(companyDTO, password);
        memberService.signup(memberDTO);
        return "redirect:/";
    }
    // 업체 목록 보기 (페이징)
    @GetMapping("/viewCompanyList")
    public String viewCompanyList(Model model, @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CompanyDTO> companyPage = companyService.getAllCompanies(pageable);

        model.addAttribute("companyList", companyPage.getContent());
        model.addAttribute("currentPage", companyPage.getNumber());
        model.addAttribute("totalPages", companyPage.getTotalPages());
        model.addAttribute("totalItems", companyPage.getTotalElements());
        return "procurementPlan/viewCompanylistForm";
    }
    // 업체 검색
    @GetMapping("/search")
    public String searchCompanies(@RequestParam("searchTerm") String searchTerm,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CompanyDTO> companyPage = companyService.searchCompaniesByName(searchTerm, pageable);

        model.addAttribute("companyList", companyPage.getContent());
        model.addAttribute("searchTerm", searchTerm);
        model.addAttribute("currentPage", companyPage.getNumber());
        model.addAttribute("totalPages", companyPage.getTotalPages());
        model.addAttribute("totalItems", companyPage.getTotalElements());
        return "procurementPlan/viewCompanylistForm";
    }
    // 업체가 제품 등록하는 페이지
    @GetMapping("/supplierRegisterProduct")
    public String supplierRegisterProduct(Model model, HttpSession session) {
        String businessId = getBusinessIdFromSession(session);
        if (businessId == null) return "redirect:/login";

        List<CategoryDTO> leafCategories = categoryService.getAllLeafCategories();
        List<Long> categoryIds = leafCategories.stream().map(CategoryDTO::getId).collect(Collectors.toList());
        List<ItemDTO> items = itemService.getItemsByCategoryIds(categoryIds);

        model.addAttribute("categories", leafCategories);
        model.addAttribute("items", items);
        model.addAttribute("businessId", businessId);
        return "supplier/supplierRegisterProduct";
    }

    // 재고 저장
    @PostMapping("/inventory/save")
    public String saveInventory(@ModelAttribute CompanyInventoryDTO inventoryDTO, HttpSession session) {
        String businessId = (String) session.getAttribute("businessId");
        if (businessId == null) return "redirect:/login";

        inventoryDTO.setBusinessId(businessId);
        companyInventoryService.saveOrUpdateInventory(inventoryDTO);
        return "redirect:/company/companyInventory";
    }

    // 재고 수정
    @PostMapping("/inventory/update")
    public String updateInventory(@ModelAttribute CompanyInventoryDTO inventoryDTO, HttpSession session) {
        String businessId = getBusinessIdFromSession(session);
        if (businessId == null) return "redirect:/login";

        inventoryDTO.setBusinessId(businessId);
        companyInventoryService.saveOrUpdateInventory(inventoryDTO);

        // 리다이렉트 경로를 "/company/companyInventory"로 수정
        return "redirect:/company/companyInventory";
    }

    // Helper Methods
    private String getBusinessIdFromSession(HttpSession session) {
        return (String) session.getAttribute("businessId");
    }

    private MemberDTO convertCompanyToMemberDTO(CompanyDTO companyDTO, String password) {
        return MemberDTO.builder()
                .memberId(companyDTO.getComId())
                .memberName(companyDTO.getComName())
                .password(password)
                .email(companyDTO.getComEmail())
                .phone(companyDTO.getComPhone())
                .Dno("05")
                .build();
    }

    private List<CompanyItemDTO> parseCompanyItems(Map<String, String> allParams, String businessId) {
        List<CompanyItemDTO> items = new ArrayList<>();
        int index = 0;
        while (allParams.containsKey("itemId_" + index)) {
            try {
                CompanyItemDTO item = CompanyItemDTO.builder()
                        .businessId(businessId)
                        .itemId(Long.parseLong(allParams.get("itemId_" + index)))
                        .leadTime(Integer.parseInt(allParams.get("leadTime_" + index)))
                        .supplyUnit(Integer.parseInt(allParams.get("supplyUnit_" + index)))
                        .productionQty(Integer.parseInt(allParams.get("productionQty_" + index)))
                        .unitCost(Integer.parseInt(allParams.get("unitCost_" + index)))
                        .build();
                items.add(item);
            } catch (Exception e) {
                // 예외 처리 로깅
                System.err.println("Error parsing company item at index " + index + ": " + e.getMessage());
            }
            index++;
        }
        return items;
    }
    @PostMapping("/saveCompanyItem")
    public String saveCompanyItems(
            @RequestParam String businessId,
            @RequestParam Map<String, String> allParams) {

        // 디버깅: 전체 파라미터 출력
        System.out.println("=== Received Parameters ===");
        allParams.forEach((key, value) -> System.out.println(key + ": " + value));

        // DTO 생성
        List<CompanyItemDTO> items = new ArrayList<>();

        // 동적으로 itemId_0, leadTime_0 등을 파싱하여 DTO 리스트 생성
        int index = 0;
        while (allParams.containsKey("itemId_" + index)) {
            try {
                // 개별 데이터 디버깅
                System.out.println("Processing index: " + index);
                System.out.println("itemId: " + allParams.get("itemId_" + index));
                System.out.println("leadTime: " + allParams.get("leadTime_" + index));
                System.out.println("supplyUnit: " + allParams.get("supplyUnit_" + index));
                System.out.println("productionQty: " + allParams.get("productionQty_" + index));
                System.out.println("unitCost: " + allParams.get("unitCost_" + index));

                // DTO 생성
                CompanyItemDTO item = CompanyItemDTO.builder()
                        .businessId(businessId)
                        .itemId(Long.parseLong(allParams.get("itemId_" + index))) // itemId 사용
                        .leadTime(Integer.parseInt(allParams.get("leadTime_" + index)))
                        .supplyUnit(Integer.parseInt(allParams.get("supplyUnit_" + index)))
                        .productionQty(Integer.parseInt(allParams.get("productionQty_" + index)))
                        .unitCost(Integer.parseInt(allParams.get("unitCost_" + index)))
                        .build();
                items.add(item);

                // 디버깅: 생성된 DTO 출력
                System.out.println("Created DTO: " + item);
            } catch (Exception e) {
                // 디버깅: 예외 발생 시 메시지 출력
                System.out.println("Error processing index: " + index + ", error: " + e.getMessage());
            }
            index++;
        }

        // 디버깅: 최종 리스트 출력
        System.out.println("=== Final DTO List ===");
        items.forEach(item -> System.out.println(item));

        // 서비스 호출
        companyItemService.saveCompanyItems(items);

        return "redirect:/company/supplierRegisterProduct";
    }
}
