package com.procuone.mit_kdt.service;

import com.procuone.mit_kdt.dto.MemberDTO;
import com.procuone.mit_kdt.entity.Member;
import com.procuone.mit_kdt.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

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

        // MemberDTO를 Member로 변환
        Member member = new Member();
        member.setMemberId(memberDTO.getMemberId());
        member.setMemberName(memberDTO.getMemberName());
        member.setPassword(memberDTO.getPassword());
        member.setEmail(memberDTO.getEmail());
        member.setPhone(memberDTO.getPhone());
        member.setDno(memberDTO.getDno());
        member.setType(memberDTO.getType());

        // When: 서비스 메서드 호출
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        // Then: 결과 검증
        String result = memberService.signup(memberDTO);
        assertEquals("testMember01", result);  // 회원가입 후 반환된 memberId와 비교

        verify(memberRepository, times(1)).save(any(Member.class));  // save() 메소드가 한번 호출됐는지 확인
    }

    @Test
    void testIsMemberIdExists() {
        // Given: 아이디가 이미 존재하는 경우
        String memberId = "testMember01";
        when(memberRepository.existsByMemberId(memberId)).thenReturn(true);

        // When: 아이디 중복 검사
        boolean exists = memberService.isMemberIdExists(memberId);

        // Then: 중복 검사 결과 검증
        assertTrue(exists);
    }
}
