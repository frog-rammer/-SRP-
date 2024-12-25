package com.procuone.mit_kdt.controller;

import com.procuone.mit_kdt.dto.CompanyDTO;
import com.procuone.mit_kdt.dto.ProcumentPlanDTO;
import com.procuone.mit_kdt.dto.ProductionPlanDTO;
import com.procuone.mit_kdt.dto.PurchaseOrderDTO;
import com.procuone.mit_kdt.entity.ProcurementPlan;
import com.procuone.mit_kdt.repository.ProcurementPlanRepository;
import com.procuone.mit_kdt.service.*;
import jakarta.servlet.http.HttpSession;
import org.apache.coyote.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/procurementPlan")
public class ProcurementPlanController {
    @Autowired
    ProductionPlanService productionPlanService;
    @Autowired
    ProcurementPlanService procurementPlanService;
    @Autowired
    PurchaseOrderService purchaseOrderService;
    @Autowired
    private ProcurementPlanRepository ProcurementPlanRepository;
    @Autowired
    MaterialIssueService  materialIssueService;
    @Autowired
    CompanyService companyService;
    @Autowired
    private ProcurementPlanRepository procurementPlanRepository;

    @GetMapping("/register")
    public String register(Model model,
                           @RequestParam(value = "page", defaultValue = "0") int page,
                           @RequestParam(value = "size", defaultValue = "4") int size,
                           @RequestParam(required = false) String PName,
                           @RequestParam(required = false) String startDate,
                           @RequestParam(required = false) String endDate,
                           Pageable pageable) {
        // 페이지네이션된 생산계획 모델 전달
        pageable = PageRequest.of(page, size,Sort.by("productPlanCode").descending());

        // 날짜 파싱
        LocalDate start = (startDate != null && !startDate.isEmpty()) ? LocalDate.parse(startDate) : null;
        LocalDate end = (endDate != null && !endDate.isEmpty()) ? LocalDate.parse(endDate) : null;

        // 검색 결과 가져오기
        Page<ProductionPlanDTO> searchResults = productionPlanService.searchProductionPlans(PName, start, end, pageable);

        // 조달 필요 수량 계산
        List<ProductionPlanDTO> productionPlanList = searchResults.getContent().stream().map(plan -> {
            long requiredQuantity = procurementPlanService.getRequiredProcurementQuantity(plan.getProductPlanCode());
            plan.setRequiredProcurementQuantity(requiredQuantity); // 조달 필요 수량 설정
            return plan;
        }).collect(Collectors.toList());

        // 페이징 정보 추가
        int totalPages = searchResults.getTotalPages();
        int currentPage = searchResults.getNumber();

        // 페이지 범위 계산 (최대 5개)
        int startPage = Math.max(1, currentPage - 2);
        int endPage = Math.min(totalPages, currentPage + 3);

        // 모델에 데이터 추가
        model.addAttribute("productionPlanList", productionPlanList);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("PName", PName); // 검색 조건 유지
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("procurementPlanDTO", new ProcumentPlanDTO());

        return "procurement/procurementPlanRegister";
    }

