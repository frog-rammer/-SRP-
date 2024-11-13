package com.procuone.mit_kdt.controller;

import com.procuone.mit_kdt.dto.CompanyDTO;
import com.procuone.mit_kdt.service.CompanyService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/company")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    // 업체 등록 폼
    @GetMapping("/register")
    public String showCompanyRegisterForm(Model model) {
        model.addAttribute("CompanyDTO", new CompanyDTO()); // 빈 DTO 객체를 모델에 추가
        return "procurementPlan/registerCompanyForm"; // 해당하는 HTML 파일을 반환 (업체 등록 폼)
    }

    @GetMapping("/viewCompanyList")
    public String viewCompanyList(Model model, @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        // 페이징 처리된 회사 목록을 가져옵니다.
        Page<CompanyDTO> companyPage = companyService.getAllCompanies(pageable);

        // 모델에 회사 목록과 페이징 정보를 추가합니다.
        model.addAttribute("companyList", companyPage.getContent()); // 회사 목록
        model.addAttribute("currentPage", companyPage.getNumber());  // 현재 페이지
        model.addAttribute("totalPages", companyPage.getTotalPages());  // 총 페이지 수
        model.addAttribute("totalItems", companyPage.getTotalElements()); // 전체 아이템 수

        return "procurementPlan/viewCompanylistForm";  // 뷰 이름
    }



    // 업체 등록 처리
    @PostMapping("/register")
    public String registerCompany(@ModelAttribute CompanyDTO companyDTO) {
        // DTO를 서비스로 전달하여 DB에 저장
        companyService.registerCompany(companyDTO);
        return "redirect:/company/viewCompanyList"; // 등록 완료 후 리다이렉트
    }

}
