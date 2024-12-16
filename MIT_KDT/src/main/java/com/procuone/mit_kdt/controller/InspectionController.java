package com.procuone.mit_kdt.controller;

import com.procuone.mit_kdt.dto.*;
import com.procuone.mit_kdt.entity.DeliveryOrder;
import com.procuone.mit_kdt.entity.PurchaseOrder;
import com.procuone.mit_kdt.service.*;
import com.procuone.mit_kdt.service.impl.PurchaseOrderServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/inspection")
@RequiredArgsConstructor
public class InspectionController {

    @Autowired
    private final InspectionService inspectionService;
    @Autowired
    InventoryService inventoryService;
    @Autowired
    private final InventoryTransactionService inventoryTransactionService;
    @Autowired
    private DeliveryOrderService deliveryOrderService;
    @Autowired
    private PurchaseOrderService purchaseOrderService;
    @Autowired
    private  ItemService itemService;



    @GetMapping("/status")
    public String getInspectionStatus(Model model) {
        List<InspectionDTO> inspections = inspectionService.getAllInspections();

        // 검수 중 또는 불량 상태 데이터
        List<InspectionDTO> ongoingInspections = inspections.stream()
                .filter(inspection -> "검수중".equals(inspection.getInspectionStatus())
                        || "검수완료(불량)".equals(inspection.getInspectionStatus()))
                .toList();
        // 검수 완료 상태 데이터
        List<InspectionDTO> completedInspections = inspections.stream()
                .filter(inspection -> "검수완료".equals(inspection.getInspectionStatus()))
                .toList();
        model.addAttribute("inspections", ongoingInspections);
        model.addAttribute("completedInspections", completedInspections);
        return "materialReceipt/inspectionStatus";
    }


    @PostMapping("/save")
    public String saveInspection(@RequestParam String inspectionCode , @RequestParam(required = false) Long defectiveQuantity) {
        InspectionDTO inspectionDTO = inspectionService.getInspectionById(inspectionCode);
        // defectiveQuantity가 null이면 0으로 설정
        if (defectiveQuantity == null) {
            defectiveQuantity = 0L; // 기본값 0으로 설정
        }
        inspectionDTO.setDefectiveQuantity(defectiveQuantity);
        System.out.println("Received DTO: " + inspectionDTO);
        inspectionService.saveInspection(inspectionDTO);
        inspectionService.processInspection(inspectionDTO);


        DeliveryOrderDTO deliveryOrderDTO = new DeliveryOrderDTO();
        deliveryOrderDTO.setDeliveryCode(inspectionDTO.getDeliveryCode());
        deliveryOrderDTO=deliveryOrderService.getDeliveryOrder(deliveryOrderDTO);

        PurchaseOrderDTO purchaseOrderDTO = purchaseOrderService.getpurchaseOrderById(deliveryOrderDTO.getPurchaseOrderCode());

        Long itemId = itemService.getItemIdByProductCode(inspectionDTO.getProductCode());

        InventoryDTO inventoryDTO = inventoryService.getInventoryByItemId(itemId);

        InventoryTransactionDTO transactionDTO = new InventoryTransactionDTO();
        transactionDTO.setProcurementCode(purchaseOrderDTO.getProcurementPlanCode());
        transactionDTO.setInventoryCode(inventoryDTO.getInventoryCode());
        transactionDTO.setProductCode(inspectionDTO.getProductCode());
        transactionDTO.setBusinessId(inspectionDTO.getBusniessId());
        transactionDTO.setTransactionType("입고");
        transactionDTO.setQuantity(inspectionDTO.getQuantity() - defectiveQuantity);
        transactionDTO.setTransactionDate(LocalDate.now());
        transactionDTO.setTransactionValue((double)(purchaseOrderDTO.getPrice()/purchaseOrderDTO.getQuantity())*(inspectionDTO.getQuantity() - defectiveQuantity));
        transactionDTO.setRelatedOrderCode(purchaseOrderDTO.getPurchaseOrderCode());
        inventoryTransactionService.createTransaction(transactionDTO);
        return "redirect:/inspection/status";
    }


    @GetMapping("/invoice/{inspectionId}")
    public String viewInvoice(@PathVariable String inspectionId, Model model) {
        InspectionDTO inspectionDTO = inspectionService.getInspectionById(inspectionId);
        ContractDTO contractDTO = inspectionService.getContractByInspection(inspectionDTO);

        model.addAttribute("inspection", inspectionDTO);
        model.addAttribute("contract", contractDTO);

        return "materialReceipt/invoice"; // HTML 템플릿 파일 이름
    }
}