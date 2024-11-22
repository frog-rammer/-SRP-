package com.procuone.mit_kdt;

import com.procuone.mit_kdt.entity.BOM.CategoryTest;
import com.procuone.mit_kdt.repository.CategoryTestRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Value;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Arrays;

@SpringBootTest
@ActiveProfiles("test")  // "test" 프로파일을 활성화하여 application-test.properties 설정을 사용
class MitKdtApplicationTests {

    @Autowired
    private CategoryTestRepository categoryTestRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${address.file.path}")  // application-test.properties에서 경로를 동적으로 주입받음
    private String filePath;

    @Test
    void testInsertCategoriesWithTestEntity() {
        // 대분류 저장
        CategoryTest parentCategory1 = new CategoryTest(null, "완성품", "대분류", null);
        CategoryTest parentCategory2 = new CategoryTest(null, "부품", "대분류", null);
        parentCategory1 = categoryTestRepository.save(parentCategory1);
        parentCategory2 = categoryTestRepository.save(parentCategory2);

        // 중분류 저장 (부품에 속하는 카테고리들)
        CategoryTest subCategory1 = new CategoryTest(null, "프레임 및 바디", "중분류", parentCategory2);
        CategoryTest subCategory2 = new CategoryTest(null, "전기부품", "중분류", parentCategory2);
        CategoryTest subCategory3 = new CategoryTest(null, "기계부품", "중분류", parentCategory2);
        CategoryTest subCategory4 = new CategoryTest(null, "바퀴 및 이동 부품", "중분류", parentCategory2);
        CategoryTest subCategory5 = new CategoryTest(null, "구동 시스템 부품", "중분류", parentCategory2);
        CategoryTest subCategory6 = new CategoryTest(null, "안전 장치", "중분류", parentCategory2);
        CategoryTest subCategory7 = new CategoryTest(null, "킥보드 부품", "중분류", parentCategory2);

        subCategory1 = categoryTestRepository.save(subCategory1);
        subCategory2 = categoryTestRepository.save(subCategory2);
        subCategory3 = categoryTestRepository.save(subCategory3);
        subCategory4 = categoryTestRepository.save(subCategory4);
        subCategory5 = categoryTestRepository.save(subCategory5);
        subCategory6 = categoryTestRepository.save(subCategory6);
        subCategory7 = categoryTestRepository.save(subCategory7);

        // 소분류 저장 (각 중분류에 속하는 카테고리들)
        categoryTestRepository.saveAll(List.of(
                new CategoryTest(null, "알루미늄 프레임", "소분류", subCategory1),
                new CategoryTest(null, "스틸 바디 커버", "소분류", subCategory1),
                new CategoryTest(null, "알루미늄 핸들바", "소분류", subCategory1),
                new CategoryTest(null, "안장", "소분류", subCategory1),
                new CategoryTest(null, "전원 시스템", "소분류", subCategory2),
                new CategoryTest(null, "구동 모터", "소분류", subCategory2),
                new CategoryTest(null, "전자 인터페이스", "소분류", subCategory2),
                new CategoryTest(null, "조명 시스템", "소분류", subCategory2),
                new CategoryTest(null, "배터리", "소분류", subCategory2),
                new CategoryTest(null, "구동계 부품", "소분류", subCategory3),
                new CategoryTest(null, "제동 시스템", "소분류", subCategory3),
                new CategoryTest(null, "핸들 및 조향 시스템", "소분류", subCategory3),
                new CategoryTest(null, "좌석 및 탑승자 지원", "소분류", subCategory3),
                new CategoryTest(null, "페달", "소분류", subCategory3),
                new CategoryTest(null, "체인", "소분류", subCategory3)
                // 나머지 소분류 카테고리들도 비슷하게 추가
        ));
    }

    @Test
    @Transactional
    void testInsertAddressData() {
        // 경로 확인: 파일 경로가 null이나 빈 문자열이 아니라면
        String filePath = "C:/address/Gyeonggi Province.txt"; // 바꾼 파일 경로

        if (filePath == null || filePath.isEmpty()) {
            System.err.println("파일 경로가 설정되지 않았습니다.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int rowCount = 0;

            while ((line = br.readLine()) != null) {
                String[] columns = line.split("\\|");

                // 데이터가 26개의 컬럼을 가지고 있지 않다면, 부족한 컬럼에 기본값을 채웁니다.
                if (columns.length < 26) {
                    columns = Arrays.copyOf(columns, 26);
                }

                // INSERT SQL을 사용하여 데이터 삽입
                String sql = "INSERT INTO address (postal_code, province, province_english, city_district, city_district_english, " +
                        "town, town_english, road_code, road_name, road_name_english, below_ground, main_building_number, " +
                        "sub_building_number, building_management_number, bulk_delivery_name, city_district_building_name, " +
                        "legal_dong_code, legal_dong_name, ri_name, administrative_dong_name, mountain_status, land_number_main, " +
                        "town_sequence_number, land_number_sub, old_postal_code, postal_code_sequence_number) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                int rowsAffected = jdbcTemplate.update(sql, (Object[]) columns);

                if (rowsAffected > 0) {
                    System.out.println("데이터 삽입 완료: " + columns[0]);  // 삽입된 데이터 확인용
                } else {
                    System.out.println("데이터 삽입 실패: " + columns[0]);
                }

                rowCount++;
            }

            System.out.println("총 " + rowCount + "개의 데이터가 삽입되었습니다.");

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("파일 읽기 오류 발생: " + e.getMessage());
        }
    }

}
