package com.procuone.mit_kdt.controller;


import com.procuone.mit_kdt.dto.CompanyInventoryDTO;
import com.procuone.mit_kdt.dto.DeliveryOrderDTO;
import com.procuone.mit_kdt.dto.ItemDTOs.ItemDTO;
import com.procuone.mit_kdt.dto.ProgressInspectionDTO;
import com.procuone.mit_kdt.dto.PurchaseOrderDTO;
import com.procuone.mit_kdt.entity.ProgressInspection;
import com.procuone.mit_kdt.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @GetMapping("/view")
    public String view(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "8") int size,
                       Model model) {
        //1. 발주서 쭉 불러오기 ->  납품지시 협력회사 가용재고 확인후 출력
        Page<PurchaseOrderDTO> purchaseOrderDTOS = purchaseOrderService.getOrdersByStatus("발주완료",page,size);
        //2. 납품지시 가능 수량 및 ,
        purchaseOrderDTOS.forEach(order -> {
            Long itemId = itemService.getItemIdByProductCode(order.getProductCode());
            Long availableQuantity = 0L;
            Optional<ItemDTO> itemDTO = null;
            if (itemId != null) {
                itemDTO= itemService.getItemById(itemId);
                CompanyInventoryDTO inventoryDTO = companyInventoryService.getInventoryByBusinessIdAndItemId(
                        order.getBusinessId(), itemId);
                availableQuantity = (inventoryDTO != null) ? inventoryDTO.getCurrentQuantity() : 0L;
            }
            order.setItemName(itemDTO.get().getItemName());
            order.setComName(companyService.getCompanyDetails(order.getBusinessId()).getComName());
            order.setAvailableQuantity(availableQuantity); // 납품 가능 수량 설정
        });

        // 3. 모델에 발주서 리스트 및 페이징 정보 추가
        model.addAttribute("purchaseOrders", purchaseOrderDTOS.getContent()); // 현재 페이지 데이터
        model.addAttribute("currentPage", purchaseOrderDTOS.getNumber()); // 현재 페이지 번호
        model.addAttribute("totalPages", purchaseOrderDTOS.getTotalPages()); // 전체 페이지 수
        model.addAttribute("pageSize", size); // 페이지 크기
        model.addAttribute("deliveryOrderDTO",new DeliveryOrderDTO());

        return "materialReceipt/deliveryOrder";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("deliveryOrderDTO") DeliveryOrderDTO deliveryOrderDTO){

        DeliveryOrderDTO deliveryOrderErrorCheck = deliveryOrderService.registerDeliveryOrder(deliveryOrderDTO);


        return "redirect:/deliveryOrder/view";
    }



}
