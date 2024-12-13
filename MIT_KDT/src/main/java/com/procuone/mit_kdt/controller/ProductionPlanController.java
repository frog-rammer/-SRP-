package com.procuone.mit_kdt.controller;

import com.procuone.mit_kdt.dto.ItemDTOs.ItemDTO;
import com.procuone.mit_kdt.dto.ProductionPlanDTO;
import com.procuone.mit_kdt.service.ItemService;
import com.procuone.mit_kdt.service.ProductionPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/productionPlan")
public class ProductionPlanController {

    @Autowired
    private ProductionPlanService productionPlanService;
    @Autowired
    private ItemService itemService;

    // 생산 계획 입력 폼
    @GetMapping("/input")
    public String input(Model model) {
        model.addAttribute("productionPlanDTO", new ProductionPlanDTO());
        // 상위 아이템 리스트를 모델에 추가
        List<ItemDTO> topItems = itemService.getTopItems();
        model.addAttribute("topItems", topItems);
        return "procurementPlan/productionPlanInput";  // 템플릿 이름
    }

    // 생산 계획 조회
    @GetMapping("/view")
    public String view(Model model, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductionPlanDTO> productionPlanPage = productionPlanService.getAllPlans(pageable);

        model.addAttribute("productionPlanList", productionPlanPage.getContent()); // 생산 계획 목록
        model.addAttribute("currentPage", productionPlanPage.getNumber());  // 현재 페이지
        model.addAttribute("totalPages", productionPlanPage.getTotalPages());  // 총 페이지 수
        model.addAttribute("totalItems", productionPlanPage.getTotalElements()); // 전체 아이템 수

        return "procurementPlan/productionPlanView";
    }

    // 생산 계획 저장
    @PostMapping("/save")
    public String planSave(@ModelAttribute("productionPlanDTO") ProductionPlanDTO productionPlanDTO,
                           BindingResult result, Model model,
                           @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        if (result.hasErrors()) {
            return "procurementPlan/productionPlanInput";  // 유효성 검사 실패 시 입력 폼으로 돌아가기
        }

        // 엔티티 저장
        Optional<ItemDTO> itemDTO = itemService.findByProductCode(productionPlanDTO.getProductCode());
        itemDTO.ifPresent(item -> productionPlanDTO.setProductName(item.getItemName()));
        productionPlanService.savePlan(productionPlanDTO);  // DTO를 서비스에 전달

        // 페이지네이션 처리
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductionPlanDTO> productionPlanPage = productionPlanService.getAllPlans(pageable);

        // 성공 메시지 추가
        model.addAttribute("successMessage", "생산 계획이 성공적으로 저장되었습니다.");
        model.addAttribute("productionPlanList", productionPlanPage.getContent()); // 생산 계획 목록
        model.addAttribute("currentPage", productionPlanPage.getNumber());  // 현재 페이지
        model.addAttribute("totalPages", productionPlanPage.getTotalPages());  // 총 페이지 수
        model.addAttribute("totalItems", productionPlanPage.getTotalElements()); // 전체 아이템 수

        return "redirect:/productionPlan/input";  // 저장 후 "input" 페이지로 리다이렉트
    }

    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> updatePlan(@RequestBody ProductionPlanDTO productionPlanDTO) {
        try {
            // 기존 데이터를 업데이트 하는 메서드로 변경해야 함
            productionPlanService.updatePlan(productionPlanDTO);  // DB에서 수정
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<Map<String, Object>> deletePlan(@RequestBody Map<String, String> request) {
        String productPlanCode = request.get("productPlanCode");

        try {
            productionPlanService.deletePlan(productPlanCode);  // DB에서 삭제
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    // 생산 계획 검색
    @PostMapping("/search")
    public String searchPlans(
            @RequestParam(required = false) String searchType, // 검색 기준
            @RequestParam(required = false) String searchKeyword, // 검색 키워드
            @RequestParam(required = false) String startDate, // 시작 날짜
            @RequestParam(required = false) String endDate, // 종료 날짜
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);

        // 서비스에서 검색 처리
        Page<ProductionPlanDTO> productionPlanPage = productionPlanService.searchPlans(
                searchType, searchKeyword, startDate, endDate, pageable);

        model.addAttribute("productionPlanList", productionPlanPage.getContent());
        model.addAttribute("currentPage", productionPlanPage.getNumber());
        model.addAttribute("totalPages", productionPlanPage.getTotalPages());
        model.addAttribute("totalItems", productionPlanPage.getTotalElements());
        model.addAttribute("searchType", searchType);
        model.addAttribute("searchKeyword", searchKeyword);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "procurementPlan/productionPlanView"; // 결과를 보여줄 뷰
    }
}