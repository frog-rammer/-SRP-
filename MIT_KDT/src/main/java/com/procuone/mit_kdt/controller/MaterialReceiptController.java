package com.procuone.mit_kdt.controller;

import com.procuone.mit_kdt.dto.DeliveryOrderDTO;
import com.procuone.mit_kdt.dto.InventoryTransactionDTO;
import com.procuone.mit_kdt.service.DeliveryOrderService;
import com.procuone.mit_kdt.service.InventoryTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/materialReceipt")
public class MaterialReceiptController {
    @Autowired
    DeliveryOrderService deliveryOrderService;
    @Autowired
    InventoryTransactionService inventoryTransactionService;

    @GetMapping("/stockIn")
    public String stockIn(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "0") int Hpage,
            @RequestParam(defaultValue = "6") int Hsize,
            @RequestParam(required = false) String purchaseOrderCode,
            @RequestParam(required = false) String productCode,
            Model model,
            Pageable pageable
    ) {
        pageable = PageRequest.of(page, size);
        Page<DeliveryOrderDTO> deliveryOrdersPage = deliveryOrderService.searchDeliveryOrders(purchaseOrderCode, productCode, pageable);

        model.addAttribute("deliveryOrdersPage", deliveryOrdersPage);
        model.addAttribute("currentPage", deliveryOrdersPage.getNumber());
        model.addAttribute("totalPages", deliveryOrdersPage.getTotalPages());
        model.addAttribute("totalItems", deliveryOrdersPage.getTotalElements());
        model.addAttribute("pageSize", deliveryOrdersPage.getSize());

        pageable = PageRequest.of(Hpage, Hsize);
        // 7. 입고 상태 트랜잭션 가져오기
        Page<InventoryTransactionDTO> inboundTransactions = inventoryTransactionService
                .getPagedTransactionsByStatus("입고", pageable);
        System.out.println("Step 7 - Inbound Transactions: " + inboundTransactions.getContent());
        model.addAttribute("inboundTransactions", inboundTransactions.getContent());
        model.addAttribute("currentInboundPage", inboundTransactions.getNumber());
        model.addAttribute("totalInboundPages", inboundTransactions.getTotalPages());
        model.addAttribute("totalInboundItems", inboundTransactions.getTotalElements());

        return "material/stockIn";
    }


}