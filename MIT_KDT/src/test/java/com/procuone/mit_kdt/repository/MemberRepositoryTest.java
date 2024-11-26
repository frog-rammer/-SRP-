package com.procuone.mit_kdt.repository;

import com.procuone.mit_kdt.entity.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void testFindByMemberId() {
        // Given: 테스트 데이터 삽입
        Member member = new Member();
        member.setMemberId("testMember01");
        member.setMemberName("홍길동");
        memberRepository.save(member);

        // When: 회원 조회
        Member foundMember = memberRepository.findByMemberId("testMember01").orElse(null);

        // Then: 결과 검증
        assertNotNull(foundMember);
        assertEquals("testMember01", foundMember.getMemberId());
        assertEquals("홍길동", foundMember.getMemberName());
    }

    @Test
    void testExistsByMemberId() {
        // Given: 테스트 데이터 삽입
        Member member = new Member();
        member.setMemberId("testMember02");
        member.setMemberName("김철수");
        memberRepository.save(member);

        // When: 회원 ID 중복 체크
        boolean exists = memberRepository.existsByMemberId("testMember02");

        // Then: 결과 검증
        assertTrue(exists);
    }
}
