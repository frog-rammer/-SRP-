package com.procuone.mit_kdt.controller;

import com.procuone.mit_kdt.dto.ProductionPlanDTO;
import com.procuone.mit_kdt.service.ProductionPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("procumentPlan")
public class ProcumentPlanController {
    @Autowired
    ProductionPlanService productionPlanService;

    @GetMapping("/register")
    public String register(Model model, Pageable pageable) {


        // 페이지 네이션 된 생산계획 모델로 전달까지
        Page<ProductionPlanDTO> productionPlanPage = productionPlanService.getAllPlans(pageable);
        int totalPages = productionPlanPage.getTotalPages(); // 총 페이지 수
        int currentPage = productionPlanPage.getNumber();    // 현재 페이지 번호 (0부터 시작)

        // 페이징 버튼 범위 계산 (최대 5개)
        int startPage = Math.max(1, currentPage - 2);        // 최소 페이지
        int endPage = Math.min(totalPages, currentPage + 3); // 최대 페이지

        model.addAttribute("productionPlanList", productionPlanPage.getContent());
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);


        return "procurementPlan/procumentPlanRegister";
    }
}
