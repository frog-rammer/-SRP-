package com.procuone.mit_kdt.controller;

import com.procuone.mit_kdt.dto.ItemDTOs.ItemDTO;
import com.procuone.mit_kdt.entity.BOM.Item;
import com.procuone.mit_kdt.entity.BOM.Category;
import com.procuone.mit_kdt.service.ItemService;
import com.procuone.mit_kdt.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/registerProductForm")
    public String showRegisterForm(Model model) {
        // 빈 Item 객체와 카테고리 리스트를 모델에 추가
        model.addAttribute("item", new ItemDTO());

        // 카테고리 목록을 계층 구조로 구성
        List<Category> categories = categoryService.getAllCategories();

        // categories 리스트가 비어 있지 않은지 확인
        if (categories == null || categories.isEmpty()) {
            System.out.println("Categories 리스트가 비어 있습니다.");
        }

        model.addAttribute("categories", categories);

        return "registerProductForm"; // registerProductForm.html 페이지 렌더링
    }


    // Item 데이터를 저장하는 POST 메서드
    @PostMapping("/InputProduct")
    public String saveItem(
            @ModelAttribute Item item,
            @RequestParam("drawingFile") MultipartFile file,
            RedirectAttributes redirectAttributes) {

        // 파일 저장 처리
        if (!file.isEmpty()) {
            String fileName = file.getOriginalFilename();
            String uploadDir = "src/main/resources/static/images/BluePrints/"; // 파일 저장 경로 설정

            try {
                // 저장 디렉토리가 없으면 생성
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // 파일 저장
                Path filePath = uploadPath.resolve(fileName);
                file.transferTo(filePath.toFile());

                // 엔티티에 웹 경로로 파일 경로 설정
                item.setDrawingFile("/images/BluePrints/" + fileName);

            } catch (IOException e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("errorMessage", "파일 업로드에 실패했습니다.");
                return "redirect:/items/registerProductForm";
            }
        }

        // Item 엔티티 저장
        itemService.saveItem(item);
        redirectAttributes.addFlashAttribute("successMessage", "품목이 성공적으로 등록되었습니다!");

        return "redirect:/items"; // 등록 완료 후 목록 페이지로 리다이렉트 (또는 다른 페이지로 이동 설정)
    }
}
