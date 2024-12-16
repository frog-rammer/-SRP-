package com.procuone.mit_kdt.controller;

import com.procuone.mit_kdt.dto.InventoryTransactionDTO;
import com.procuone.mit_kdt.dto.ItemDTOs.BOMRelationshipDTO;
import com.procuone.mit_kdt.dto.ItemDTOs.ItemDTO;
import com.procuone.mit_kdt.dto.ProductionPlanDTO;
import com.procuone.mit_kdt.dto.ShipmentDTO;
import com.procuone.mit_kdt.entity.BOM.BOMRelationship;
import com.procuone.mit_kdt.repository.BOMRelationshipRepository;
import com.procuone.mit_kdt.service.InventoryTransactionService;
import com.procuone.mit_kdt.service.ItemService;
import com.procuone.mit_kdt.service.MaterialIssueService;
import com.procuone.mit_kdt.service.ProductionPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/materialIssue")
public class MaterialIssueController {

    @Autowired
    private ProductionPlanService productionPlanService;

    @Autowired
    private MaterialIssueService materialIssueService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private BOMRelationshipRepository relationshipRepository;

    @Autowired
    private InventoryTransactionService inventoryTransactionService;

    @GetMapping("/stock")
    public String stock(Model model, Pageable pageable,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "8") int size) {
        pageable = PageRequest.of(page, size);

        // 1. 완제품 리스트 가져오기
        List<ItemDTO> finishedProducts = itemService.getItemsByCategoryId(1L);
        model.addAttribute("finishedProducts", finishedProducts);

        // 2. 완제품 하위 부품 리스트 가져오기
        Map<String, List<ItemDTO>> finishedProductChildItems = new HashMap<>();
        for (ItemDTO product : finishedProducts) {
            List<BOMRelationship> childItemList = relationshipRepository
                    .findChildItemsByParentProductCode(product.getProductCode());
            List<ItemDTO> childItems = new ArrayList<>();
            for (BOMRelationship relationship : childItemList) {
                Optional<ItemDTO> childItemOpt = itemService.findByProductCode(relationship.getChildItem().getProductCode());
                childItemOpt.ifPresent(childItems::add);
            }
            finishedProductChildItems.put(product.getProductCode(), childItems);
        }
        model.addAttribute("finishedProductChildItems", finishedProductChildItems);

        // 3. 월별 입고/출고량 계산
        Map<String, Map<String, Map<String, Map<String, Long>>>> monthlyStats = new HashMap<>();
        for (ItemDTO product : finishedProducts) {
            List<ItemDTO> childItems = finishedProductChildItems.get(product.getProductCode());
            Map<String, Map<String, Map<String, Long>>> productStats = new HashMap<>();
            for (ItemDTO childItem : childItems) {
                Map<String, Long> inboundStats = inventoryTransactionService.getMonthlyInboundStatsByProductCode(childItem.getProductCode());
                Map<String, Long> outboundStats = inventoryTransactionService.getMonthlyOutboundStatsByProductCode(childItem.getProductCode());
                Map<String, Map<String, Long>> childStats = new HashMap<>();
                childStats.put("inbound", inboundStats);
                childStats.put("outbound", outboundStats);
                productStats.put(childItem.getProductCode(), childStats);
            }
            monthlyStats.put(product.getProductCode(), productStats);
        }
        model.addAttribute("monthlyStats", monthlyStats);

        // 4. 주별 입고/출고량 계산
        Map<String, Map<String, Map<String, Map<String, Long>>>> weeklyStats = new HashMap<>();
        for (ItemDTO product : finishedProducts) {
            List<ItemDTO> childItems = finishedProductChildItems.get(product.getProductCode());
            Map<String, Map<String, Map<String, Long>>> productStats = new HashMap<>();
            for (ItemDTO childItem : childItems) {
                Map<String, Long> inboundStats = inventoryTransactionService.getWeeklyInboundStatsByProductCode(childItem.getProductCode());
                Map<String, Long> outboundStats = inventoryTransactionService.getWeeklyOutboundStatsByProductCode(childItem.getProductCode());
                Map<String, Map<String, Long>> childStats = new HashMap<>();
                childStats.put("inbound", inboundStats);
                childStats.put("outbound", outboundStats);
                productStats.put(childItem.getProductCode(), childStats);
            }
            weeklyStats.put(product.getProductCode(), productStats);
        }
        model.addAttribute("weeklyStats", weeklyStats);

        // 5. 주별/월별 금액 계산
        Map<String, Map<String, Double>> costStats = new HashMap<>();
        for (ItemDTO product : finishedProducts) {
            List<ItemDTO> childItems = finishedProductChildItems.get(product.getProductCode());
            Map<String, Double> productCostStats = new HashMap<>();
            for (ItemDTO childItem : childItems) {
                Double childCostStats = inventoryTransactionService
                        .getCostStatsByProductCode(childItem.getProductCode());
                productCostStats.put(childItem.getProductCode(), childCostStats);
            }
            costStats.put(product.getProductCode(), productCostStats);
        }
        model.addAttribute("costStats", costStats);

        // 6. 월별 목표 금액 설정
        Map<Integer, Long> monthlyInboundValues = new HashMap<>(); // Long으로 변경 (소수점 제거)
        Map<Integer, String> monthlyStatus = new HashMap<>();      // 목표 달성 여부
        long monthlyTarget = 5000000L; // 예: 월별 목표 금액 (5백만 원) -> Long 타입

        for (int month = 1; month <= 12; month++) {
            long totalInbound = 0L; // 초기값도 Long으로 설정

            for (ItemDTO product : finishedProducts) {
                List<ItemDTO> childItems = finishedProductChildItems.get(product.getProductCode());

                for (ItemDTO childItem : childItems) {
                    // 월별 입고 금액 가져오기
                    Map<String, Long> inboundStats = inventoryTransactionService.getMonthlyInboundStatsByProductCode(childItem.getProductCode());
                    totalInbound += inboundStats.getOrDefault(String.valueOf(month), 0L);
                }
            }

            // 월별 입고 금액 저장
            monthlyInboundValues.put(month, totalInbound);

            // 목표 금액 이하 여부 확인
            if (totalInbound <= monthlyTarget) {
                monthlyStatus.put(month, "달성");
            } else {
                monthlyStatus.put(month, "미달");
            }
        }

        // 모델에 추가
        model.addAttribute("monthlyTarget", monthlyTarget);
        model.addAttribute("monthlyInboundValues", monthlyInboundValues);
        model.addAttribute("monthlyStatus", monthlyStatus);
        System.out.println("Monthly Inbound Values: " + monthlyInboundValues);
        System.out.println("Monthly Status: " + monthlyStatus);
        System.out.println("Monthly Target: " + monthlyTarget);

    // 7. 입고 상태 트랜잭션 가져오기
    Page<InventoryTransactionDTO> inboundTransactions = inventoryTransactionService
            .getPagedTransactionsByStatus("입고", pageable);
        model.addAttribute("inboundTransactions", inboundTransactions.getContent());

    // 8. 출고 상태 트랜잭션 가져오기
    Page<InventoryTransactionDTO> outboundTransactions = inventoryTransactionService
            .getPagedTransactionsByStatus("출고", pageable);
        model.addAttribute("outboundTransactions", outboundTransactions.getContent());

    // 9. 페이징 정보 추가
        model.addAttribute("currentInboundPage", inboundTransactions.getNumber());
        model.addAttribute("totalInboundPages", inboundTransactions.getTotalPages());
        model.addAttribute("totalInboundItems", inboundTransactions.getTotalElements());

        model.addAttribute("currentOutboundPage", outboundTransactions.getNumber());
        model.addAttribute("totalOutboundPages", outboundTransactions.getTotalPages());
        model.addAttribute("totalOutboundItems", outboundTransactions.getTotalElements());


        return "materialIssue/stock";
    }




    @GetMapping("/stockOut")
    public String stockOut(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Pageable pageable
    ) {
        // 페이지 정보 생성
        pageable = PageRequest.of(page, size);

        // 생산 계획 페이지 가져오기
        Page<ProductionPlanDTO> productionPlanPage = productionPlanService.getAllPlans(pageable);
        model.addAttribute("productionPlanList", productionPlanPage.getContent()); // 생산 계획 목록
        model.addAttribute("currentProductionPlanPage", productionPlanPage.getNumber());  // 현재 페이지
        model.addAttribute("totalProductionPlanPages", productionPlanPage.getTotalPages());  // 총 페이지 수
        model.addAttribute("totalProductionPlanItems", productionPlanPage.getTotalElements()); // 전체 아이템 수

        // 인벤토리에서 현재 수량 업데이트  (시간 많이 잡아먹을 수 있으니 업데이트 최적화 필요)
        materialIssueService.updateCurrentQuantity();
        // 상태별 데이터 필터링 및 페이지네이션
        Page<ShipmentDTO> waitingShipments = materialIssueService.getShipmentsByStatus("대기", pageable);
        Page<ShipmentDTO> ongoingOrShortageShipments = materialIssueService.getShipmentsByStatuses(
                List.of("진행중", "재고부족"), pageable);
        Page<ShipmentDTO> completedShipments = materialIssueService.getShipmentsByStatus("출고완료", pageable);
        // 출고 목록 (대기 상태)
        model.addAttribute("waitingShipmentList", waitingShipments.getContent());
        model.addAttribute("currentWaitingPage", waitingShipments.getNumber());
        model.addAttribute("totalWaitingPages", waitingShipments.getTotalPages());
        model.addAttribute("totalWaitingItems", waitingShipments.getTotalElements());

        // 출고 현황 (진행중, 재고부족, 출고완료 상태)
        model.addAttribute("ongoingOrShortageShipmentList", ongoingOrShortageShipments.getContent());
        model.addAttribute("currentOngoingPage", ongoingOrShortageShipments.getNumber());
        model.addAttribute("totalOngoingPages", ongoingOrShortageShipments.getTotalPages());
        model.addAttribute("totalOngoingItems", ongoingOrShortageShipments.getTotalElements());

        // 출고 내역 (출고완료 상태)
        model.addAttribute("completedShipmentList", completedShipments.getContent());
        model.addAttribute("currentCompletedPage", completedShipments.getNumber());
        model.addAttribute("totalCompletedPages", completedShipments.getTotalPages());
        model.addAttribute("totalCompletedItems", completedShipments.getTotalElements());

        // 반환할 뷰 이름
        return "materialIssue/stockOut";
    }

    @GetMapping("/stockOutOnProductionPart")
    public String stockOutOnProductionPart(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
            ,Pageable pageable
    ) {
        // 페이지 정보 생성
        pageable = PageRequest.of(page, size);
        // 인벤토리에서 현재 수량 업데이트  (시간 많이 잡아먹을 수 있으니 업데이트 최적화 필요)
        materialIssueService.updateCurrentQuantity();
        // 상태별 데이터 필터링 및 페이지네이션
        Page<ShipmentDTO> ongoingOrShortageShipments = materialIssueService.getShipmentsByStatus("진행중", pageable);
        Page<ShipmentDTO> completedShipments = materialIssueService.getShipmentsByStatus("출고완료", pageable);
        // 출고 학인 (진행중 상태)
        model.addAttribute("ongoingOrShortageShipmentList", ongoingOrShortageShipments.getContent());
        model.addAttribute("currentOngoingPage", ongoingOrShortageShipments.getNumber());
        model.addAttribute("totalOngoingPages", ongoingOrShortageShipments.getTotalPages());
        model.addAttribute("totalOngoingItems", ongoingOrShortageShipments.getTotalElements());
        // 출고 내역 (출고완료 상태)
        model.addAttribute("completedShipmentList", completedShipments.getContent());
        model.addAttribute("currentCompletedPage", completedShipments.getNumber());
        model.addAttribute("totalCompletedPages", completedShipments.getTotalPages());
        model.addAttribute("totalCompletedItems", completedShipments.getTotalElements());
        return "materialIssue/shipmentConfirmation";
    }

    @PostMapping("/confirmReceipt")
    public String confirmReceipt(
            @RequestParam(value = "selectedShipments", required = false) List<String> shipmentIds,
            RedirectAttributes redirectAttributes
    ) {



        // 선택된 출고번호를 처리
        if (shipmentIds != null && !shipmentIds.isEmpty()) {
            materialIssueService.confirmReceipt(shipmentIds); // 수령 확인 처리
            redirectAttributes.addFlashAttribute("message", "선택된 출고 요청이 성공적으로 확인되었습니다.");
        } else {
            redirectAttributes.addFlashAttribute("error", "수령 확인할 출고 요청이 선택되지 않았습니다.");
        }

        // 다시 출고 확인 페이지로 리다이렉트
        return "redirect:/materialIssue/stockOutOnProductionPart";
    }

    @PostMapping("/stockOut")
    @ResponseBody
    public String updateToOngoing(@RequestBody Map<String, List<String>> payload) {
        System.out.println("Payload received: " + payload); // 디버깅용 로그 추가
        // JSON 데이터에서 shipments 배열 추출
        List<String> shipmentIds = payload.get("shipments");

        if (shipmentIds == null || shipmentIds.isEmpty()) {
            return "출고 요청 항목이 없습니다.";
        }
        // 출고 상태를 "진행중"으로 업데이트
        materialIssueService.updateToOngoing(shipmentIds);
        return "선택된 항목의 상태가 '진행중'으로 업데이트되었습니다.";
    }
}
