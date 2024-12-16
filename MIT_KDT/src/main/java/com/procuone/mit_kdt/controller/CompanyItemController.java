package com.procuone.mit_kdt.controller;

import com.procuone.mit_kdt.dto.CompanyItemDTO;
import com.procuone.mit_kdt.entity.CompanyItem;
import com.procuone.mit_kdt.service.CompanyItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/company")
public class CompanyItemController {
    @Autowired
    private CompanyItemService companyItemService;
    @PostMapping("/uploadExcel")
    @ResponseBody
    public ResponseEntity<?> uploadExcel(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("No file uploaded.");
        }
        try {
            companyItemService.processExcelFile(file);
            return ResponseEntity.ok("Excel file uploaded and processed successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to upload Excel file: " + e.getMessage());
        }
    }
}
