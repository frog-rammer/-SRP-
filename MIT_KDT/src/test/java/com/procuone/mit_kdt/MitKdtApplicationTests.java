package com.procuone.mit_kdt;

import com.procuone.mit_kdt.entity.BOM.CategoryTest;
import com.procuone.mit_kdt.entity.Member;
import com.procuone.mit_kdt.entity.Company;
import com.procuone.mit_kdt.repository.CategoryTestRepository;
import com.procuone.mit_kdt.repository.MemberRepository;
import com.procuone.mit_kdt.repository.CompanyRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

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

    @Transactional
    @Test
    void contextLoads() {
        // 회사 및 멤버 데이터 삽입
        insertCompanies();
        insertMembers();
    }

    @Transactional
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

            // 로그 추가: 회사 데이터가 저장되기 전
            System.out.println("Saving company: " + company.getComName() + ", businessId: " + company.getBusinessId());

            companyRepository.save(company);  // 회사 저장
            companyRepository.flush();  // 즉시 DB에 반영

            // 로그 추가: DB에 반영된 후
            System.out.println("Company saved: " + company.getComName() + ", businessId: " + company.getBusinessId());
        }
    }

    @Transactional
    void insertMembers() {
        for (int i = 1; i <= 20; i++) {
            final int companyIndex = i;  // i를 final로 선언하여 람다식에서 참조할 수 있도록 함
            String companyId = "company" + companyIndex; // companyId 생성

            // 회사 정보를 DB에서 조회
            Company company = companyRepository.findById(companyId)
                    .orElseThrow(() -> new IllegalArgumentException("Company not found with ID: " + companyId));  // 해당 회사가 없으면 예외 처리

            // 로그 추가: 회사 정보가 제대로 조회됐는지 확인
            System.out.println("Found company: " + company.getComName() + ", companyId: " + company.getComId());

            // 회사마다 두 명의 멤버를 생성
            for (int j = 1; j <= 2; j++) {
                // 부서 번호 설정
                String dno = "05";  // 협력업체는 부서 번호 05로 고정

                // 협력업체로 설정할 때, 해당 회사 정보를 멤버에게 설정
                String type = "협력업체";  // 회원 유형은 협력업체로 고정

                Member member = new Member(
                        "user" + ((companyIndex - 1) * 2 + j),  // memberId (회사마다 두 명씩 생성)
                        "회원 " + ((companyIndex - 1) * 2 + j),  // memberName
                        "password" + ((companyIndex - 1) * 2 + j),  // password
                        "user" + ((companyIndex - 1) * 2 + j) + "@test.com",  // email
                        "010-1234-56" + ((companyIndex - 1) * 2 + j),  // phone
                        type,  // 회원 유형
                        dno  // 부서 번호 (협력업체는 05로 고정)
                );

                // 멤버가 협력업체일 경우, 회사의 이메일 및 전화번호를 멤버에게 설정
                if ("협력업체".equals(type)) {
                    member.setEmail(company.getComEmail());  // 회사 이메일을 멤버의 이메일로 설정
                    member.setPhone(company.getComPhone());  // 회사 전화번호를 멤버의 전화번호로 설정
                }

                // 로그 추가: 멤버 데이터가 저장되기 전
                System.out.println("Saving member: " + member.getMemberName() + ", memberId: " + member.getMemberId());

                memberRepository.save(member);  // 멤버 저장
                memberRepository.flush();  // 즉시 DB에 반영

                // 로그 추가: DB에 반영된 후
                System.out.println("Member saved: " + member.getMemberName() + ", memberId: " + member.getMemberId());
            }
        }
    }


    // 랜덤으로 부서 타입을 선택
    private String getRandomType() {
        Random random = new Random();
        String[] types = {"구매부서", "생산부서", "자재부서", "협력업체"};  // 부서 유형 배열에 협력업체 추가
        return types[random.nextInt(types.length)];
    }
}