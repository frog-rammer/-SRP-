package com.procuone.mit_kdt.controller;

import com.procuone.mit_kdt.dto.InventoryDTO;
import com.procuone.mit_kdt.dto.InventoryTransactionDTO;
import com.procuone.mit_kdt.dto.ItemDTOs.ItemDTO;
import com.procuone.mit_kdt.dto.ProductionPlanDTO;
import com.procuone.mit_kdt.dto.ShipmentDTO;
import com.procuone.mit_kdt.entity.BOM.BOMRelationship;
import com.procuone.mit_kdt.entity.ProductionPlan;
import com.procuone.mit_kdt.repository.BOMRelationshipRepository;
import com.procuone.mit_kdt.repository.ProductionPlanRepository;
import com.procuone.mit_kdt.service.*;
import com.procuone.mit_kdt.service.impl.InventoryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
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

    @Autowired
    private ProductionPlanRepository productionPlanRepository;
    @Autowired
    private InventoryService inventoryService;

    @GetMapping("/getProductionPlans")
    public ResponseEntity<?> getProductionPlans(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "") String keyword
    ) {
        Page<ProductionPlan> plans = productionPlanRepository.findProductionPlans(keyword, PageRequest.of(page, size));
        return ResponseEntity.ok(plans);
    }
    @GetMapping("/getShipments")
    @ResponseBody
    public Map<String, Object> getShipments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "대기") String status
    ) {
        Page<ShipmentDTO> shipments = materialIssueService.getShipmentsByStatus(status, PageRequest.of(page, size));
        Map<String, Object> response = new HashMap<>();
        response.put("shipments", shipments.getContent());
        response.put("currentPage", shipments.getNumber());
        response.put("totalPages", shipments.getTotalPages());
        return response;
    }


    @GetMapping("/stock")
    public String stock(Model model, Pageable pageable,
                        @RequestParam(defaultValue = "0") int ipage,
                        @RequestParam(defaultValue = "8") int isize,
                        @RequestParam(defaultValue = "0") int opage,
                        @RequestParam(defaultValue = "8") int osize) {


        // 1. 완제품 리스트 가져오기
        List<ItemDTO> finishedProducts = itemService.getItemsByCategoryId(1L);
//        System.out.println("Step 1 - Finished Products: " + finishedProducts);
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
//            System.out.println("Step 2 - Product: " + product.getProductCode() + ", Child Items: " + childItems);
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
//                System.out.println("Step 3 - Monthly Stats for " + childItem.getProductCode() + " - Inbound: " + inboundStats + ", Outbound: " + outboundStats);
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
//                System.out.println("Step 4 - Weekly Stats for " + childItem.getProductCode() + " - Inbound: " + inboundStats + ", Outbound: " + outboundStats);
            }
            weeklyStats.put(product.getProductCode(), productStats);
        }
        model.addAttribute("weeklyStats", weeklyStats);

        // 5. 주별/월별 금액 계산
        Map<String, Map<String, Map<String, Double>>> costStats = new HashMap<>();
        for (ItemDTO product : finishedProducts) {
            List<ItemDTO> childItems = finishedProductChildItems.get(product.getProductCode());
            Map<String, Map<String, Double>> productCostStats = new HashMap<>();

            for (ItemDTO childItem : childItems) {
                // 월별 금액 통계
                Map<String, Double> monthlyCostStats = inventoryTransactionService
                        .getMonthlyCostStatsByProductCode(childItem.getProductCode());

                // 주별 금액 통계
                Map<String, Double> weeklyCostStats = inventoryTransactionService
                        .getWeeklyCostStatsByProductCode(childItem.getProductCode());

                // 각 데이터 저장
                Map<String, Double> childCostStats = new HashMap<>();
                childCostStats.putAll(monthlyCostStats);
                childCostStats.putAll(weeklyCostStats);

                productCostStats.put(childItem.getProductCode(), childCostStats);

//                System.out.println("Step 5 - Cost Stats for " + childItem.getProductCode() + " - Monthly: " + monthlyCostStats + ", Weekly: " + weeklyCostStats);
            }
            costStats.put(product.getProductCode(), productCostStats);
        }

        model.addAttribute("costStats", costStats);


        // 6. 월별 목표 금액 설정
        Map<Integer, Long> monthlyInboundValues = new HashMap<>();
        Map<Integer, String> monthlyStatus = new HashMap<>();
        long monthlyTarget = 5000000000L;
        long totalInbound = 0L;
        for (int month = 1; month <= 12; month++) {
            String monthKey = getMonthName(month); // 월 숫자를 영문 월 이름으로 변환

            for (ItemDTO product : finishedProducts) {
                List<ItemDTO> childItems = finishedProductChildItems.get(product.getProductCode());

                for (ItemDTO childItem : childItems) {
                    Map<String, Long> inboundStats = inventoryTransactionService.getMonthlyInboundPriceStatsByProductCode(childItem.getProductCode());
                    // 영문 월 이름 키로 데이터 가져오기
                    long inboundForMonth = inboundStats.getOrDefault(monthKey, 0L);
//                    System.out.println("ChildItem: " + childItem.getProductCode() + ", Month: " + monthKey + ", Inbound: " + inboundForMonth);

                    totalInbound += inboundForMonth;
                }
            }

            monthlyInboundValues.put(month, totalInbound);
            monthlyStatus.put(month, totalInbound >= monthlyTarget ? "달성" : "미달");

//            System.out.println("Step 6 - Month: " + monthKey + ", Total Inbound: " + totalInbound + ", Status: " + monthlyStatus.get(month));
        }
