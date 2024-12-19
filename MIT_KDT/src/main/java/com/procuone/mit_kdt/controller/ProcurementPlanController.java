package com.procuone.mit_kdt.controller;

import com.procuone.mit_kdt.dto.CompanyDTO;
import com.procuone.mit_kdt.dto.ProcumentPlanDTO;
import com.procuone.mit_kdt.dto.ProductionPlanDTO;
import com.procuone.mit_kdt.dto.PurchaseOrderDTO;
import com.procuone.mit_kdt.entity.ProcurementPlan;
import com.procuone.mit_kdt.repository.ProcurementPlanRepository;
import com.procuone.mit_kdt.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

        return "procurement/procurementPlanRegister";
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

        return "procurement/procurementPlanRegister"; // 검색 결과 페이지
    }

    @PostMapping("/register")
    public String procurementInput(@ModelAttribute ProcumentPlanDTO procumentPlanDTO,HttpSession session) {

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
        return "redirect:/procurementPlan/register"; // GET 메서드 호출로 리다이렉트
    }

    @GetMapping("/procurementPlanView")
    public String procurementPlanView(
            Model model,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "page", defaultValue = "0") int page) {
        int pageSize = 10;
        Pageable pageable = PageRequest.of(page, pageSize);

        Page<ProcurementPlan> procurementPlans;
        if (search != null && !search.isEmpty()) {
            procurementPlans = ProcurementPlanRepository.findByProductNameContainingOrProductCodeContaining(search, search, pageable);
        } else {
            procurementPlans = ProcurementPlanRepository.findAll(pageable);
        }
        model.addAttribute("procurementPlanList", procurementPlans.getContent());
        model.addAttribute("search", search);
        model.addAttribute("currentPage", procurementPlans.getNumber());
        model.addAttribute("totalPages", procurementPlans.getTotalPages());
        model.addAttribute("totalItems", procurementPlans.getTotalElements());

        return "procurement/procurementPlanView";
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

        return "supplier/companyProcumentPlanView";
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


//    @GetMapping("/viewSearch")
//    public String searchProcurementPlans(@RequestParam(required = false) String productName,
//                                         @RequestParam(required = false) String startDate,
//                                         @RequestParam(required = false) String endDate,
//                                         @RequestParam(required = false) Long quantity,
//                                         Model model){
//
//        // 문자열로 입력받은 날짜를 LocalDate로 변환
//        LocalDate start = (startDate != null && !startDate.isEmpty()) ? LocalDate.parse(startDate) : null;
//        LocalDate end = (endDate != null && !endDate.isEmpty()) ? LocalDate.parse(endDate) : null;
//
//        // 검색 조건을 Service에 전달하여 결과를 가져옵니다.
//        List<ProcumentPlanDTO> result = procurementPlanService.searchProcurementPlans(productName, startDate, endDate, quantity);
//
//        // 결과를 모델에 추가하여 뷰에서 사용할 수 있도록 합니다.
//        model.addAttribute("result", result);
//
//        return "purchaseOrder/procurementPlanView";  // 검색 결과 페이지로 이동
//    }
}


