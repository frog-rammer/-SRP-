package com.procuone.mit_kdt;

import com.procuone.mit_kdt.entity.BOM.CategoryTest;
import com.procuone.mit_kdt.entity.Member;
import com.procuone.mit_kdt.entity.Company;
import com.procuone.mit_kdt.repository.CategoryTestRepository;
import com.procuone.mit_kdt.repository.MemberRepository;
import com.procuone.mit_kdt.repository.CompanyRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@SpringBootTest
@ActiveProfiles("test")  // "test" 프로파일을 활성화하여 application-test.properties 설정을 사용
class MitKdtApplicationTests {

    @Autowired
    private CategoryTestRepository categoryTestRepository;

    @Autowired
    private MemberRepository memberRepository;  // Member Repository 추가
    @Autowired
    private CompanyRepository companyRepository; // Company Repository 추가

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



    // 회원 생성
    @Test
    void testInsertMembersAndCompanies() {
        insertCompanies();  // Companies 먼저 생성
        insertMembers();    // 그 후 Members 생성
    }

    // 회원 생성
    void insertMembers() {
        for (int i = 1; i <= 20; i++) {
            final int companyIndex = i;  // i를 final로 선언하여 람다식에서 참조할 수 있도록 함
            String companyId = String.valueOf(companyIndex); // i를 String으로 변환

            Company company = companyRepository.findById(companyId)
                    .orElseThrow(() -> new IllegalArgumentException("Company not found with ID: " + companyIndex));  // 해당 회사가 없으면 예외 처리

            for (int j = 1; j <= 2; j++) {
                String type;

                // 회사의 타입이 '협력업체'인 경우, type을 "협력업체"로 강제 설정
                if ("협력업체".equals(company.getCompanyType())) {
                    type = "협력업체"; // 협력업체로 고정
                } else {
                    type = getRandomType();  // 부서 타입이 "구매부서", "생산부서", "자재부서" 중 하나로 랜덤 선택
                }

                Member member = new Member(
                        "user" + ((companyIndex - 1) * 2 + j),  // memberId (회사마다 두 명씩 생성)
                        "회원 " + ((companyIndex - 1) * 2 + j),  // memberName
                        "password" + ((companyIndex - 1) * 2 + j),  // password
                        "user" + ((companyIndex - 1) * 2 + j) + "@test.com",  // email
                        "010-1234-56" + ((companyIndex - 1) * 2 + j),  // phone
                        type,  // 랜덤으로 생성한 type 또는 '협력업체'
                        company  // 회사 정보 설정
                );
                memberRepository.save(member);  // Member 저장
            }
        }
    }



    // 회사 생성
    void insertCompanies() {
        for (int i = 1; i <= 20; i++) {
            Company company = new Company(
                    "company" + i,  // companyName
                    "협력업체",  // companyType은 항상 협력업체
                    "businessId" + i,  // businessId
                    "company" + i + "Name",  // comName
                    "comId" + i,  // comId (String)
                    "address" + i,  // comAdd
                    "company" + i + "@test.com",  // comEmail
                    "010-1234-567" + i,  // comPhone
                    "은행명 " + i + " 계좌번호",  // comPaymentInfo
                    "은행명" + i,  // comBank
                    "계좌번호" + i  // comAccountNumber
            );
            companyRepository.save(company);  // Company 저장
        }
    }

    // 랜덤으로 부서 타입을 선택하는 메소드
    private String getRandomType() {
        String[] types = {"구매부서", "생산부서", "자재부서"};
        Random random = new Random();
        return types[random.nextInt(types.length)];
    }
}

