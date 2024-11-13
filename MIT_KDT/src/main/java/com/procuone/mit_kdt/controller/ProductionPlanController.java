package com.procuone.mit_kdt.controller;

import com.procuone.mit_kdt.dto.ProductionPlanDTO;
import com.procuone.mit_kdt.service.ProductionPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/productionPlan")
public class ProductionPlanController {

    @Autowired
    private ProductionPlanService productionPlanService;

    @GetMapping("/input")
    public String input(Model model) {
        model.addAttribute("productionPlanDTO", new ProductionPlanDTO());
        return "procurementPlan/productionPlanInput";  // 템플릿 이름
    }

    @GetMapping("/view")
    public String view(Model model) {
        model.addAttribute("productionPlanDTO", new ProductionPlanDTO());
        return "procurementPlan/productionPlanView";
    }


    @PostMapping("/save")
    public String planSave(@ModelAttribute("productionPlanDTO") ProductionPlanDTO productionPlanDTO,
                           BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "productionPlanInput";  // 유효성 검사 실패 시 입력 폼으로 돌아가기
        }

        // 엔티티 저장
        productionPlanService.savePlan(productionPlanDTO);  // 엔티티를 서비스에 전달

        // 성공 메시지 추가
        model.addAttribute("successMessage", "생산 계획이 성공적으로 저장되었습니다.");
        return "procurementPlan/productionPlanInput";
    }

}
