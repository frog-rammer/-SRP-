package com.procuone.mit_kdt.controller;

import com.procuone.mit_kdt.dto.CompanyDTO;
import com.procuone.mit_kdt.dto.ItemDTOs.PurchaseBOMDTO;
import com.procuone.mit_kdt.dto.ProcumentPlanDTO;
import com.procuone.mit_kdt.dto.ProductionPlanDTO;
import com.procuone.mit_kdt.dto.PurchaseOrderDTO;
import com.procuone.mit_kdt.entity.BOM.PurchaseBOM;
import com.procuone.mit_kdt.entity.ProcurementPlan;
import com.procuone.mit_kdt.repository.ProcurementPlanRepository;
import com.procuone.mit_kdt.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @GetMapping("/register")
    public String register(Model model, Pageable pageable) {
        // 페이지 네이션 된 생산계획 모델로 전달까지
        Page<ProductionPlanDTO> productionPlanPage = productionPlanService.getAllPlans(pageable);

        // 각 생산 계획에 대해 조달 필요 수량 계산
        List<ProductionPlanDTO> productionPlanList = productionPlanPage.getContent().stream().map(plan -> {
            long requiredQuantity = procurementPlanService.getRequiredProcurementQuantity(plan.getProductPlanCode());
            plan.setRequiredProcurementQuantity(requiredQuantity); // 새로운 필드 설정
            return plan;
        }).collect(Collectors.toList());

        int totalPages = productionPlanPage.getTotalPages(); // 총 페이지 수
        int currentPage = productionPlanPage.getNumber();    // 현재 페이지 번호 (0부터 시작)

        // 페이징 버튼 범위 계산 (최대 5개)
        int startPage = Math.max(1, currentPage - 2);        // 최소 페이지
        int endPage = Math.min(totalPages, currentPage + 3); // 최대 페이지

        // 모델에 추가
        model.addAttribute("productionPlanList", productionPlanList);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("procurementPlanDTO", new ProcumentPlanDTO());

        List<ProcurementPlan> procurementPlanList = procurementPlanService.getAllProcurementPlans();//추가

        return "procurementPlan/procurementPlanRegister";
    }
    @GetMapping("/search")
    public String searchProductionPlans(
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Model model,
            Pageable pageable) {

        // 날짜 파싱 및 기본값 설정
        LocalDate start = (startDate != null && !startDate.isEmpty()) ? LocalDate.parse(startDate) : null;
        LocalDate end = (endDate != null && !endDate.isEmpty()) ? LocalDate.parse(endDate) : null;

        // 검색 결과 가져오기
        Page<ProductionPlanDTO> searchResults = productionPlanService.searchProductionPlans(productName, start, end, pageable);

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

        return "procurementPlan/procurementPlanRegister"; // 검색 결과 페이지
    }

    @PostMapping("/register")
    public String procurementInput(@ModelAttribute ProcumentPlanDTO procumentPlanDTO) {

        System.out.println("===================== DTO 출력 =================" + procumentPlanDTO.toString());

        //1. 조달계획 테이블에 넣기
        procumentPlanDTO = procurementPlanService.registerProcurementPlan(procumentPlanDTO);
        //2. 발주서 자동생성
        purchaseOrderService.registerPurchaseOrder(procumentPlanDTO);

        //3.상태를 "대기" 상태로 출고요청에 자동생성하기
        materialIssueService.createAndSaveShipmentsFromProcurementPlan(procumentPlanDTO); // Shipment 생성


        // 4. 저장된 조달 계획 리스트를 다시 로드하여 View에 전달
        return "redirect:/procurementPlan/register"; // GET 메서드 호출로 리다이렉트
    }

    @GetMapping("/procurementPlanView")
    public String procurementPlanView(
            Model model,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "startDate", required = false) String startDateString,
            @RequestParam(value = "endDate", required = false) String endDateString,
            @RequestParam(value = "page", defaultValue = "0") int page) {

        int pageSize = 10;
        Pageable pageable = PageRequest.of(page, pageSize);

        // startDate, endDate를 LocalDate로 변환
        LocalDate startDate = (startDateString != null && !startDateString.isEmpty()) ? LocalDate.parse(startDateString) : null;
        LocalDate endDate = (endDateString != null && !endDateString.isEmpty()) ? LocalDate.parse(endDateString) : null;

        Page<ProcurementPlan> procurementPlans;

        // 검색 조건에 따라 쿼리 수행
        if (search != null && !search.isEmpty()) {
            if (startDate != null && endDate != null) {
                // 검색어, 시작일, 종료일 모두 있을 때
                procurementPlans = ProcurementPlanRepository.findByProductNameContainingAndPlanStartDateGreaterThanEqualAndPlanEndDateLessThanEqual(
                        search, startDate, endDate, pageable);
            } else if (startDate != null) {
                // 검색어와 시작일만 있을 때
                procurementPlans = ProcurementPlanRepository.findByProductNameContainingAndPlanStartDateGreaterThanEqual(
                        search, startDate, pageable);
            } else if (endDate != null) {
                // 검색어와 종료일만 있을 때
                procurementPlans = ProcurementPlanRepository.findByProductNameContainingAndPlanEndDateLessThanEqual(
                        search, endDate, pageable);
            } else {
                // 검색어만 있을 때
                procurementPlans = ProcurementPlanRepository.findByProductNameContainingOrProductCodeContaining(search, search, pageable);
            }
        } else {
            // 검색어가 없을 때
            if (startDate != null && endDate != null) {
                procurementPlans = ProcurementPlanRepository.findByPlanStartDateGreaterThanEqualAndPlanEndDateLessThanEqual(startDate, endDate, pageable);
            } else {
                procurementPlans = ProcurementPlanRepository.findAll(pageable);
            }
        }

        // 뷰에 데이터 추가
        model.addAttribute("procurementPlanList", procurementPlans.getContent());
        model.addAttribute("search", search);
        model.addAttribute("startDate", startDateString); // String 그대로 전달
        model.addAttribute("endDate", endDateString); // String 그대로 전달
        model.addAttribute("currentPage", procurementPlans.getNumber());
        model.addAttribute("totalPages", procurementPlans.getTotalPages());
        model.addAttribute("totalItems", procurementPlans.getTotalElements());

        return "purchaseOrder/procurementPlanView";
    }


    @GetMapping("/comProcurementPlanView")
    public String comProcurementPlanView(
            Model model,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Pageable pageable,
            HttpSession session) {
        String businessId = session.getAttribute("businessId").toString();
        if(businessId == null) {
            session.invalidate();
            return "redirect:/login";
        }

        pageable = PageRequest.of(page, size);

        Page<ProcurementPlan> comProcurementPlans;

        if (search != null && !search.isEmpty()) {
            comProcurementPlans = ProcurementPlanRepository.findByProductNameContainingOrProductCodeContaining(search, search, pageable);
        } else {
            List<PurchaseOrderDTO> pDTO  =purchaseOrderService.getCompletedOrdersBybusinessId(businessId);
            List<String> uniqueProcurementCodes = pDTO.stream()
                    .map(PurchaseOrderDTO::getProcurementPlanCode) // Extract procurement_plan_code
                    .distinct() // Remove duplicates
                    .collect(Collectors.toList());
            comProcurementPlans = ProcurementPlanRepository.findByProcurementPlanCodeIn(uniqueProcurementCodes, pageable);
        }
        CompanyDTO companyDTO= companyService.getCompanyDetails(businessId);
        model.addAttribute("companyDTO", companyDTO);
        model.addAttribute("procurementPlanList", comProcurementPlans.getContent());
        model.addAttribute("search", search);
        model.addAttribute("currentPage", comProcurementPlans.getNumber());
        model.addAttribute("totalPages", comProcurementPlans.getTotalPages());
        model.addAttribute("totalItems", comProcurementPlans.getTotalElements());

        return "companyProcumentPlanView";
    }
}


