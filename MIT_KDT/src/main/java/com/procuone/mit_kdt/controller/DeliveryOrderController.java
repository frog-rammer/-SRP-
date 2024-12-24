package com.procuone.mit_kdt.controller;


import com.procuone.mit_kdt.dto.CompanyInventoryDTO;
import com.procuone.mit_kdt.dto.DeliveryOrderDTO;
import com.procuone.mit_kdt.dto.ItemDTOs.ItemDTO;
import com.procuone.mit_kdt.dto.ProgressInspectionDTO;
import com.procuone.mit_kdt.dto.PurchaseOrderDTO;
import com.procuone.mit_kdt.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/deliveryOrder")
public class DeliveryOrderController {

    @Autowired
    PurchaseOrderService purchaseOrderService;
    @Autowired
    CompanyInventoryService companyInventoryService;
    @Autowired
    ItemService itemService;
    @Autowired
    CompanyService companyService;

    @Autowired
    DeliveryOrderService deliveryOrderService;

    @Autowired
    ProgressInspectionService progressInspectionService;


    @GetMapping("/view")
    public String view(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "8") int size,
                       Model model, Pageable pageable) {

        pageable = PageRequest.of(page, size);

        // 1. 검수 완료된 항목 가져오기
        Page<ProgressInspectionDTO> p = progressInspectionService.getInspectionsByCompleteStatus("검수완료", pageable);

        // 2. 납품 가능 수량 설정
        p.forEach(order -> {
            Long availableQuantity = order.getInspectedQuantity(); // 납품 가능 수량 설정
            order.setAvailableQuantity(availableQuantity);
        });

        // 3. 페이지네이션 정보 계산
        int maxVisiblePages = 5; // 한 번에 표시할 최대 페이지 수
        int totalPages = p.getTotalPages(); // 전체 페이지 수
        int startPage = Math.max(0, page - maxVisiblePages / 2); // 시작 페이지
        int endPage = Math.min(totalPages - 1, startPage + maxVisiblePages - 1); // 종료 페이지

        // 4. 모델에 필요한 데이터 추가
        model.addAttribute("purchaseOrders", p.getContent()); // 현재 페이지 데이터
        model.addAttribute("currentPage", p.getNumber()); // 현재 페이지 번호
        model.addAttribute("totalPages", totalPages); // 전체 페이지 수
        model.addAttribute("pageSize", size); // 페이지 크기
        model.addAttribute("startPage", startPage); // 시작 페이지 번호
        model.addAttribute("endPage", endPage); // 종료 페이지 번호
        model.addAttribute("deliveryOrderDTO", new DeliveryOrderDTO()); // 추가 데이터

        return "procurement/deliveryOrder";
    }


    @PostMapping("/register")
    public String register(@ModelAttribute("deliveryOrderDTO") DeliveryOrderDTO deliveryOrderDTO){

        DeliveryOrderDTO deliveryOrderErrorCheck = deliveryOrderService.registerDeliveryOrder(deliveryOrderDTO);

        //해당 진척검수 삭제
        progressInspectionService.deleteProgressByProductCodeAndPurchaseOrderCodeAndBusinessId(deliveryOrderDTO.getProductCode(),deliveryOrderDTO.getPurchaseOrderCode(),deliveryOrderDTO.getBusinessId());


        return "redirect:/deliveryOrder/view";
    }



}
