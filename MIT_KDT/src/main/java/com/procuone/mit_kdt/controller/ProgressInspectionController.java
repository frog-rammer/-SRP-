package com.procuone.mit_kdt.controller;

import com.procuone.mit_kdt.dto.ProgressInspectionDTO;
import com.procuone.mit_kdt.service.ProgressInspectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/progressInspection")
public class ProgressInspectionController {

    @Autowired
    ProgressInspectionService progressInspectionService;

    @GetMapping
    public String getProgressInspection(Model model,
                                        @RequestParam(defaultValue = "1") int page, // 사용자 요청 페이지 (1부터 시작)
                                        @RequestParam(defaultValue = "8") int size,
                                        @RequestParam(required = false) String productCodeQuery,
                                        @RequestParam(required = false) String productNameQuery,
                                        @RequestParam(required = false) String procurementPlanCodeQuery,
                                        @RequestParam(required = false) LocalDate dateStart, // 시작날짜
                                        @RequestParam(required = false) LocalDate dateEnd// 종료 날짜
    ) {
        Pageable pageable = PageRequest.of(page - 1, size); // 0부터 시작하도록 조정

        // 날짜 필터링 및 기타 필터 조건 추가
        Page<ProgressInspectionDTO> productionPlanPage = progressInspectionService.searchProgressInspections(
                productCodeQuery, productNameQuery, procurementPlanCodeQuery, dateStart, dateEnd, pageable);

        int totalPages = productionPlanPage.getTotalPages();
        int currentPage = page;

        // 페이지네이션 범위 계산 (최대 5개 페이지 표시)
        int startPage = Math.max(1, currentPage - 2); // 최소 페이지는 1
        int endPage = Math.min(totalPages, startPage + 4); // 최대 페이지는 startPage + 4

        // 페이지 범위를 조정하여 시작 페이지가 최소 범위를 초과하지 않도록 보정
        startPage = Math.max(1, endPage - 4);

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
}

