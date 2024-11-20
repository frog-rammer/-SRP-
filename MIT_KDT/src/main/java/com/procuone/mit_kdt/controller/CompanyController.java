package com.procuone.mit_kdt.controller;

import com.procuone.mit_kdt.dto.CompanyDTO;
import com.procuone.mit_kdt.dto.CompanyItemDTO;
import com.procuone.mit_kdt.dto.ItemDTOs.CategoryDTO;
import com.procuone.mit_kdt.dto.ItemDTOs.ItemDTO;
import com.procuone.mit_kdt.dto.MemberDTO;
import com.procuone.mit_kdt.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    public CompanyController(CompanyService companyService, MemberService memberService) {
        this.companyService = companyService;
        this.memberService = memberService;
    }

    @GetMapping("/company/{id}")
    public String getCompanyDetails(@PathVariable String businessId, Model model) {
        CompanyDTO companyDTO = companyService.getCompanyDetails(businessId);
        model.addAttribute("companyDTO", companyDTO);
        return "companyDetails";  // Thymeleaf 템플릿 이름
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

        return "redirect:/company/viewCompanyList"; // 등록 완료 후 리다이렉트
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
    @GetMapping("/supplierRregisterProduct")
    public String supplierRregisterProduct(Model model, HttpSession session) {

        // 0. 세션에서 businessId 가져오기
        String businessId = (String) session.getAttribute("businessId");

        if (businessId == null) {
            throw new IllegalStateException("Business ID not found in session");
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
    public String saveCompanyItem(
            @ModelAttribute CompanyItemDTO companyItemDTO) {
        companyItemService.saveCompanyItem(companyItemDTO);
        return "redirect:/company/supplierRregisterProduct";
    }
}
