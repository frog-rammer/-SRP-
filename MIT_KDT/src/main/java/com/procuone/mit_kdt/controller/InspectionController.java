package com.procuone.mit_kdt.controller;

import com.procuone.mit_kdt.dto.*;
import com.procuone.mit_kdt.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

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
    public String getInspectionStatus(Model model,
                                      @RequestParam(defaultValue = "0") int ongoingPage,
                                      @RequestParam(defaultValue = "0") int errorPage,
                                      @RequestParam(defaultValue = "0") int completedPage,
                                      @RequestParam(defaultValue = "8") int size) {

        // 검수 중 상태 페이지네이션
        Pageable ongoingPageable = PageRequest.of(ongoingPage, size);
        Page<InspectionDTO> ongoingInspections = inspectionService.getInspectionsByStatus("검수중", ongoingPageable);

        // 불량 상태 페이지네이션
        Pageable errorPageable = PageRequest.of(errorPage, size);
        Page<InspectionDTO> errorInspections = inspectionService.getInspectionsByStatus("검수완료(불량)", errorPageable);

        // 검수 완료 상태 페이지네이션
        Pageable completedPageable = PageRequest.of(completedPage, size);
        Page<InspectionDTO> completedInspections = inspectionService.getInspectionsByStatus("검수완료", completedPageable);

        // 블록 처리 변수 설정
        int blockSize = 5; // 페이지 블록 크기 (1~5, 6~10 등)
        setPaginationAttributes(model, "ongoing", ongoingInspections, ongoingPage, size, blockSize);
        setPaginationAttributes(model, "error", errorInspections, errorPage, size, blockSize);
        setPaginationAttributes(model, "completed", completedInspections, completedPage, size, blockSize);

        return "material/inspectionStatus";
    }
    private void setPaginationAttributes(Model model, String prefix, Page<?> pageData, int currentPage, int size, int blockSize) {
        int currentBlock = currentPage / blockSize; // 현재 페이지 블록
        int startPage = currentBlock * blockSize; // 시작 페이지 번호
        int endPage = Math.min(startPage + blockSize, pageData.getTotalPages()); // 끝 페이지 번호

        model.addAttribute(prefix + "Inspections", pageData.getContent());
        model.addAttribute(prefix + "CurrentPage", currentPage);
        model.addAttribute(prefix + "TotalPages", pageData.getTotalPages());
        model.addAttribute(prefix + "PageSize", size);
        model.addAttribute(prefix + "StartPage", startPage);
        model.addAttribute(prefix + "EndPage", endPage);
        model.addAttribute(prefix + "HasPreviousBlock", currentBlock > 0);
        model.addAttribute(prefix + "HasNextBlock", endPage < pageData.getTotalPages());
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

        return "material/invoice"; // HTML 템플릿 파일 이름
    }
}