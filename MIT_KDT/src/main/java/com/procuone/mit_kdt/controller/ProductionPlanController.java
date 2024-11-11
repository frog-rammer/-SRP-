package com.procuone.mit_kdt.controller;


import com.procuone.mit_kdt.dto.ProductionPlanDTO;
import com.procuone.mit_kdt.entity.ProductionPlan;
import com.procuone.mit_kdt.service.ProductionPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("productionPlan")
public class ProductionPlanController {

    @Autowired
    private ProductionPlanService productionPlanService;

    @GetMapping("/input")
    public String input(Model model) {
        model.addAttribute("productionPlanDTO", new ProductionPlan());
        return "productionPlanInput";
    }
    @PostMapping("/save")
    public String planSave(@ModelAttribute ProductionPlanDTO planDTO) {

        productionPlanService.savePlan(planDTO);
        return "productionPlanInput";
    }

}
