package com.procuone.mit_kdt.repository;

import com.procuone.mit_kdt.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {
    Optional<Member> findByMemberId(String userId); // 아이디로 사용자 찾기

}
