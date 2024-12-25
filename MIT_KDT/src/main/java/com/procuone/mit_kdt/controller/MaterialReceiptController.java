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
import java.util.stream.Collectors;
import java.util.stream.IntStream;


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
        // 첫 번째 페이지네이션
        pageable = PageRequest.of(page, size);
        Page<DeliveryOrderDTO> deliveryOrdersPage = deliveryOrderService.searchDeliveryOrders(purchaseOrderCode, productCode, pageable);

        model.addAttribute("deliveryOrdersPage", deliveryOrdersPage);
        model.addAttribute("currentPage", deliveryOrdersPage.getNumber());
        model.addAttribute("totalPages", deliveryOrdersPage.getTotalPages());
        model.addAttribute("totalItems", deliveryOrdersPage.getTotalElements());
        model.addAttribute("pageSize", deliveryOrdersPage.getSize());

        // 페이지 번호 범위 계산 (최대 5개)
        int totalPages = deliveryOrdersPage.getTotalPages();
        model.addAttribute("paginationRange", getPaginationRange(page, totalPages));

        // 두 번째 페이지네이션
        pageable = PageRequest.of(Hpage, Hsize);
        Page<InventoryTransactionDTO> inboundTransactions = inventoryTransactionService.getPagedTransactionsByStatus("입고", pageable);

        model.addAttribute("inboundTransactions", inboundTransactions.getContent());
        model.addAttribute("currentInboundPage", inboundTransactions.getNumber());
        model.addAttribute("totalInboundPages", inboundTransactions.getTotalPages());
        model.addAttribute("totalInboundItems", inboundTransactions.getTotalElements());

        // 페이지 번호 범위 계산 (최대 5개)
        int totalInboundPages = inboundTransactions.getTotalPages();
        model.addAttribute("inboundPaginationRange", getPaginationRange(Hpage, totalInboundPages));

        return "material/stockIn";
    }

    // 페이지네이션 범위를 계산하는 메서드
    private List<Integer> getPaginationRange(int currentPage, int totalPages) {
        int rangeSize = 5; // 최대 페이지 번호 표시 개수
        int start = Math.max(0, currentPage - rangeSize / 2);
        int end = Math.min(totalPages, start + rangeSize);

        if (end - start < rangeSize) {
            start = Math.max(0, end - rangeSize);
        }
        return IntStream.range(start, end).boxed().collect(Collectors.toList());
    }


}