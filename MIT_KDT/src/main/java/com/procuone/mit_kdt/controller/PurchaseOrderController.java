package com.procuone.mit_kdt.controller;

import com.procuone.mit_kdt.dto.PurchaseOrderDTO;
import com.procuone.mit_kdt.entity.PurchaseOrder;
import com.procuone.mit_kdt.service.InventoryService;
import com.procuone.mit_kdt.service.PurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/purchaseOrder")
public class PurchaseOrderController {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @GetMapping("/purchaseOrders")
    public String purchaseOrders(
            @RequestParam(defaultValue = "0") int page1,
            @RequestParam(defaultValue = "7") int size1,
            @RequestParam(defaultValue = "0") int page2,
            @RequestParam(defaultValue = "7") int size2,
            Model model) {
        // 위쪽 테이블: 자동생성 상태인 항목
        Page<PurchaseOrderDTO> autoGeneratedOrders = purchaseOrderService.getOrdersByStatus("자동생성", page1, size1);
        model.addAttribute("autoOrders", autoGeneratedOrders.getContent());
        model.addAttribute("autoTotalPages", autoGeneratedOrders.getTotalPages());
        model.addAttribute("autoCurrentPage", autoGeneratedOrders.getNumber());

        // 아래쪽 테이블: 발주완료 상태인 항목
        Page<PurchaseOrderDTO> completedOrders = purchaseOrderService.getOrdersByStatus("발주완료", page2, size2);
        model.addAttribute("completedOrders", completedOrders.getContent());
        model.addAttribute("completedTotalPages", completedOrders.getTotalPages());
        model.addAttribute("completedCurrentPage", completedOrders.getNumber());

        // 디버깅 로깅
        System.out.println("자동 생성 리스트: " + autoGeneratedOrders.getContent());
        System.out.println("발주 완료 리스트: " + completedOrders.getContent());

        return "purchaseOrder/purchaseOrders";
    }

    @PostMapping("/order")
    public String completeOrders(@RequestParam List<String> orderIds) {
        // 발주 처리
        purchaseOrderService.completeOrders(orderIds);
        return "redirect:/purchaseOrder/purchaseOrders";
    }

    @GetMapping("/progressInspection")
    public String progressInspection() {
        return "purchaseOrder/progressInspection";
    }

    @GetMapping("/procurementPlanView")
    public String procurementPlanView() {
        return "purchaseOrder/procurementPlanView";
    }
}