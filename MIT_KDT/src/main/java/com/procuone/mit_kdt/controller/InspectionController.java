package com.procuone.mit_kdt.controller;

import com.procuone.mit_kdt.dto.ContractDTO;
import com.procuone.mit_kdt.dto.InspectionDTO;
import com.procuone.mit_kdt.service.InspectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/inspection")
@RequiredArgsConstructor
public class InspectionController {

    @Autowired
    private final InspectionService inspectionService;

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
    public String saveInspection(@RequestParam String inspectionCode , @RequestParam Long defectiveQuantity) {
        InspectionDTO inspectionDTO = inspectionService.getInspectionById(inspectionCode);
        inspectionDTO.setDefectiveQuantity(defectiveQuantity);
        System.out.println("Received DTO: " + inspectionDTO);
        inspectionService.saveInspection(inspectionDTO);
        inspectionService.processInspection(inspectionDTO);
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