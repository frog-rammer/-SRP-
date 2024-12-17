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
                                        @RequestParam(required = false) LocalDate dateEnd) {
        Pageable pageable = PageRequest.of(page - 1, size);

        // '검수예정' 상태 데이터 필터링 및 조건 검색
        Page<ProgressInspectionDTO> progressPage = progressInspectionService.searchProgressInspections(
                productCodeQuery, productNameQuery, null, dateStart, dateEnd, pageable);

        int totalPages = progressPage.getTotalPages();
        int currentPage = page;
        int startPage = Math.max(1, currentPage - 2);
        int endPage = Math.min(totalPages, startPage + 4);
        startPage = Math.max(1, endPage - 4);

        model.addAttribute("progressInspectionList", progressPage.getContent());
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("dateStart", dateStart);
        model.addAttribute("dateEnd", dateEnd);
        model.addAttribute("productCodeQuery", productCodeQuery);
        model.addAttribute("productNameQuery", productNameQuery);

        return "purchaseOrder/progressInspection";
    }

    @GetMapping("/progressInspectionProcessingBoard")
    public String ProcessingBoardView(Model model, HttpSession session, @RequestParam(defaultValue = "1") int page, // 사용자 요청 페이지 (1부터 시작)
                                      @RequestParam(defaultValue = "8") int size,
                                      Pageable pageable) {

        String businessId = (String) session.getAttribute("businessId");
        pageable = PageRequest.of(page - 1, size);
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
