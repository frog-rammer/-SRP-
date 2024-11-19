package com.procuone.mit_kdt.repository;

import com.procuone.mit_kdt.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {

    // MemberId로 회원 찾기
    Optional<Member> findByMemberId(String memberId);

    // 회원ID가 존재하는지 확인
    boolean existsByMemberId(String memberId);

    // UserType을 String으로 반환하도록 수정
    @Query("SELECT m.type FROM Member m WHERE m.memberId = :memberId")
    String findUserTypeByMemberId(@Param("memberId") String memberId);
}
