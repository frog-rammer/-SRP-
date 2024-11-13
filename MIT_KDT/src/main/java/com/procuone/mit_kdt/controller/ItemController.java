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

    // 상품 등록 폼 표시
    @GetMapping("/registerProductForm")
    public String showRegisterForm(Model model) {
        // 빈 ItemDTO 객체와 최상위 카테고리 리스트를 모델에 추가
        model.addAttribute("item", new ItemDTO());

        // 최상위 카테고리를 DTO 형식으로 가져옴
        List<CategoryDTO> rootCategories = categoryService.getRootCategories();
        model.addAttribute("categories", rootCategories);

        return "procurementPlan/registerProductForm"; // registerProductForm.html 페이지 렌더링
    }
    @GetMapping("/view")
    public String showView(Model model) {
        model.addAttribute("item", new ItemDTO());
        return "procurementPlan/view";
    }

    @PostMapping("/InputProduct")
    public String saveItem(
            @ModelAttribute Item item, // Item 엔티티
            @RequestParam("drawingFile") MultipartFile file, // 업로드된 파일
            RedirectAttributes redirectAttributes) {

        // 파일 업로드 처리
        if (file != null && !file.isEmpty()) {
            String uploadDir = "src/main/resources/static/images/BluePrints/"; // 저장 경로
            String webPath = "/images/BluePrints/"; // 반환할 웹 경로
            String filePath = saveFile(file, uploadDir, webPath, redirectAttributes);
            if (filePath == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "파일 업로드에 실패했습니다.");
                return "redirect:/items/registerProductForm";
            }
            // 파일 경로를 Item 엔티티의 drawingFilePath 필드에 저장
            item.setDrawingFile(filePath);
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


    // 파일 저장 메서드
    private String saveFile(MultipartFile file, String uploadDir, String webPath, RedirectAttributes redirectAttributes) {
        try {
            // 파일 저장 경로 생성
            File uploadDirectory = new File(uploadDir);
            if (!uploadDirectory.exists()) {
                boolean isCreated = uploadDirectory.mkdirs(); // 디렉토리 생성
                if (!isCreated) {
                    throw new IOException("디렉토리 생성에 실패했습니다: " + uploadDir);
                }
            }

            // 파일 이름 생성 (중복 방지)
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            File destinationFile = new File(uploadDir + fileName);

            // 파일 저장
            file.transferTo(destinationFile);

            // 웹 경로 반환
            return webPath + fileName;
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "파일 저장 중 오류가 발생했습니다.");
            return null;
        }
    }


}
