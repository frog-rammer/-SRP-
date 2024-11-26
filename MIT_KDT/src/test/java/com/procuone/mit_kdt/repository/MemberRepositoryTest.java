package com.procuone.mit_kdt.repository;

import com.procuone.mit_kdt.entity.Member;
import com.procuone.mit_kdt.entity.Company;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    void testFindByMemberId() {
        // Given: 테스트 데이터 삽입
        Company company = new Company();
        company.setComName("업체 A");
        company.setBusinessId("123-45-67890-1");
        company.setComId("company1");
        company.setComManage("홍길동");
        company.setComEmail("hong@companyA.com");
        company.setComPhone("010-1111-1111");
        company.setComAdd("서울시 A구 A동");
        company.setComPaymentInfo("은행 A / 계좌번호 A");
        company.setComBank("은행 A");
        company.setComAccountNumber("111-11-11111-1");
        companyRepository.save(company);

        Member member = new Member();
        member.setMemberId("user1");
        member.setPassword("password1");
        member.setEmail(company.getComEmail());  // 회사의 이메일을 가져옴
        member.setPhone("010-1000-1001");
        member.setType("구매부서");
        member.setDno("00"); // 구매부서

        member.setMemberName(company.getComManage()); // 회사의 담당자 이름을 가져옴
        memberRepository.save(member);

        // When: 회원 조회
        Member foundMember = memberRepository.findByMemberId("user1").orElse(null);

        // Then: 결과 검증
        assertNotNull(foundMember);
        assertEquals("user1", foundMember.getMemberId());
        assertEquals("홍길동", foundMember.getMemberName());  // 회사의 담당자 이름 확인
        assertEquals("홍길동@companyA.com", foundMember.getEmail());  // 회사 이메일 확인
    }
}
