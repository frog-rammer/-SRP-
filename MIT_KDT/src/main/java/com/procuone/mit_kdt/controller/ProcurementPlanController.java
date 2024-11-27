package com.procuone.mit_kdt.controller;

import com.procuone.mit_kdt.dto.ProcumentPlanDTO;
import com.procuone.mit_kdt.dto.ProductionPlanDTO;
import com.procuone.mit_kdt.entity.ProcurementPlan;
import com.procuone.mit_kdt.service.ProcurementPlanService;
import com.procuone.mit_kdt.service.ProductionPlanService;
import com.procuone.mit_kdt.service.PurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/procumentPlan")
public class ProcurementPlanController {
    @Autowired
    ProductionPlanService productionPlanService;
    @Autowired
    ProcurementPlanService procurementPlanService;
    @Autowired
    PurchaseOrderService purchaseOrderService;
    @GetMapping("/register")
    public String register(Model model, Pageable pageable) {
        // 페이지 네이션 된 생산계획 모델로 전달까지
        Page<ProductionPlanDTO> productionPlanPage = productionPlanService.getAllPlans(pageable);

        // 각 생산 계획에 대해 조달 필요 수량 계산
        List<ProductionPlanDTO> productionPlanList = productionPlanPage.getContent().stream().map(plan -> {
            long requiredQuantity = procurementPlanService.getRequiredProcurementQuantity(plan.getProductPlanCode());
            plan.setRequiredProcurementQuantity(requiredQuantity); // 새로운 필드 설정
            return plan;
        }).collect(Collectors.toList());

        int totalPages = productionPlanPage.getTotalPages(); // 총 페이지 수
        int currentPage = productionPlanPage.getNumber();    // 현재 페이지 번호 (0부터 시작)

        // 페이징 버튼 범위 계산 (최대 5개)
        int startPage = Math.max(1, currentPage - 2);        // 최소 페이지
        int endPage = Math.min(totalPages, currentPage + 3); // 최대 페이지

        // 모델에 추가
        model.addAttribute("productionPlanList", productionPlanList);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("procurementPlanDTO", new ProcumentPlanDTO());

        List<ProcurementPlan> procurementPlanList = procurementPlanService.getAllProcurementPlans();//추가

        return "procurementPlan/procurementPlanRegister";
    }

    @PostMapping("/register")
    public String procurementInput(@ModelAttribute ProcumentPlanDTO procumentPlanDTO) {

        System.out.println("===================== DTO 출력 =================" + procumentPlanDTO.toString());

        //1. 조달계획 테이블에 넣기
        procumentPlanDTO = procurementPlanService.registerProcurementPlan(procumentPlanDTO);
        //2. 발주서 자동생성
        purchaseOrderService.registerPurchaseOrder(procumentPlanDTO);
        // 3. 저장된 조달 계획 리스트를 다시 로드하여 View에 전달
        return "redirect:/procurementPlan/register"; // GET 메서드 호출로 리다이렉트
    }


}
