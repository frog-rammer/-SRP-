package com.procuone.mit_kdt.service;

import com.procuone.mit_kdt.entity.Company;
import com.procuone.mit_kdt.entity.Member;
import com.procuone.mit_kdt.repository.CompanyRepository;
import com.procuone.mit_kdt.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service
public class DataService {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private MemberRepository memberRepository;

    // 더미 데이터 삽입 메서드
    public void insertData() {
        // 1. 회사 정보 20개 생성
        for (int i = 1; i <= 20; i++) {
            Company company = new Company(
                    "업체 " + i,  // 회사명
                    "biz_id_" + i, // 사업자 번호
                    "company" + i, // 회사 ID
                    "업체 " + i + " 주소", // 회사 주소
                    "company" + i + "@company.com", // 회사 이메일
                    "010-1234-567" + i, // 회사 연락처
                    "은행명 " + i + " / 계좌번호 " + i, // 결제 정보
                    "은행명 " + i, // 은행명
                    "123-45-67890-" + i, // 계좌 번호
                    "담당자 " + i, // 담당자
                    "테스트 협력업체 " + i // 협력업체 이름
            );
            companyRepository.save(company);  // 회사 저장

            // 2. 해당 회사에 멤버 1명 추가 (협력업체 멤버)
            Member companyMember = new Member(
                    "user" + (i + 20),  // userId
                    "협력업체 " + i,  // 회원 이름
                    "password" + (i + 20),  // 비밀번호
                    "user" + (i + 20) + "@test.com",  // 이메일
                    "010-1234-56" + (i + 20),  // 전화번호
                    "협력업체",  // 부서 타입 (협력업체)
                    company  // 회사 정보 (협력업체에 연결)
            );
            memberRepository.save(companyMember);  // 협력업체 멤버 저장
        }

        // 3. 부서별 멤버 20명 생성 (구매부서, 생산부서, 자재부서)
        for (int i = 1; i <= 20; i++) {
            String memberType = getRandomDepartmentType();  // 랜덤 부서 타입

            Member departmentMember = new Member(
                    "user" + i,  // userId
                    "회원 " + i,  // 회원 이름
                    "password" + i,  // 비밀번호
                    "user" + i + "@test.com",  // 이메일
                    "010-1234-56" + i,  // 전화번호
                    memberType,  // 부서 타입
                    null  // 회사 정보는 null로 설정 (협력업체와는 연결되지 않음)
            );
            memberRepository.save(departmentMember);  // 부서 멤버 저장
        }
    }

    // 부서 타입을 랜덤으로 리턴 (구매부서, 생산부서, 자재부서)
    private String getRandomDepartmentType() {
        List<String> departmentTypes = Arrays.asList("구매부서", "생산부서", "자재부서");
        Random random = new Random();
        return departmentTypes.get(random.nextInt(departmentTypes.size()));  // 랜덤 부서 타입 리턴
    }
}
