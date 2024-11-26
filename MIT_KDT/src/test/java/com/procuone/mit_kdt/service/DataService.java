package com.procuone.mit_kdt.service;

import com.procuone.mit_kdt.entity.Company;
import com.procuone.mit_kdt.entity.Member;
import com.procuone.mit_kdt.repository.CompanyRepository;
import com.procuone.mit_kdt.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataService {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private MemberRepository memberRepository;

    // 더미 데이터 삽입 메서드
    public void insertData() {
        // 1. 회사 정보 3개 생성
        for (int i = 1; i <= 3; i++) {
            // Company 객체 생성
            Company company = new Company(
                    "company" + i,  // 회사 ID
                    "biz_id_" + i,   // 사업자 번호
                    "회사 " + i,      // 회사명
                    "com" + i,       // 회사 고유 ID
                    "서울시 주소 " + i, // 회사 주소
                    "company" + i + "@test.com",  // 이메일
                    "010-1234-56" + i, // 전화번호
                    "결제정보 " + i,   // 결제 정보
                    "우리은행",      // 은행명
                    "123-45-67890-" + i,  // 계좌 번호
                    "담당자 " + i   // 담당자 이름
            );
            companyRepository.save(company);  // 회사 저장

            // 2. 해당 회사에 멤버 2명 추가 (협력업체)
            for (int j = 1; j <= 2; j++) {
                // 회사 정보를 기반으로 멤버 생성
                Member companyMember = new Member(
                        "user" + (i * 2 + j),  // 회원 ID
                        "회원 " + (i * 2 + j),  // 회원 이름
                        "password" + (i * 2 + j),  // 비밀번호
                        "user" + (i * 2 + j) + "@test.com",  // 이메일
                        "010-1234-56" + (i * 2 + j),  // 전화번호
                        "협력업체",  // 회원 유형
                        "DNO" + i  // 부서 번호
                );

                // Dno가 "05"인 경우 협력업체의 정보 추가
                if ("05".equals(companyMember.getDno())) {
                    // 해당 부서 번호에 해당하는 회사 정보 조회
                    Company companyInfo = companyRepository.findById("company" + i)
                            .orElseThrow(() -> new IllegalArgumentException("회사를 찾을 수 없습니다."));

                    // 회사 정보를 기반으로 멤버의 이메일, 전화번호 설정
                    companyMember.setEmail(companyInfo.getComEmail());
                    companyMember.setPhone(companyInfo.getComPhone());
                    companyMember.setType("협력업체");  // 협력업체로 설정
                }

                // 멤버 저장
                memberRepository.save(companyMember);  // 멤버 저장
            }
        }
    }
}
