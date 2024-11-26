package com.procuone.mit_kdt.controller;

import com.procuone.mit_kdt.dto.*;
import com.procuone.mit_kdt.dto.ItemDTOs.CategoryDTO;
import com.procuone.mit_kdt.dto.ItemDTOs.ItemDTO;
import com.procuone.mit_kdt.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/company")
public class CompanyController {

    @Autowired
    CompanyService companyService;
    @Autowired
    MemberService memberService;
    @Autowired
    ItemService itemService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    CompanyItemService companyItemService;
    @Autowired
    ContractService contractService;
    @Autowired
    CompanyInventoryService companyInventoryService;

    public CompanyController(CompanyService companyService, MemberService memberService) {
        this.companyService = companyService;
        this.memberService = memberService;
    }

    @GetMapping("/contractList")
    public String contractList(Model model, HttpSession session) {
        // 세션에서 로그인된 업체의 businessId 가져오기
        String businessId = (String) session.getAttribute("businessId");

        if (businessId == null) {
            return "redirect:/login"; // 세션이 없으면 로그인 페이지로 리다이렉트
        }

        // 계약된 항목 가져오기
        List<ContractDTO> contracts = companyService.getContractsByBusinessId(businessId);

        // 모델에 계약 데이터 추가
        model.addAttribute("contracts", contracts);

        return "contractListForm"; // 계약 목록 페이지로 이동
    }

    @GetMapping("/companyInventory")
    public String companyInventory(Model model, HttpSession session) {
        String businessId = (String) session.getAttribute("businessId");
        if (businessId == null) {
            return "redirect:/login"; // 로그인 페이지로 리다이렉트
        }

        // 업체의 전체 재고 데이터 조회
        List<CompanyInventoryDTO> inventoryList = companyInventoryService.getInventoryByBusinessId(businessId);
        model.addAttribute("inventoryList", inventoryList); // 전체 재고 리스트

        // 로그인된 업체가 공급하는 품목 리스트 조회
        List<ItemDTO> items = companyItemService.getItemsByBusinessId(businessId);
        model.addAttribute("items", items); // 공급 품목 리스트 추가

        model.addAttribute("inventory", new CompanyInventoryDTO()); // 빈 DTO 추가 (입력용)

        return "companyInventory"; // HTML 템플릿 반환
    }

    @GetMapping("/contract/contractForm/{id}")
    public String viewContractForm(@PathVariable Long id, Model model) {
        // 계약 데이터를 가져옴
        ContractDTO contract = contractService.getContractById(id);

        // 모델에 데이터 추가
        model.addAttribute("contract", contract);

        // 계약서 페이지로 이동
        return "contractForm";
    }

    @GetMapping("/companyDetail/{businessId}")
    public String getCompanyDetails(@PathVariable String businessId, Model model) {
        CompanyDTO companyDTO = companyService.getCompanyDetails(businessId);
        model.addAttribute("companyDTO", companyDTO);
        return "procurementPlan/viewDetailCompany";  // 이 부분이 view의 경로를 정확히 지정해야 합니다.
    }

    // 업체 등록 폼
    @GetMapping("/register")
    public String showCompanyRegisterForm(Model model) {
        model.addAttribute("companyDTO", new CompanyDTO()); // 빈 DTO 객체를 모델에 추가
        return "procurementPlan/registerCompanyForm"; // 해당하는 HTML 파일을 반환 (업체 등록 폼)
    }

    // 업체 등록 처리
    @PostMapping("/register")
    public String registerCompany(@ModelAttribute CompanyDTO companyDTO, @RequestParam String password) {
        // DTO를 서비스로 전달하여 DB에 저장
        companyService.registerCompany(companyDTO);
        // 업체 회원 정보도 멤버 테이블에 저장
        MemberDTO memberDTO = convertCompanyToMemberDTO(companyDTO,password);
        memberService.signup(memberDTO);  // 멤버 테이블에 추가

        return "redirect:/";
    }


    // CompanyDTO를 MemberDTO로 변환하는 메서드
    private MemberDTO convertCompanyToMemberDTO(CompanyDTO companyDTO,String password) {
        return MemberDTO.builder()
                .memberId(companyDTO.getComId())  // 회사 계정으로 멤버 ID를 설정
                .memberName(companyDTO.getComName())
                .password(password)  // 기본 비밀번호 설정 (필요한 로직에 맞게 처리)
                .email(companyDTO.getComEmail())
                .phone(companyDTO.getComPhone())
                .Dno("05")
                .build();
    }


    @GetMapping("/viewCompanyList")
    public String viewCompanyList(Model model, @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size) {

        // 페이징 처리된 회사 목록을 가져옵니다.
        Pageable pageable = PageRequest.of(page, size);
        Page<CompanyDTO> companyPage = companyService.getAllCompanies(pageable);

        // 모델에 회사 목록과 페이징 정보를 추가합니다.
        model.addAttribute("companyList", companyPage.getContent()); // 회사 목록
        model.addAttribute("currentPage", companyPage.getNumber());  // 현재 페이지
        model.addAttribute("totalPages", companyPage.getTotalPages());  // 총 페이지 수
        model.addAttribute("totalItems", companyPage.getTotalElements()); // 전체 아이템 수



        return "procurementPlan/viewCompanylistForm";  // 뷰 이름
    }
    @GetMapping("/search")
    public String searchCompanies(@RequestParam("searchTerm") String searchTerm,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  Model model) {

        // Pageable 객체 생성
        Pageable pageable = PageRequest.of(page, size);

        // 검색된 회사 목록을 가져옴
        Page<CompanyDTO> companyPage = companyService.searchCompaniesByName(searchTerm, pageable);

        // 모델에 데이터 추가
        model.addAttribute("companyList", companyPage.getContent());  // 회사 목록
        model.addAttribute("searchTerm", searchTerm);  // 검색어
        model.addAttribute("currentPage", companyPage.getNumber());  // 현재 페이지
        model.addAttribute("totalPages", companyPage.getTotalPages());  // 총 페이지 수
        model.addAttribute("totalItems", companyPage.getTotalElements()); // 전체 아이템 수

        return "procurementPlan/viewCompanylistForm";  // 검색 결과 페이지로 이동
    }

    @GetMapping("/supplierRregisterProduct")
    public String supplierRregisterProduct(Model model, HttpSession session) {

        // 0. 세션에서 businessId 가져오기
        String businessId = (String) session.getAttribute("businessId");

        if (businessId == null) {
            return "redirect:/login"; // 로그인 페이지로 리다이렉트
        }

        // 1. 모든 하위 카테고리 조회
        List<CategoryDTO> leafCategories = categoryService.getAllLeafCategories();

        // 2. 카테고리 ID만 추출
        List<Long> categoryIds = leafCategories.stream()
                .map(CategoryDTO::getId)
                .collect(Collectors.toList());

        System.out.println("Category IDs: " + categoryIds);
        // 3. 해당 카테고리들의 아이템 리스트 조회
        List<ItemDTO> items = itemService.getItemsByCategoryIds(categoryIds);
        System.out.println("Items: " + items);
        // 4. 모델에 데이터 추가
        model.addAttribute("categories", leafCategories);
        model.addAttribute("items", items);
        model.addAttribute("businessId", businessId); // 세션에서 가져온 businessId 추가
        // 5. 뷰 렌더링
        return "supplier/supplierRregisterProduct";
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

        return "redirect:/company/supplierRregisterProduct";
    }

}
