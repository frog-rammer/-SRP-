package com.procuone.mit_kdt.service.impl;

import com.procuone.mit_kdt.dto.CompanyItemDTO;
import com.procuone.mit_kdt.dto.ItemDTOs.ItemDTO;
import com.procuone.mit_kdt.entity.BOM.Item;
import com.procuone.mit_kdt.entity.Company;
import com.procuone.mit_kdt.entity.CompanyItem;
import com.procuone.mit_kdt.repository.BOMRelationshipRepository;
import com.procuone.mit_kdt.repository.CompanyItemRepository;
import com.procuone.mit_kdt.repository.CompanyRepository;
import com.procuone.mit_kdt.repository.ItemRepository;
import com.procuone.mit_kdt.service.CompanyItemService;
import com.procuone.mit_kdt.service.ItemService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CompanyItemServiceImpl implements CompanyItemService {

    @Autowired
    private CompanyItemRepository companyItemRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemService itemService;

    @Autowired
    private BOMRelationshipRepository bomRelationshipRepository;

    @Override
    public List<CompanyItem> findItemsByItemId(Long itemId) {
        return companyItemRepository.findItemsByItemId(itemId);
    }

    @Override
    public List<CompanyItemDTO> getAvailableCompanyItems(String parentProductCode) {
        List<String> childProductCodes = bomRelationshipRepository.findChildProductCodesByParentCode(parentProductCode);
        System.out.println("Child Product Codes: " + childProductCodes);

        // child_product_code -> item_id 추출
        List<Long> itemIds = childProductCodes.stream()
                .map(productCode -> {
                    Long id = itemRepository.findIdByProductCode(productCode);
                    System.out.println("Product Code: " + productCode + ", Item ID: " + id);
                    return id;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        System.out.println("Item IDs: " + itemIds);

        // CompanyItem에서 계약 상태가 false인 데이터 조회
        List<CompanyItem> companyItems = itemIds.stream()
                .flatMap(itemId -> companyItemRepository.findByItemIdAndContractStatusFalse(itemId).stream())
                .collect(Collectors.toList());
        System.out.println("Company Items: " + companyItems.size());

        return companyItems.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 특정 카테고리 이름에 해당하며 계약 상태가 false인 업체 리스트
    @Override
    public List<CompanyItemDTO> getSuppliersByCategoryName(String categoryName) {
        List<CompanyItem> companyItems = companyItemRepository.findByCategoryNameAndContractStatusFalse(categoryName);
        System.out.println("Fetched Company Items: " + companyItems); // 결과 로그 추가
        return companyItems.stream()
                .map(this::convertEntityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CompanyItemDTO> getSuppliersByItem(Long itemId) {
        List<CompanyItem> companyItems = companyItemRepository.findByItemId(itemId);
        return companyItems.stream()
                .map(companyItem -> new CompanyItemDTO(
                        companyItem.getId(),
                        companyItem.getCompany().getBusinessId(),
                        companyItem.getItem().getId(),
                        companyItem.getLeadTime(),
                        companyItem.getSupplyUnit(),
                        companyItem.getProductionQty(),
                        companyItem.getUnitCost(),
                        companyItem.getContractStatus()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public CompanyItemDTO getCompanyItemByBussinessId(String businessId) {
        List<CompanyItem> companyItems = companyItemRepository.findByCompany_BusinessId(businessId);
        if (companyItems.isEmpty()) {
            throw new IllegalArgumentException("No CompanyItem found for Business ID: " + businessId);
        }
        CompanyItem companyItem = companyItems.get(0);
        return new CompanyItemDTO(
                companyItem.getId(),
                companyItem.getCompany().getBusinessId(),
                companyItem.getItem().getId(),
                companyItem.getLeadTime(),
                companyItem.getSupplyUnit(),
                companyItem.getProductionQty(),
                companyItem.getUnitCost(),
                companyItem.getContractStatus()
        );
    }

    @Override
    public void saveCompanyItems(List<CompanyItemDTO> companyItemDTOs) {
        for (CompanyItemDTO companyItemDTO : companyItemDTOs) {
            saveSingleCompanyItem(companyItemDTO);
        }
    }

    private void saveSingleCompanyItem(CompanyItemDTO companyItemDTO) {
        Company company = companyRepository.findByBusinessId(companyItemDTO.getBusinessId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Business ID: " + companyItemDTO.getBusinessId()));

        Item item = itemRepository.findById(companyItemDTO.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Item ID: " + companyItemDTO.getItemId()));

        CompanyItem companyItem = CompanyItem.builder()
                .company(company)
                .item(item)
                .leadTime(companyItemDTO.getLeadTime())
                .supplyUnit(companyItemDTO.getSupplyUnit())
                .productionQty(companyItemDTO.getProductionQty())
                .unitCost(companyItemDTO.getUnitCost())
                .contractStatus(companyItemDTO.getContractStatus())
                .build();

        companyItemRepository.save(companyItem);
    }

    @Override
    public void updateContractStatus(Long itemId, String businessId, boolean status) {
        Optional<CompanyItem> companyItem = companyItemRepository.findByItemIdAndCompany_BusinessId(itemId, businessId);
        if (companyItem.isPresent()) {
            CompanyItem item = companyItem.get();
            item.setContractStatus(status);
            companyItemRepository.save(item);
        }
    }

    @Override
    public CompanyItemDTO getCompanyItemByBussinessIdAnditemId(String businessId, Long itemId) {
        Optional<CompanyItem> companyItem = companyItemRepository.findByCompany_BusinessIdAndItem_Id(businessId, itemId);
        if (!companyItem.isPresent()) {
            throw new NoSuchElementException("No CompanyItem found for businessId: " + businessId + " and itemId: " + itemId);
        }
        return convertToDTO(companyItem.get());
    }

    @Override
    public Optional<CompanyItem> findByItemIdAndBusinessId(Long itemId, String businessId) {
        return Optional.empty();
    }

    @Override
    public void update(CompanyItem companyItem) {
    }

    @Override
    public List<ItemDTO> getItemsByBusinessId(String businessId) {
        List<CompanyItem> companyItems = companyItemRepository.findByCompany_BusinessId(businessId);
        return companyItems.stream()
                .map(companyItem -> {
                    Item item = companyItem.getItem();
                    return ItemDTO.builder()
                            .id(item.getId())
                            .itemName(item.getItemName())
                            .build();
                })
                .collect(Collectors.toList());
    }

    private CompanyItemDTO convertToDTO(CompanyItem companyItem) {
        return CompanyItemDTO.builder()
                .id(companyItem.getId())
                .businessId(companyItem.getCompany().getBusinessId())
                .itemId(companyItem.getItem().getId())
                .leadTime(companyItem.getLeadTime())
                .supplyUnit(companyItem.getSupplyUnit())
                .productionQty(companyItem.getProductionQty())
                .unitCost(companyItem.getUnitCost())
                .contractStatus(companyItem.getContractStatus())
                .build();
    }

    public void processExcelFile(MultipartFile file) throws Exception {
        String prodcuctCode;
        List<CompanyItemDTO> companyItems = new ArrayList<>();
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    CompanyItemDTO dto = new CompanyItemDTO();

                    dto.setContractStatus(false);
                    dto.setLeadTime(getNumericValue(row.getCell(0)));
                    dto.setProductionQty(getNumericValue(row.getCell(1)));
                    dto.setSupplyUnit(getNumericValue(row.getCell(2)));
                    dto.setUnitCost(getNumericValue(row.getCell(3)));
                    dto.setBusinessId(getStringValue(row.getCell(4)));
                    prodcuctCode= getStringValue(row.getCell(5));
                    dto.setItemId(itemService.getItemIdByProductCode(prodcuctCode));

                    companyItems.add(dto);
                }
            }
        }

        for (CompanyItemDTO dto : companyItems) {
            saveSingleCompanyItem(dto);
        }
    }

    private Long getLongValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case NUMERIC:
                return (long) cell.getNumericCellValue();
            case STRING:
                try {
                    return Long.parseLong(cell.getStringCellValue().trim());
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing long value from string: " + cell.getStringCellValue());
                    return null;
                }
            case BLANK:
                return null;
            default:
                System.err.println("Unsupported cell type for long conversion: " + cell.getCellType());
                return null;
        }
    }

    private String getStringValue(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((int) cell.getNumericCellValue());
            default -> null;
        };
    }

    private int getNumericValue(Cell cell) {
        if (cell == null) return 0;
        return switch (cell.getCellType()) {
            case NUMERIC -> (int) cell.getNumericCellValue();
            case STRING -> {
                try {
                    yield Integer.parseInt(cell.getStringCellValue().trim());
                } catch (NumberFormatException e) {
                    yield 0;
                }
            }
            default -> 0;
        };
    }

    // Entity -> DTO 변환 메서드
    private CompanyItemDTO convertEntityToDTO(CompanyItem companyItem) {
        return CompanyItemDTO.builder()
                .businessId(companyItem.getCompany().getBusinessId())
                .itemId(companyItem.getItem().getId())
                .unitCost(companyItem.getUnitCost())
                .leadTime(companyItem.getLeadTime())
                .supplyUnit(companyItem.getSupplyUnit())
                .productionQty(companyItem.getProductionQty())
                .contractStatus(companyItem.getContractStatus())
                .build();
    }
}