//        System.out.println("=============ㄴㅁ어ㅏㅁ로ㅑ매노려ㅑㅐㅁ노야ㅐㅁ노야ㅐ=======" + monthlyInboundValues);
        model.addAttribute("monthlyTarget", monthlyTarget);
        model.addAttribute("monthlyInboundValues", monthlyInboundValues);
        model.addAttribute("monthlyStatus", monthlyStatus);

        // 7. 입고 상태 트랜잭션 가져오기
        pageable = PageRequest.of(ipage, isize);
        Page<InventoryTransactionDTO> inboundTransactions = inventoryTransactionService
                .getPagedTransactionsByStatus("입고", pageable);
//        System.out.println("Step 7 - Inbound Transactions: " + inboundTransactions.getContent());

        pageable = PageRequest.of(opage, osize);
        // 8. 출고 상태 트랜잭션 가져오기
        Page<InventoryTransactionDTO> outboundTransactions = inventoryTransactionService
                .getPagedTransactionsByStatus("출고", pageable);
//        System.out.println("Step 8 - Outbound Transactions: " + outboundTransactions.getContent());

        model.addAttribute("inboundTransactions", inboundTransactions.getContent());
        model.addAttribute("outboundTransactions", outboundTransactions.getContent());

        // 9. 페이징 정보 추가
        // 페이지네이션 범위 계산
        int paginationSize = 5; // 한 번에 표시할 페이지네이션 수

        // 출고 페이지네이션 계산
        int currentOutboundPage = outboundTransactions.getNumber();
        int totalOutboundPages = outboundTransactions.getTotalPages();
        int startOutboundPage = Math.max(0, currentOutboundPage - paginationSize / 2);
        int endOutboundPage = Math.min(totalOutboundPages, startOutboundPage + paginationSize);

        // 입고 페이지네이션 계산
        int currentInboundPage = inboundTransactions.getNumber();
        int totalInboundPages = inboundTransactions.getTotalPages();
        int startInboundPage = Math.max(0, currentInboundPage - paginationSize / 2);
        int endInboundPage = Math.min(totalInboundPages, startInboundPage + paginationSize);

        // 모델에 페이지네이션 정보 추가
        model.addAttribute("currentOutboundPage", currentOutboundPage);
        model.addAttribute("totalOutboundPages", totalOutboundPages);
        model.addAttribute("startOutboundPage", startOutboundPage);
        model.addAttribute("endOutboundPage", endOutboundPage);

        model.addAttribute("currentInboundPage", currentInboundPage);
        model.addAttribute("totalInboundPages", totalInboundPages);
        model.addAttribute("startInboundPage", startInboundPage);
        model.addAttribute("endInboundPage", endInboundPage);


        return "material/stock";
    }


    private void addPaginationAttributes(Model model, Page<?> inboundTransactions, Page<?> outboundTransactions) {
        int paginationSize = 5;

        // 출고 페이지네이션
        int currentOutboundPage = outboundTransactions.getNumber();
        int totalOutboundPages = outboundTransactions.getTotalPages();
        model.addAttribute("startOutboundPage", Math.max(0, currentOutboundPage - paginationSize / 2));
        model.addAttribute("endOutboundPage", Math.min(totalOutboundPages, currentOutboundPage + paginationSize));

        // 입고 페이지네이션
        int currentInboundPage = inboundTransactions.getNumber();
        int totalInboundPages = inboundTransactions.getTotalPages();
        model.addAttribute("startInboundPage", Math.max(0, currentInboundPage - paginationSize / 2));
        model.addAttribute("endInboundPage", Math.min(totalInboundPages, currentInboundPage + paginationSize));
    }


    // 숫자 월을 영문 월 이름으로 변환하는 메서드
    private String getMonthName(int month) {
        String[] monthNames = {
                "JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE",
                "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"
        };
        return monthNames[month - 1];
    }

    @GetMapping("/stockOut")
    public String stockOut(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "0") int spage,
            @RequestParam(defaultValue = "10") int ssize,
            @RequestParam(defaultValue = "0") int dpage,
            @RequestParam(defaultValue = "10") int dsize,
            @RequestParam(defaultValue = "0") int fpage,
            @RequestParam(defaultValue = "10") int fsize
    ) {
        int maxVisiblePages = 5; // 한 번에 보여줄 최대 페이지 버튼 수

        // 생산 계획 페이지 (정렬 없음)
        Pageable productionPageable = PageRequest.of(page, size);
        Page<ProductionPlanDTO> productionPlanPage = productionPlanService.getAllPlans(productionPageable);
        int productionPlanStartPage = Math.max(0, page - (maxVisiblePages / 2));
        int productionPlanEndPage = Math.min(productionPlanPage.getTotalPages(), productionPlanStartPage + maxVisiblePages);

        model.addAttribute("productionPlanList", productionPlanPage.getContent());
        model.addAttribute("currentProductionPlanPage", productionPlanPage.getNumber());
        model.addAttribute("totalProductionPlanPages", productionPlanPage.getTotalPages());
        model.addAttribute("productionPlanStartPage", productionPlanStartPage);
        model.addAttribute("productionPlanEndPage", productionPlanEndPage);

        // 출고 목록 (대기 상태) - 출고번호 기준 내림차순
        Pageable waitingPageable = PageRequest.of(spage, ssize, Sort.by("shipmentId").descending());
        Page<ShipmentDTO> waitingShipments = materialIssueService.getShipmentsByStatus("대기", waitingPageable);
        for(ShipmentDTO shipment : waitingShipments.getContent()) {
            Long ItemId =itemService.getItemIdByProductCode(shipment.getProductCode());
            InventoryDTO in = inventoryService.getInventoryByItemId(ItemId);
            shipment.setCurrentQuantity(in.getCurrentQuantity().longValue());
        }

        int waitingStartPage = Math.max(0, spage - (maxVisiblePages / 2));
        int waitingEndPage = Math.min(waitingShipments.getTotalPages(), waitingStartPage + maxVisiblePages);

        model.addAttribute("waitingShipmentList", waitingShipments.getContent());
        model.addAttribute("currentWaitingPage", waitingShipments.getNumber());
        model.addAttribute("totalWaitingPages", waitingShipments.getTotalPages());
        model.addAttribute("waitingStartPage", waitingStartPage);
        model.addAttribute("waitingEndPage", waitingEndPage);

        // 출고 현황 (진행중, 재고부족 상태) - 출고번호 기준 내림차순
        Pageable ongoingPageable = PageRequest.of(dpage, dsize, Sort.by("shipmentId").descending());
        Page<ShipmentDTO> ongoingOrShortageShipments = materialIssueService.getShipmentsByStatuses(
                List.of("진행중", "재고부족"), ongoingPageable);
        int ongoingStartPage = Math.max(0, dpage - (maxVisiblePages / 2));
        int ongoingEndPage = Math.min(ongoingOrShortageShipments.getTotalPages(), ongoingStartPage + maxVisiblePages);

        model.addAttribute("ongoingOrShortageShipmentList", ongoingOrShortageShipments.getContent());
        model.addAttribute("currentOngoingPage", ongoingOrShortageShipments.getNumber());
        model.addAttribute("totalOngoingPages", ongoingOrShortageShipments.getTotalPages());
        model.addAttribute("ongoingStartPage", ongoingStartPage);
        model.addAttribute("ongoingEndPage", ongoingEndPage);

        // 출고 내역 (출고완료 상태) - 출고번호 기준 내림차순
        Pageable completedPageable = PageRequest.of(fpage, fsize, Sort.by("shipmentId").descending());
        Page<ShipmentDTO> completedShipments = materialIssueService.getShipmentsByStatus("출고완료", completedPageable);
        int completedStartPage = Math.max(0, fpage - (maxVisiblePages / 2));
        int completedEndPage = Math.min(completedShipments.getTotalPages(), completedStartPage + maxVisiblePages);

        model.addAttribute("completedShipmentList", completedShipments.getContent());
        model.addAttribute("currentCompletedPage", completedShipments.getNumber());
        model.addAttribute("totalCompletedPages", completedShipments.getTotalPages());
        model.addAttribute("completedStartPage", completedStartPage);
        model.addAttribute("completedEndPage", completedEndPage);

        return "material/stockOut";
    }


    @GetMapping("/stockOutOnProductionPart")
    public String stockOutOnProductionPart(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "0") int spage,
            @RequestParam(defaultValue = "10") int ssize,
            Pageable pageable
    ) {
        // 페이지 정보 생성
        pageable = PageRequest.of(page, size,Sort.by("shipmentId").descending());
        materialIssueService.updateCurrentQuantity();

        // 진행중 상태의 데이터 가져오기
        Page<ShipmentDTO> ongoingOrShortageShipments = materialIssueService.getShipmentsByStatus("진행중", pageable);
        int ongoingStartPage = Math.max(0, ongoingOrShortageShipments.getNumber() - 2);
        int ongoingEndPage = Math.min(ongoingStartPage + 5, ongoingOrShortageShipments.getTotalPages());

        // 출고완료 상태의 데이터 가져오기
        pageable = PageRequest.of(spage, ssize,Sort.by("shipmentId").descending());
        Page<ShipmentDTO> completedShipments = materialIssueService.getShipmentsByStatus("출고완료", pageable);
        int completedStartPage = Math.max(0, completedShipments.getNumber() - 2);
        int completedEndPage = Math.min(completedStartPage + 5, completedShipments.getTotalPages());

        // 진행중 상태 데이터
        model.addAttribute("ongoingOrShortageShipmentList", ongoingOrShortageShipments.getContent());
        model.addAttribute("currentOngoingPage", ongoingOrShortageShipments.getNumber());
        model.addAttribute("totalOngoingPages", ongoingOrShortageShipments.getTotalPages());
        model.addAttribute("totalOngoingItems", ongoingOrShortageShipments.getTotalElements());
        model.addAttribute("ongoingStartPage", ongoingStartPage);
        model.addAttribute("ongoingEndPage", ongoingEndPage);

        // 출고완료 상태 데이터
        model.addAttribute("completedShipmentList", completedShipments.getContent());
        model.addAttribute("currentCompletedPage", completedShipments.getNumber());
        model.addAttribute("totalCompletedPages", completedShipments.getTotalPages());
        model.addAttribute("totalCompletedItems", completedShipments.getTotalElements());
        model.addAttribute("completedStartPage", completedStartPage);
        model.addAttribute("completedEndPage", completedEndPage);

        return "production/shipmentConfirmation";
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
