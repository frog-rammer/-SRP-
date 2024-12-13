package com.procuone.mit_kdt.service.impl;

import com.procuone.mit_kdt.dto.ProductionPlanDTO;
import com.procuone.mit_kdt.entity.ProductionPlan;
import com.procuone.mit_kdt.repository.ProductionPlanRepository;
import com.procuone.mit_kdt.service.ProductionPlanService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductionPlanServiceImpl implements ProductionPlanService {

    @Autowired
    private ProductionPlanRepository productionPlanRepository;

    @Override
    public void savePlan(ProductionPlanDTO productionPlanDTO) {
        // 새로운 ID 생성
        String newId = generateNewId();
        productionPlanDTO.setProductPlanCode(newId);

        productionPlanRepository.save(dtoToEntity(productionPlanDTO));
    }
    @Override
    public ProductionPlanDTO getPlanById(String planNum) {
        return productionPlanRepository.findById(planNum)
                .map(this::entityToDto)
                .orElse(null);
    }

    @Override
    public List<ProductionPlanDTO> getAllPlans() {
        return productionPlanRepository.findAll().stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deletePlan(String planNum) {
        productionPlanRepository.deleteById(planNum);
    }

    @Override
    public Page<ProductionPlanDTO> getAllPlans(Pageable pageable) {
        Page<ProductionPlan> planPage = productionPlanRepository.findAll(pageable);
        return planPage.map(this::entityToDto);
    }

    // DTO를 엔티티로 변환
    private ProductionPlan dtoToEntity(ProductionPlanDTO dto) {
        return ProductionPlan.builder()
                .productPlanCode(dto.getProductPlanCode())
                .productName(dto.getProductName())
                .productCode(dto.getProductCode())
                .planStartDate(dto.getPlanStartDate())
                .planEndDate(dto.getPlanEndDate())
                .quantity(dto.getQuantity())
                .build();
    }

    // 엔티티를 DTO로 변환
    private ProductionPlanDTO entityToDto(ProductionPlan entity) {
        return ProductionPlanDTO.builder()
                .productPlanCode(entity.getProductPlanCode())
                .productName(entity.getProductName())
                .productCode(entity.getProductCode())
                .planStartDate(entity.getPlanStartDate())
                .planEndDate(entity.getPlanEndDate())
                .quantity(entity.getQuantity())
                .build();
    }

    private String generateNewId() {
        String prefix = "PdPlan_";
        int nextId = productionPlanRepository.findMaxId()
                .map(id -> Integer.parseInt(id.substring(8)) + 1)
                .orElse(1);
        return prefix + String.format("%03d", nextId);
    }

    public Page<ProductionPlanDTO> searchPlans(String searchType, String searchKeyword, String startDate, String endDate, Pageable pageable) {
        LocalDate start = (startDate != null && !startDate.isEmpty()) ? LocalDate.parse(startDate) : null;
        LocalDate end = (endDate != null && !endDate.isEmpty()) ? LocalDate.parse(endDate) : null;

        Page<ProductionPlan> productionPlanPage; // 변수 선언

        // 검색 조건에 따라 다른 쿼리 실행
        if (start != null && end != null) {
            productionPlanPage = productionPlanRepository.findByDateRangeAndKeyword(searchType, searchKeyword, start, end, pageable);
        } else if (searchKeyword != null && !searchKeyword.isEmpty()) {
            productionPlanPage = productionPlanRepository.findByKeyword(searchType, searchKeyword, pageable);
        } else {
            productionPlanPage = productionPlanRepository.findAll(pageable); // 검색 조건이 없으면 전체 조회
        }

        // ProductionPlan 엔티티를 DTO로 변환하여 반환
        return productionPlanPage.map(this::entityToDto);
    }

    @Override
    public Page<ProductionPlanDTO> searchProductionPlans(String productName, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        Page<ProductionPlan> productionPlans = productionPlanRepository.findByProductNameAndPlanStartDateBetween(
                productName, startDate, endDate, pageable);

        return productionPlans.map(this::entityToDto);
    }

    @Override
    public void processExcelFile(MultipartFile file) throws Exception {
        List<ProductionPlanDTO> productionPlans = new ArrayList<>();
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0); // 첫 번째 시트 사용
            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // 첫 번째 행은 헤더
                Row row = sheet.getRow(i);
                if (row != null) {
                    ProductionPlanDTO dto = new ProductionPlanDTO();

                    // 0번 셀: ProductPlanCode (문자열)
                    dto.setProductPlanCode(getStringValue(row.getCell(0)));

                    // 1번 셀: ProductName (문자열)
                    dto.setProductName(getStringValue(row.getCell(1)));

                    // 2번 셀: ProductCode (문자열)
                    dto.setProductCode(getStringValue(row.getCell(2)));

                    // 3번 셀: PlanStartDate (날짜)
                    dto.setPlanStartDate(getDateValue(row.getCell(3)));

                    // 4번 셀: PlanEndDate (날짜)
                    dto.setPlanEndDate(getDateValue(row.getCell(4)));

                    // 5번 셀: Quantity (숫자)
                    dto.setQuantity(getNumericValue(row.getCell(5)));

                    // 6번 셀: RequiredProcurementQuantity (숫자)
                    dto.setRequiredProcurementQuantity((long) getNumericValue(row.getCell(6)));

                    productionPlans.add(dto);
                }
            }
        }

        // 저장 로직
        for (ProductionPlanDTO dto : productionPlans) {
            saveProductionPlan(dto);
        }
    }

    private int getNumericValue(Cell cell) {
        if (cell == null) return 0;
        return switch (cell.getCellType()) {
            case NUMERIC -> (int) cell.getNumericCellValue();
            case STRING -> {
                try {
                    yield Integer.parseInt(cell.getStringCellValue().trim());
                } catch (NumberFormatException e) {
                    yield 0; // 숫자가 아닌 경우 기본값
                }
            }
            default -> 0;
        };
    }

    private void saveProductionPlan(ProductionPlanDTO dto) {
        // DTO를 엔티티로 변환
        ProductionPlan productionPlan = ProductionPlan.builder()
                .productPlanCode(dto.getProductPlanCode())
                .productName(dto.getProductName())
                .productCode(dto.getProductCode())
                .planStartDate(dto.getPlanStartDate())
                .planEndDate(dto.getPlanEndDate())
                .quantity(dto.getQuantity())
                .build();

        // 데이터베이스에 저장
        productionPlanRepository.save(productionPlan);
    }


    private String getStringValue(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((int) cell.getNumericCellValue()); // 숫자를 문자열로 변환
            default -> null; // 빈 셀 등 기타 처리
        };
    }

    private LocalDate getDateValue(Cell cell) {
        if (cell == null) return null; // 셀이 null인 경우 처리
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getLocalDateTimeCellValue().toLocalDate();
        } else if (cell.getCellType() == CellType.STRING) {
            // 문자열로 날짜가 입력된 경우 파싱 시도
            try {
                return LocalDate.parse(cell.getStringCellValue().trim());
            } catch (Exception e) {
                System.err.println("날짜 파싱 실패: " + cell.getStringCellValue());
            }
        }
        return null; // 날짜가 아닌 경우 null 반환
    }
}
