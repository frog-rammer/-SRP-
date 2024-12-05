package com.procuone.mit_kdt.controller;

import com.procuone.mit_kdt.dto.ProductionPlanDTO;
import com.procuone.mit_kdt.dto.ShipmentDTO;
import com.procuone.mit_kdt.service.MaterialIssueService;
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

    @Autowired
    private ProductionPlanService productionPlanService;

    @Autowired
    private MaterialIssueService materialIssueService;

    @GetMapping("/stock")
    public String stock() {
        return "materialIssue/stock";
    }

    @GetMapping("/stockOut")
    public String stockOut(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        // 페이지 정보 생성
        Pageable pageable = PageRequest.of(page, size);

        // 생산 계획 페이지 가져오기
        Page<ProductionPlanDTO> productionPlanPage = productionPlanService.getAllPlans(pageable);
        model.addAttribute("productionPlanList", productionPlanPage.getContent()); // 생산 계획 목록
        model.addAttribute("currentProductionPlanPage", productionPlanPage.getNumber());  // 현재 페이지
        model.addAttribute("totalProductionPlanPages", productionPlanPage.getTotalPages());  // 총 페이지 수
        model.addAttribute("totalProductionPlanItems", productionPlanPage.getTotalElements()); // 전체 아이템 수

        // 인벤토리에서 현재 수량 업데이트  (시간 많이 잡아먹을 수 있으니 업데이트 최적화 필요)


        // 출고 정보 페이지 가져오기
        Page<ShipmentDTO> shipmentPage = materialIssueService.getAllShipments(pageable);
        model.addAttribute("shipmentList", shipmentPage.getContent()); // 출고 목록
        model.addAttribute("currentShipmentPage", shipmentPage.getNumber());  // 현재 페이지
        model.addAttribute("totalShipmentPages", shipmentPage.getTotalPages());  // 총 페이지 수
        model.addAttribute("totalShipmentItems", shipmentPage.getTotalElements()); // 전체 아이템 수

        // 반환할 뷰 이름
        return "materialIssue/stockOut";
    }
}
