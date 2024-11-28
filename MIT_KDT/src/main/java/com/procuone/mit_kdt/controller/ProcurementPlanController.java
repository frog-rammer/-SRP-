package com.procuone.mit_kdt.controller;

import com.procuone.mit_kdt.dto.ProcumentPlanDTO;
import com.procuone.mit_kdt.dto.ProductionPlanDTO;
import com.procuone.mit_kdt.entity.ProcurementPlan;
import com.procuone.mit_kdt.repository.ProcurementPlanRepository;
import com.procuone.mit_kdt.service.ProcurementPlanService;
import com.procuone.mit_kdt.service.ProductionPlanService;
import com.procuone.mit_kdt.service.PurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/procumentPlan")
public class ProcurementPlanController {
    @Autowired
    ProductionPlanService productionPlanService;
    @Autowired
    ProcurementPlanService procurementPlanService;
    @Autowired
    PurchaseOrderService purchaseOrderService;
    @Autowired
    private ProcurementPlanRepository ProcurementPlanRepository;

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

    @PostMapping("/register")
    public String procurementInput(@ModelAttribute ProcumentPlanDTO procumentPlanDTO) {

        System.out.println("===================== DTO 출력 =================" + procumentPlanDTO.toString());

        //1. 조달계획 테이블에 넣기
        procumentPlanDTO = procurementPlanService.registerProcurementPlan(procumentPlanDTO);
        //2. 발주서 자동생성
        purchaseOrderService.registerPurchaseOrder(procumentPlanDTO);
        // 3. 저장된 조달 계획 리스트를 다시 로드하여 View에 전달
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

        return "purchaseOrder/procurementPlanView";
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

//    // 수정 페이지로 이동
//    @GetMapping("/procurementPlan/edit/{procurementPlanCode}")
//    public String editProcurementPlan(@PathVariable String procurementPlanCode, Model model) {
//        ProcurementPlan procurementPlan = procurementPlanService.getProcurementPlanByCode(procurementPlanCode);
//        model.addAttribute("procurementPlan", procurementPlan);
//        return "editProcurementPlan"; // 수정 폼 페이지로 이동
//    }

//    @GetMapping("/search")
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


