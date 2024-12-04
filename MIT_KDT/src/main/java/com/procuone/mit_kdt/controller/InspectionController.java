package com.procuone.mit_kdt.controller;

import com.procuone.mit_kdt.dto.InspectionDTO;
import com.procuone.mit_kdt.service.InspectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inspection")
@RequiredArgsConstructor
public class InspectionController {

    @Autowired
    private final InspectionService inspectionService;

    @GetMapping("/load")
    public List<InspectionDTO> loadAllInspections() {
        // Inspection 데이터를 전체 불러오기
        List<InspectionDTO> l =
        inspectionService.getAllInspections();
        for (InspectionDTO dto : l) {
            System.out.println(dto.toString());
        }
        return l;
    }

    @PostMapping("/save")
    public ResponseEntity<String> saveInspection(@RequestBody InspectionDTO inspectionDTO) {
        System.out.println("전송된 DTO: " + inspectionDTO);
        inspectionService.processInspection(inspectionDTO); // 검수 처리 서비스 호출
        return ResponseEntity.ok("검수 데이터가 저장되었습니다.");
    }

    @GetMapping("/invoice/{inspectionId}")
    public String viewInvoice(@PathVariable String inspectionId) {
        // 특정 Inspection 데이터를 기반으로 거래명세서 데이터를 조회하여 HTML 렌더링.
        return inspectionService.generateInvoice(inspectionId);
    }
}