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
@RequestMapping("/materialIssue")
public class MaterialIssueController {

    @GetMapping("/stock")
    public String stock() {
        return "materialIssue/stock";
    }
    @Autowired
    private ProductionPlanService productionPlanService;

    @GetMapping("/stockOut")
    public String stockOut(Model model,Pageable pageable, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

        pageable = PageRequest.of(page, size);
        Page<ProductionPlanDTO> productionPlanPage = productionPlanService.getAllPlans(pageable);


        model.addAttribute("productionPlanList", productionPlanPage.getContent()); // 생산 계획 목록
        model.addAttribute("currentPage", productionPlanPage.getNumber());  // 현재 페이지
        model.addAttribute("totalPages", productionPlanPage.getTotalPages());  // 총 페이지 수
        model.addAttribute("totalItems", productionPlanPage.getTotalElements()); // 전체 아이템 수


        return "materialIssue/stockOut";
    }
}
