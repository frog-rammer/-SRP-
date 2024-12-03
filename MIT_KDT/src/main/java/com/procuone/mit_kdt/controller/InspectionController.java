package com.procuone.mit_kdt.controller;

import com.procuone.mit_kdt.dto.InspectionDTO;
import com.procuone.mit_kdt.entity.DeliveryOrder;
import com.procuone.mit_kdt.service.DeliveryOrderService;
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
    private final DeliveryOrderService deliveryOrderService;
    @Autowired
    private final InspectionService inspectionService;

    // 입고검수 초기 데이터 로드
    @GetMapping("/load")
    public List<DeliveryOrder> loadPendingDeliveryOrders() {
        return deliveryOrderService.findPendingOrders(); // "운송중" 상태로 납품일이 지난 데이터
    }

    // 검수 저장
    @PostMapping("/save")
    public ResponseEntity<String> saveInspection(@RequestBody InspectionDTO inspectionDTO) {
        inspectionService.saveInspection(inspectionDTO);
        return ResponseEntity.ok("검수 데이터가 저장되었습니다.");
    }
}
