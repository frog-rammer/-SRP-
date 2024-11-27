package com.procuone.mit_kdt.controller;

import com.procuone.mit_kdt.dto.ProgressInspectionDTO;
import com.procuone.mit_kdt.entity.ProgressInspection;
import com.procuone.mit_kdt.service.ProgressInspectionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/progressInspection")
public class ProgressInspectionController {

    @Autowired
    ProgressInspectionService progressInspectionService;

    @GetMapping("/progressInspectionBoard")
    public String getProgressInspection(Model model,
                                        @RequestParam(defaultValue = "1") int page, // 사용자 요청 페이지 (1부터 시작)
                                        @RequestParam(defaultValue = "8") int size,
                                        @RequestParam(required = false) String productCodeQuery,
                                        @RequestParam(required = false) String productNameQuery,
                                        @RequestParam(required = false) String procurementPlanCodeQuery,
                                        @RequestParam(required = false) LocalDate dateStart, // 시작날짜
                                        @RequestParam(required = false) LocalDate dateEnd // 종료 날짜
    ) {
        Pageable pageable = PageRequest.of(page - 1, size); // 0부터 시작하도록 조정

        // 날짜 필터링 및 기타 필터 조건 추가
        Page<ProgressInspectionDTO> productionPlanPage = progressInspectionService.searchProgressInspections(
                productCodeQuery, productNameQuery, procurementPlanCodeQuery, dateStart, dateEnd, pageable);

        // 로그로 productionPlanPage 출력
        System.out.println("============ 시작: productionPlanPage 출력 ============");
        productionPlanPage.getContent().forEach(System.out::println);
        System.out.println("============ 끝: productionPlanPage 출력 ============");

        int totalPages = productionPlanPage.getTotalPages();
        int currentPage = page;

        // 페이지네이션 범위 계산 (최대 5개 페이지 표시)
        int startPage = 1;
        int endPage = 1;

        if (totalPages > 0) {
            startPage = Math.max(1, currentPage - 2); // 최소 페이지는 1
            endPage = Math.min(totalPages, startPage + 4); // 최대 페이지는 startPage + 4
            startPage = Math.max(1, endPage - 4); // 보정
        }

        // 모델에 데이터 및 페이지네이션 정보 추가
        model.addAttribute("progressInspectionList", productionPlanPage.getContent());
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("size", size);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("dateStart", dateStart); // 상태 유지
        model.addAttribute("dateEnd", dateEnd); // 상태 유지
        model.addAttribute("productCodeQuery", productCodeQuery); // 상태 유지
        model.addAttribute("productNameQuery", productNameQuery); // 상태 유지
        model.addAttribute("procurementPlanCodeQuery", procurementPlanCodeQuery); // 상태 유지

        return "purchaseOrder/progressInspection";
    }

    @GetMapping("/progressInspectionProcessingBoard")
    public String ProcessingBoardView(Model model, HttpSession session) {

        String businessId = (String) session.getAttribute("businessId");
        Pageable pageable = PageRequest.of(0, 8);
        Page<ProgressInspectionDTO> productionPlanPage = progressInspectionService.getInspectionsByBusinessId(businessId, pageable);

        // 페이지네이션 관련 정보
        int currentPage = productionPlanPage.getNumber() + 1; // 현재 페이지 (0-based를 1-based로 변환)
        int totalPages = productionPlanPage.getTotalPages();  // 총 페이지 수
        int startPage = Math.max(1, currentPage - 2); // 시작 페이지
        int endPage = Math.min(totalPages, startPage + 4); // 끝 페이지
        startPage = Math.max(1, endPage - 4); // 보정

        // 모델에 데이터 추가
        model.addAttribute("progressInspectionList", productionPlanPage.getContent()); // 데이터 리스트
        model.addAttribute("currentPage", currentPage); // 현재 페이지
        model.addAttribute("totalPages", totalPages); // 총 페이지
        model.addAttribute("startPage", startPage); // 시작 페이지
        model.addAttribute("endPage", endPage); // 끝 페이지

        return "purchaseOrder/progressInspectionProcessing";
    }

    @PostMapping("/updateInspectedQuantity")
    public String updateInspectedQuantity(@RequestParam String progressInspectionCode,
                                          @RequestParam Long newInspectionQuantity,
                                          HttpSession session,
                                          RedirectAttributes redirectAttributes) {
        try {
            String businessId = (String) session.getAttribute("businessId");
            progressInspectionService.updateInspectedQuantity(progressInspectionCode, newInspectionQuantity, businessId);

            redirectAttributes.addFlashAttribute("message", "검수 수량이 성공적으로 업데이트되었습니다.");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "검수 수량 업데이트 중 오류가 발생했습니다.");
            redirectAttributes.addFlashAttribute("messageType", "error");
        }

        return "redirect:/progressInspection/progressInspectionProcessingBoard";
    }

}
