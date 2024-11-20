package com.procuone.mit_kdt.controller;

import com.procuone.mit_kdt.dto.ItemDTOs.ItemDTO;
import com.procuone.mit_kdt.dto.ItemDTOs.CategoryDTO;
import com.procuone.mit_kdt.entity.BOM.Item;
import com.procuone.mit_kdt.service.ContractService;
import com.procuone.mit_kdt.service.ItemService;
import com.procuone.mit_kdt.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ContractService contractService;

    // 품목 목록을 반환하는 API
    @GetMapping("/api/items")
    public List<Item> getAllItems() {
        return contractService.getAllItems();  // 서비스에서 품목 목록을 가져옴
    }

    // 상품 등록 폼 표시
    @GetMapping("/registerProductForm")
    public String showRegisterForm(Model model) {
        model.addAttribute("item", new ItemDTO());
        List<CategoryDTO> rootCategories = categoryService.getRootCategories();
        model.addAttribute("categories", rootCategories);
        return "procurementPlan/registerProductForm";
    }

    @GetMapping("/view")
    public String showView(Model model) {
        model.addAttribute("item", new ItemDTO());
        return "procurementPlan/view";
    }

    @PostMapping("/InputProduct")
    public String saveItem(
            @ModelAttribute ItemDTO itemDTO,
            @RequestParam(value = "file", required = false) MultipartFile file,
            RedirectAttributes redirectAttributes) {

        try {
            // 파일 업로드 처리
            if (file != null && !file.isEmpty()) {
                String uploadDir = "C:/blueprints/"; // C 드라이브에 blueprints 디렉토리
                String webPath = "/blueprints/";    // 웹 경로
                String filePath = saveFile(file, uploadDir, webPath, redirectAttributes);
                if (filePath == null) {
                    redirectAttributes.addFlashAttribute("errorMessage", "파일 업로드에 실패했습니다.");
                    return "redirect:/items/registerProductForm";
                }
                itemDTO.setDrawingFile(filePath); // 파일 경로 설정
            } else {
                itemDTO.setDrawingFile(null); // 파일이 없으면 null
            }

            // 서비스 계층 저장 호출
            boolean isSaved = itemService.saveItem(itemDTO);
            if (!isSaved) {
                redirectAttributes.addFlashAttribute("infoMessage", "기존 품목이 이미 등록되어 있습니다.");
                return "redirect:/items";
            }

            redirectAttributes.addFlashAttribute("successMessage", "품목이 성공적으로 등록되었습니다!");

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "품목 등록 중 오류 발생: " + e.getMessage());
            return "redirect:/items/registerProductForm";
        }

        return "redirect:/items";
    }

    @GetMapping("/categories/subcategories")
    @ResponseBody
    public List<CategoryDTO> getSubCategories(@RequestParam Long parentId) {
        return categoryService.getSubCategoriesByParentId(parentId);
    }

    // 파일 저장 메서드
    private String saveFile(MultipartFile file, String uploadDir, String webPath, RedirectAttributes redirectAttributes) {
        try {
            // 디렉토리 생성
            File uploadDirectory = new File(uploadDir);
            if (!uploadDirectory.exists()) {
                boolean isCreated = uploadDirectory.mkdirs();
                if (!isCreated) {
                    throw new IOException("디렉토리 생성 실패: " + uploadDir);
                }
            }

            // 파일 이름 생성
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            File destinationFile = new File(uploadDir + fileName);

            // 파일 저장
            file.transferTo(destinationFile);

            // 웹 경로 반환
            return webPath + fileName;
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "파일 저장 중 오류 발생: " + e.getMessage());
            return null;
        }
    }
}
