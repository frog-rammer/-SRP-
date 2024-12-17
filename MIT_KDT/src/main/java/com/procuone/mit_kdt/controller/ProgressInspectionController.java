package com.procuone.mit_kdt.controller;

import com.procuone.mit_kdt.dto.CompanyInventoryDTO;
import com.procuone.mit_kdt.dto.ProgressInspectionDTO;
import com.procuone.mit_kdt.entity.CompanyInventory;
import com.procuone.mit_kdt.entity.ProgressInspection;
import com.procuone.mit_kdt.service.CompanyInventoryService;
import com.procuone.mit_kdt.service.ItemService;
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
    @Autowired
    CompanyInventoryService companyInventoryService;
    @Autowired
    ItemService itemService;
    @GetMapping("/progressInspectionBoard")
    public String getProgressInspection(Model model,
                                        @RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "8") int size,
                                        @RequestParam(required = false) String productCodeQuery,
                                        @RequestParam(required = false) String productNameQuery,
                                        @RequestParam(required = false) LocalDate dateStart,
                                        @RequestParam(required = false) LocalDate dateEnd,
                                        @RequestParam(required = false) String inspectionStatus) {
        Pageable pageable = PageRequest.of(page - 1, size);

        // NULL 또는 공백 값 처리
        productCodeQuery = (productCodeQuery != null && !productCodeQuery.isEmpty()) ? productCodeQuery : null;
        productNameQuery = (productNameQuery != null && !productNameQuery.isEmpty()) ? productNameQuery : null;
        inspectionStatus = (inspectionStatus != null && !inspectionStatus.isEmpty()) ? inspectionStatus : null;

        // 필터링 검색 호출
        Page<ProgressInspectionDTO> progressPage = progressInspectionService.searchProgressInspections(
                productCodeQuery, productNameQuery, null, dateStart, dateEnd, inspectionStatus, pageable);

        // 페이지네이션 설정
        int totalPages = progressPage.getTotalPages();
        model.addAttribute("progressInspectionList", progressPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", Math.max(1, page - 2));
        model.addAttribute("endPage", Math.min(totalPages, page + 2));

        // 기존 필터링 조건을 화면에 유지
        model.addAttribute("dateStart", dateStart);
        model.addAttribute("dateEnd", dateEnd);
        model.addAttribute("productCodeQuery", productCodeQuery);
        model.addAttribute("productNameQuery", productNameQuery);
        model.addAttribute("inspectionStatus", inspectionStatus);

        return "purchaseOrder/progressInspection";
    }

    @GetMapping("/progressInspectionProcessingBoard")
    public String ProcessingBoardView(Model model, HttpSession session,
                                      @RequestParam(defaultValue = "1") int ongoingPage, // 상단 테이블 페이지
                                      @RequestParam(defaultValue = "1") int completedPage, // 하단 테이블 페이지
                                      @RequestParam(defaultValue = "8") int size) {

        String businessId = (String) session.getAttribute("businessId");

        // 상단 테이블: 검수예정과 검수진행중 상태
        Pageable ongoingPageable = PageRequest.of(ongoingPage - 1, size);
        Page<ProgressInspectionDTO> ongoingInspections = progressInspectionService
                .getInspectionsByStatus(businessId, "검수예정", "검수진행중", ongoingPageable);

        // 하단 테이블: 검수완료 상태
        Pageable completedPageable = PageRequest.of(completedPage - 1, size);
        Page<ProgressInspectionDTO> completedInspections = progressInspectionService
                .getInspectionsByStatus(businessId, "검수완료", null, completedPageable);

        // 상단 테이블 페이지네이션
        model.addAttribute("progressInspectionList", ongoingInspections.getContent());
        model.addAttribute("ongoingCurrentPage", ongoingPage);
        model.addAttribute("ongoingTotalPages", ongoingInspections.getTotalPages());

        // 하단 테이블 페이지네이션
        model.addAttribute("completedInspectionList", completedInspections.getContent());
        model.addAttribute("completedCurrentPage", completedPage);
        model.addAttribute("completedTotalPages", completedInspections.getTotalPages());

        return "purchaseOrder/progressInspectionProcessing";
    }

    @PostMapping("/updateInspectedQuantity")
    public String updateInspectedQuantity(@RequestParam String progressInspectionCode,
                                          @RequestParam Long newInspectionQuantity,
                                          @RequestParam String productCode,
                                          HttpSession session,
                                          RedirectAttributes redirectAttributes) {
        try {
            String businessId = (String) session.getAttribute("businessId");
            progressInspectionService.updateInspectedQuantity(progressInspectionCode, newInspectionQuantity, businessId);

            // 수정해야함 ((검수 수량 업데이트 시 협력업체 인벤토리도 업데이트))
            Long ItemId = itemService.getItemIdByProductCode(productCode);
            System.out.println("++++++++++++++++++++++++++++++++" +ItemId);
            if (ItemId != null) {
                CompanyInventoryDTO companyInventoryDTO = companyInventoryService.getInventoryByBusinessIdAndItemId(businessId, ItemId);
                if (companyInventoryDTO != null) {
                    companyInventoryDTO.setCurrentQuantity(companyInventoryDTO.getCurrentQuantity() + (newInspectionQuantity.intValue()));
                    companyInventoryDTO.setLastUpdated(LocalDate.now().toString());
                } else {
                    companyInventoryDTO = new CompanyInventoryDTO();
                    companyInventoryDTO.setBusinessId(businessId);
                    companyInventoryDTO.setItemId(ItemId);
                    companyInventoryDTO.setCurrentQuantity(newInspectionQuantity.intValue());
                    companyInventoryDTO.setReservedQuantity(0);
                    companyInventoryDTO.setLastUpdated(LocalDate.now().toString());
                }
                companyInventoryService.saveOrUpdateInventory(companyInventoryDTO);
            }

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