    @GetMapping("/search")
    public String searchProductionPlans(
            @RequestParam(required = false) String PName,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "4") int size,
            Model model,
            Pageable pageable) {

        // 날짜 파싱 및 기본값 설정
        LocalDate start = (startDate != null && !startDate.isEmpty()) ? LocalDate.parse(startDate) : null;
        LocalDate end = (endDate != null && !endDate.isEmpty()) ? LocalDate.parse(endDate) : null;

        // 검색 결과 가져오기
        Page<ProductionPlanDTO> searchResults = productionPlanService.searchProductionPlans(PName, start, end, pageable);

        // 각 생산 계획에 대해 조달 필요 수량 계산
        List<ProductionPlanDTO> productionPlanList = searchResults.getContent().stream().map(plan -> {
            long requiredQuantity = procurementPlanService.getRequiredProcurementQuantity(plan.getProductPlanCode());
            plan.setRequiredProcurementQuantity(requiredQuantity); // 조달 필요 수량 설정
            return plan;
        }).collect(Collectors.toList());

        // 페이징 정보 추가
        int totalPages = searchResults.getTotalPages();
        int currentPage = searchResults.getNumber();

        // 페이징 버튼 범위 계산 (최대 5개)
        int startPage = Math.max(1, currentPage - 2);        // 최소 페이지
        int endPage = Math.min(totalPages, currentPage + 3); // 최대 페이지

        // 모델에 데이터 추가
        model.addAttribute("productionPlanList", productionPlanList);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("procurementPlanDTO", new ProcumentPlanDTO());

        return "procurement/procurementPlanRegister"; // 검색 결과 페이지
    }

    @PostMapping("/register")
    public String procurementInput(    @ModelAttribute ProcumentPlanDTO procumentPlanDTO,
                                       HttpSession session,
                                       @RequestParam(required = false) String PName,
                                       @RequestParam(required = false) String startDate,
                                       @RequestParam(required = false) String endDate,
                                       @RequestParam(value = "page", defaultValue = "0") int page,
                                       @RequestParam(value = "size", defaultValue = "4") int size) {

        System.out.println("===================== DTO 출력 =================" + procumentPlanDTO.toString());
        // 세션에서 사용자 이름 가져오기
        String username = (String) session.getAttribute("username");

        System.out.println("===================== memberName 출력 =================" + username );


        //1. 조달계획 테이블에 넣기
        procumentPlanDTO = procurementPlanService.registerProcurementPlan(procumentPlanDTO);
        //2. 발주서 자동생성
        purchaseOrderService.registerPurchaseOrder(procumentPlanDTO,username);
        //3.상태를 "대기" 상태로 출고요청에 자동생성하기
        materialIssueService.createAndSaveShipmentsFromProcurementPlan(procumentPlanDTO,username); // Shipment 생성
        // 4. 저장된 조달 계획 리스트를 다시 로드하여 View에 전달
        return String.format("redirect:/procurementPlan/register?productName=%s&startDate=%s&endDate=%s&page=%d&size=%d",
                PName != null ? PName : "",
                startDate != null ? startDate : "",
                endDate != null ? endDate : "",
                page,
                size);
        // GET 메서드 호출로 리다이렉트
    }

    @GetMapping("/procurementPlanView")
    public String procurementPlanView(
            Model model,
            @RequestParam(value = "productName", required = false) String productName,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(value = "page", defaultValue = "0") int page) {

        int pageSize = 8; // 한 페이지에 표시할 항목 수
        int paginationSize = 5; // 한 번에 표시할 페이지네이션 수
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "procurementPlanCode"));

        Page<ProcurementPlan> procurementPlans;

        // 검색 조건 확인
        if ((productName != null && !productName.isEmpty()) || startDate != null || endDate != null) {
            procurementPlans = procurementPlanService.searchProcurementPlans(null, productName, startDate, endDate, pageable);
        } else {
            procurementPlans = procurementPlanService.findAll(pageable);
        }

        // 페이지네이션 범위 계산
        int currentPage = procurementPlans.getNumber();
        int totalPages = procurementPlans.getTotalPages();

        int startPage = Math.max(0, currentPage - paginationSize / 2);
        int endPage = Math.min(totalPages, startPage + paginationSize);

        // 모델 속성 설정
        model.addAttribute("procurementPlanList", procurementPlans.getContent());
        model.addAttribute("productName", productName);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "procurement/procurementPlanView";
    }

    @GetMapping("/comProcurementPlanView")
    public String comProcurementPlanView(
            Model model,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(value = "page", defaultValue = "0") int page,
            HttpSession session) {

        int pageSize = 10; // 한 페이지에 표시할 항목 수
        int paginationSize = 5; // 페이지네이션에서 표시할 최대 페이지 수

        // 세션에서 비즈니스 ID 가져오기
        String businessId = (String) session.getAttribute("businessId");
        if (businessId == null) {
            session.invalidate();
            return "redirect:/login";
        }

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "procurementPlanCode"));

        // 발주서를 통해 조달계획 코드를 가져오기
        List<PurchaseOrderDTO> purchaseOrders = purchaseOrderService.getCompletedOrdersBybusinessId(businessId);
        List<String> procurementPlanCodes = purchaseOrders.stream()
                .map(PurchaseOrderDTO::getProcurementPlanCode)
                .distinct()
                .collect(Collectors.toList());

        if (procurementPlanCodes.isEmpty()) {
            // 관련 발주서가 없는 경우 빈 페이지 반환
            model.addAttribute("procurementPlanList", List.of());
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
            model.addAttribute("startPage", 0);
            model.addAttribute("endPage", 0);
            model.addAttribute("search", search);
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);
            return "supplier/companyProcumentPlanView";
        }

        Page<ProcurementPlan> comProcurementPlans;

        // 검색 조건 처리
        if ((search != null && !search.isEmpty()) || startDate != null || endDate != null) {
            comProcurementPlans = procurementPlanService.searchProcurementPlans(search, null, startDate, endDate, pageable);
            // 검색 결과를 발주서에서 가져온 조달계획 코드로 필터링
            List<ProcurementPlan> filteredPlans = comProcurementPlans.getContent().stream()
                    .filter(plan -> procurementPlanCodes.contains(plan.getProcurementPlanCode()))
                    .collect(Collectors.toList());
            comProcurementPlans = new PageImpl<>(filteredPlans, pageable, filteredPlans.size());
        } else {
            // 발주서에서 가져온 조달계획 코드로만 필터링
            comProcurementPlans = procurementPlanRepository.findByProcurementPlanCodeIn(procurementPlanCodes, pageable);
        }

        // 페이지네이션 범위 계산
        int currentPage = comProcurementPlans.getNumber();
        int totalPages = comProcurementPlans.getTotalPages();
        int startPage = Math.max(0, currentPage - paginationSize / 2);
        int endPage = Math.min(totalPages, startPage + paginationSize);

        // 모델에 데이터 추가
        CompanyDTO companyDTO = companyService.getCompanyDetails(businessId);
        model.addAttribute("companyDTO", companyDTO);
        model.addAttribute("procurementPlanList", comProcurementPlans.getContent());
        model.addAttribute("search", search);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "supplier/companyProcumentPlanView";
    }


    @PostMapping("/procurementDelete")
    @ResponseBody
    public ResponseEntity<String> deleteProcurementPlan(@RequestParam("procurementPlanCode") String procurementPlanCode) {
        try {
            Long isDeleted = procurementPlanService.deleteProcurementPlanByCode(procurementPlanCode);
            switch (isDeleted.intValue()) {
                case 1:
                    return ResponseEntity.ok("삭제가 완료되었습니다.");
                case 2:
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 조달계획을 찾을 수 없습니다.");
                case 3:
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("발주완료 상태의 조달계획은 삭제할 수 없습니다.");
                default:
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("알 수 없는 오류가 발생했습니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }


    // 수정 폼을 띄울 때
    @GetMapping("/procurementPlan/edit/{procurementPlanCode}")
    public String showEditForm(@PathVariable String procurementPlanCode, Model model) {
        // 서비스에서 해당 코드에 대한 조달계획 정보를 가져옵니다.
        ProcurementPlan procurementPlan = procurementPlanService.getProcurementPlanByCode(procurementPlanCode);
        model.addAttribute("procurementPlan", procurementPlan);
        return "editProcurementPlan"; // 'procurementPlanEdit.html' 화면을 렌더링
    }

    // 수정 완료 후 처리
    @PostMapping("/procurementPlan/update")
    public String updateProcurementPlan(@ModelAttribute ProcurementPlan procurementPlan) {
        // 서비스에서 해당 정보를 업데이트합니다.
        procurementPlanService.updateProcurementPlan(procurementPlan);
        return "redirect:/procurementPlan/list"; // 수정 후 목록 페이지로 리디렉션
    }


}


