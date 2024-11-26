package com.procuone.mit_kdt.service;

import com.procuone.mit_kdt.dto.MemberDTO;
import com.procuone.mit_kdt.entity.Company;
import com.procuone.mit_kdt.entity.Member;
import com.procuone.mit_kdt.repository.MemberRepository;
import com.procuone.mit_kdt.repository.CompanyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private MemberService memberService;

    @Test
    void testSignup() {
        // Given: 테스트 데이터 준비
        MemberDTO memberDTO = MemberDTO.builder()
                .memberId("testMember01")
                .memberName("홍길동")
                .password("password123")
                .email("testmember01@example.com")
                .phone("010-1234-5678")
                .Dno("05")
                .type("일반회원")
                .build();

        // 회사 정보 준비 (협력업체)
        Company company = new Company();
        company.setComId("company123");
        company.setComEmail("company123@example.com");
        company.setComPhone("02-1234-5678");
        when(companyRepository.findById("company123")).thenReturn(java.util.Optional.of(company));

        // When: 회원 가입 호출
        Member member = new Member();
        member.setMemberId(memberDTO.getMemberId());
        member.setMemberName(memberDTO.getMemberName());
        member.setPassword(memberDTO.getPassword());
        member.setEmail(memberDTO.getEmail());
        member.setPhone(memberDTO.getPhone());
        member.setDno(memberDTO.getDno());
        member.setType("협력업체");

        when(memberRepository.save(any(Member.class))).thenReturn(member);

        String result = memberService.signup(memberDTO);

        // Then: 회원 가입 후 반환된 ID 확인
        assertEquals("testMember01", result);
        verify(memberRepository, times(1)).save(any(Member.class));  // save() 메소드가 호출됐는지 확인
    }
}
