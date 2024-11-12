package com.procuone.mit_kdt.controller;

import com.procuone.mit_kdt.dto.CompanyDTO;
import com.procuone.mit_kdt.entity.Company;
import com.procuone.mit_kdt.service.CompanyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

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
        return "registerCompanyForm"; // 해당하는 HTML 파일을 반환 (업체 등록 폼)
    }

    @GetMapping("/viewCompanyList")
    public String viewCompanyList(Model model) {
        List<Company> companyList = companyService.getAllCompanies();  // 회사 목록 가져오기
        model.addAttribute("companyList", companyList);  // 모델에 추가
        return "viewCompanyListForm";  // 뷰 이름
    }

    // 업체 등록 처리
    @PostMapping("/register")
    public String registerCompany(@ModelAttribute("CompanyDTO") CompanyDTO companyDTO) {
        // DTO를 서비스로 전달하여 DB에 저장
        companyService.registerCompany(companyDTO);
        return "viewCompanyListForm"; // 등록 완료 후 리다이렉트
    }
}
