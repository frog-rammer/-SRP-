package com.procuone.mit_kdt.controller;

import com.procuone.mit_kdt.dto.ItemDTOs.ItemDTO;
import com.procuone.mit_kdt.dto.ItemDTOs.CategoryDTO;
import com.procuone.mit_kdt.entity.BOM.Item;
import com.procuone.mit_kdt.service.ItemService;
import com.procuone.mit_kdt.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequestMapping("/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private CategoryService categoryService;

    // 상품 등록 폼 표시
    @GetMapping("/registerProductForm")
    public String showRegisterForm(Model model) {
        // 빈 ItemDTO 객체와 최상위 카테고리 리스트를 모델에 추가
        model.addAttribute("item", new ItemDTO());

        // 최상위 카테고리를 DTO 형식으로 가져옴
        List<CategoryDTO> rootCategories = categoryService.getRootCategories();
        model.addAttribute("categories", rootCategories);

        return "registerProductForm"; // registerProductForm.html 페이지 렌더링
    }

    // 상품 데이터 저장 처리
    @PostMapping("/InputProduct")
    public String saveItem(
            @ModelAttribute Item item,
            @RequestParam("drawingFile") MultipartFile file,
            RedirectAttributes redirectAttributes) {

        // 파일 저장 처리
        if (!file.isEmpty()) {
            String uploadDir = "src/main/resources/static/images/BluePrints/"; // 파일 저장 경로
            String filePath = saveFile(file, uploadDir, redirectAttributes);
            if (filePath == null) {
                return "redirect:/items/registerProductForm";
            }
            item.setDrawingFile(filePath); // 저장된 파일 경로 설정
        }

        // Item 엔티티 저장
        itemService.saveItem(item);
        redirectAttributes.addFlashAttribute("successMessage", "품목이 성공적으로 등록되었습니다!");

        return "redirect:/items"; // 등록 완료 후 목록 페이지로 리다이렉트
    }

    // 하위 카테고리 AJAX 응답
    @GetMapping("/categories/subcategories")
    @ResponseBody
    public List<CategoryDTO> getSubCategories(@RequestParam Long parentId) {
        // 하위 카테고리를 DTO 형식으로 반환
        return categoryService.getSubCategoriesByParentId(parentId);
    }

    // 파일 저장 메서드 (중복 방지 및 예외 처리 포함)
    private String saveFile(MultipartFile file, String uploadDir, RedirectAttributes redirectAttributes) {
        String fileName = file.getOriginalFilename();
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(fileName);
            file.transferTo(filePath.toFile());
            return "/images/BluePrints/" + fileName; // 웹 경로 반환
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "파일 업로드에 실패했습니다.");
            return null;
        }
    }
}
