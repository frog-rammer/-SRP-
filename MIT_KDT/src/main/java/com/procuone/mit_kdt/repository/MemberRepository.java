package com.procuone.mit_kdt.repository;

import com.procuone.mit_kdt.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {

    // MemberId로 회원 찾기
    Optional<Member> findByMemberId(String memberId);

    // 회원ID가 존재하는지 확인
    boolean existsByMemberId(String memberId);

    String findUserTypeByMemberId(String memberId);
}
